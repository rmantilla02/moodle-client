package ar.com.moodle;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ar.com.moodle.client.MoodleClientApi;
import ar.com.moodle.client.impl.MoodleClientApiImpl;
import ar.com.moodle.config.Configuration;
import ar.com.moodle.model.CohortData;
import ar.com.moodle.model.UserData;
import ar.com.moodle.parser.CSVParser;

public class MoodleClient {
	final static Logger logger = LogManager.getLogger(MoodleClient.class);

	public static void main(String[] args) {

		try {
			logger.info("Iniciando proceso para crear los usuarios en la plataforma de Moodle...");

			String filePath = Configuration.get("users.file.path.main");
//			String filePath = Configuration.get("users.file.path.test");
			List<UserData> users = CSVParser.parseUsers(filePath);

			MoodleClientApi moodleCliet = new MoodleClientApiImpl();
			List<CohortData> allCohorts = moodleCliet.getAllCohortes();

			logger.info("Cantidad de usuarios a procesar: " + users.size());
			for (UserData user : users) {
				try {
					CohortData cohortExist = getCohortIfExist(allCohorts, user.getSectorJn(), user.getCentroDeCostos());
					if (cohortExist == null) { // crear la cohort

						cohortExist = moodleCliet.createCohort(user.getSectorJn(), user.getCentroDeCostos());
						if (cohortExist == null) {
							logger.error("error al crear la cohort " + user.getSectorJn() + "-"
									+ user.getCentroDeCostos() + " para el usuario " + user.getUsername());
							continue;
						} else {
							allCohorts.add(cohortExist);
						}
					}

					// crear usuario
					moodleCliet.createUser(user);

					// asignarlo a la cohort
					moodleCliet.cohortAddMember(cohortExist.getIdnumber(), user.getUsername());
				} catch (Exception e) {
					logger.error("error inesperado procesar el usuario " + user.getUsername(), e.getMessage());
				}
			}

			logger.info("Fin del proceso... ");
		} catch (Exception e) {
			logger.error("error inesperado al procesar los usuarios", e.getMessage());
		}
	}

	private static CohortData getCohortIfExist(List<CohortData> cohortes, String sectorJn, String centroDeCostos) {
		CohortData result = cohortes.stream()
				.filter(c -> c.getName().contains(sectorJn) & c.getName().contains(centroDeCostos)).findFirst()
				.orElse(null);

		if (result != null) {
			logger.info("cohorte id: " + result.getId() + ", nombre: " + result.getName());
		}
		return result;
	}

}