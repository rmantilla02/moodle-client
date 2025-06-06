package ar.com.moodle;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ar.com.moodle.client.JNextClientApi;
import ar.com.moodle.client.MoodleClientApi;
import ar.com.moodle.client.impl.JNextClientApiImpl;
import ar.com.moodle.client.impl.MoodleClientApiImpl;
import ar.com.moodle.config.Config;
import ar.com.moodle.exception.ExternalApiException;
import ar.com.moodle.exception.ParserUserException;
import ar.com.moodle.model.CohortData;
import ar.com.moodle.model.LegajoData;
import ar.com.moodle.model.UserData;
import ar.com.moodle.parser.UserValidator;
import ar.com.moodle.parser.DateUtils;

@Component
public class MoodleProcess {

	private static final Logger logger = LogManager.getLogger(MoodleProcess.class);

	// Se ejecuta todos los dias a las 10am
	@Scheduled(cron = "0 0 10 * * ?")
	public void executeProcessJnext() {
		try {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			logger.info("iniciando el proceso con fechaIngreso {}...", yesterday);

			String strEmpresas = Config.get("ids.empresas");
			List<Integer> idsEmpresas = Arrays.stream(strEmpresas.split(",")).map(String::trim).map(Integer::valueOf)
					.toList();

			for (Integer idEmpresa : idsEmpresas) {
				logger.info("procesando idEmpresa {}...", idEmpresa);

				List<UserData> newUsers = getNewUsersJnextByIdEmpresa(idEmpresa, yesterday);
				logger.info("Cantidad de usuarios a procesar  {}", newUsers.size());

				if (!newUsers.isEmpty())
					this.procesarUsuarios(newUsers);
			}

			logger.info("fin del proceso...");
		} catch (ExternalApiException e) {
			logger.error("error al ejecutar el proceso para dar de alta los usuarios...", e);
		} catch (Exception ex) {
			logger.error("error inesperado al ejecutar el proceso para dar de alta los usuarios...", ex);
		}
	}

	/**
	 * 
	 * @param idEmpresa
	 * @return los usuarios a dar de alta en la plataforma de moodle
	 * @throws ExternalApiException
	 */
	private List<UserData> getNewUsersJnextByIdEmpresa(Integer idEmpresa, LocalDate fechaIngreso)
			throws ExternalApiException {
		JNextClientApi jnextClient = new JNextClientApiImpl();

		List<LegajoData> legajos = null;
		List<UserData> newUsers = new ArrayList<>();
		logger.info("consultando legajos en jnext con idEmpresa {} y fechaIngreso {}", idEmpresa, fechaIngreso);
		try {
			legajos = jnextClient.getLegajosWithcertificateIS(idEmpresa);

			for (LegajoData legajo : legajos) {
				LocalDate dateLegajo = DateUtils.convertToLocalDate(legajo.getFechaIngreso());
				if (fechaIngreso.equals(dateLegajo)) {
					newUsers.add(legajo.mapperToUserData());
				}
			}

			return newUsers;

		} catch (Exception e) {
			logger.error("error al consultar los usuarios en jnext", e);
			throw new ExternalApiException("error al consultar los usuarios en jnex", e);
		}
	}

	/**
	 * 
	 * @param newUsers
	 * @throws ExternalApiException
	 */
	private void procesarUsuarios(List<UserData> newUsers) throws ExternalApiException {
		MoodleClientApi moodleClient = new MoodleClientApiImpl();
		List<CohortData> allCohorts = moodleClient.getAllCohortes();
		for (UserData user : newUsers) {
			try {
				logger.info("procesando el usuario {}... ", user.getUsername());
				UserValidator.validateUser(user);

				CohortData cohortExist = getCohortIfExist(allCohorts, user.getSectorJn(), user.getCentroDeCostos());
				if (cohortExist == null) { // crear cohort

					cohortExist = moodleClient.createCohort(user.getSectorJn(), user.getCentroDeCostos());
					if (cohortExist == null) {
						logger.error("error al crear la cohort {}-{} para el usuario {}", user.getSectorJn(),
								user.getCentroDeCostos(), user.getUsername());
						continue;
					} else {
						allCohorts.add(cohortExist);
					}
				}
				moodleClient.createUser(user);
				// asignarlo a la cohort
				moodleClient.cohortAddMember(cohortExist.getIdnumber(), user.getUsername());
			} catch (ParserUserException pe) {
				logger.error("error al validar el usuario: {}", user.getUsername(), pe);
			} catch (Exception ex) {
				logger.error("error inesperado procesar el usuario {}", user.getUsername(), ex);
			}
		}
	}

	/**
	 * 
	 * @param cohortes
	 * @param sectorJn
	 * @param centroDeCostos
	 * @return
	 */
	private static CohortData getCohortIfExist(List<CohortData> cohortes, String sectorJn, String centroDeCostos) {
		CohortData result = cohortes.stream().filter(c -> {
			String name = normalize(c.getName());
			return name.contains(normalize(sectorJn)) && name.contains(normalize(centroDeCostos));
		}).findFirst().orElse(null);

		if (result != null) {
			logger.info("existe cohort con id: {} , nombre {} ", result.getId(), result.getName());
		}
		return result;
	}

	private static String normalize(String input) {
		if (input == null)
			return null;
		String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
		return normalized.replaceAll("\\p{M}", "").toLowerCase();
	}

}
