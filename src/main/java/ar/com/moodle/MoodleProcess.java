package ar.com.moodle;

import java.time.LocalDate;
import java.util.ArrayList;
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
import ar.com.moodle.parser.CSVParser;
import ar.com.moodle.parser.DateUtils;

@Component
public class MoodleProcess {

	final static Logger logger = LogManager.getLogger(MoodleProcess.class);
	private final int ID_EMPRESA_SANATORIO = 6201;

	// Se ejecuta todos los dias a las 10am
	@Scheduled(cron = "0 0 10 * * ?")
	public void executeProcessJnext() {
		try {
			logger.info("Iniciando proceso para dar de alta a los usuarios en la plataforma de Moodle...");

			LocalDate yesterday = LocalDate.now().minusDays(1);
			List<UserData> newUsers = getNewUsersJnextByIdEmpresa(ID_EMPRESA_SANATORIO, yesterday);

			logger.info("Cantidad de usuarios a procesar: " + newUsers.size());
			this.procesarUsuarios(newUsers);

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
	 * @return
	 * @throws ExternalApiException
	 */
	private List<UserData> getNewUsersJnextByIdEmpresa(Integer idEmpresa, LocalDate date) throws ExternalApiException {
		JNextClientApi jnextClient = new JNextClientApiImpl();

		List<LegajoData> legajos = null;
		List<UserData> newUsers = new ArrayList<UserData>();
		logger.info("consultando los legajos en jnext... " + "idEmpresa: " + idEmpresa);
		try {
			legajos = jnextClient.getLegajosWithcertificateIS(idEmpresa);

			for (LegajoData legajo : legajos) {
				LocalDate dateLegajo = DateUtils.convertToLocalDate(legajo.getFechaIngreso());
				if (date.equals(dateLegajo)) {
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
				// validar usuario
				CSVParser.validateUser(user);

				CohortData cohortExist = getCohortIfExist(allCohorts, user.getSectorJn(), user.getCentroDeCostos());
				if (cohortExist == null) { // crear cohort

					cohortExist = moodleClient.createCohort(user.getSectorJn(), user.getCentroDeCostos());
					if (cohortExist == null) {
						logger.error("error al crear la cohort " + user.getSectorJn() + "-" + user.getCentroDeCostos()
								+ " para el usuario " + user.getUsername());
						continue;
					} else {
						allCohorts.add(cohortExist);
					}
				}
				// crear usuario
				moodleClient.createUser(user);
				// asignarlo a la cohort
				moodleClient.cohortAddMember(cohortExist.getIdnumber(), user.getUsername());
			} catch (ParserUserException pe) {
				logger.error("error al validar el usuario: " + user.getUsername(), pe);
			} catch (Exception ex) {
				logger.error("error inesperado procesar el usuario " + user.getUsername(), ex);
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
		CohortData result = cohortes.stream()
				.filter(c -> c.getName().contains(sectorJn) & c.getName().contains(centroDeCostos)).findFirst()
				.orElse(null);

		if (result != null) {
			logger.info("existe cohort con id: " + result.getId() + ", nombre: " + result.getName());
		}
		return result;
	}

	public void executeProcessFromFile(String confFilePath) {
		try {
			logger.info("Iniciando proceso para dar de alta a los usuarios en la plataforma de Moodle...");

			String filePath = Config.get(confFilePath);
			List<UserData> newUsers = CSVParser.parseUsers(filePath);

			logger.info("Cantidad de usuarios a procesar: " + newUsers.size());
			procesarUsuarios(newUsers);

			logger.info("fin del proceso...");
		} catch (ExternalApiException e) {
			logger.error("error al ejecutar el proceso para dar de alta los usuarios...", e);
		} catch (Exception ex) {
			logger.error("error inesperado al ejecutar el proceso para dar de alta los usuarios...", ex);
		}
	}

}
