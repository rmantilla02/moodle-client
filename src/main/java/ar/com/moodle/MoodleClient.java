package ar.com.moodle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class MoodleClient {

	final static Logger logger = LogManager.getLogger(MoodleClient.class);

	@Bean
	public MoodleProcess taskScheduled() {
		return new MoodleProcess();
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(3); // cantidad de hilos para ejecutar tareas
		scheduler.setThreadNamePrefix("scheduler-thread-");
		scheduler.initialize();
		return scheduler;
	}

	public static void main(String[] args) {

		try {
			logger.info("Iniciando configApplicationContext...");

			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MoodleClient.class);

		} catch (Exception e) {
			logger.error("error al iniciar el proceso ", e);
		}
	}
}

//			String filePath = Config.get("users.file.path.main");
////			String filePath = Configuration.get("users.file.path.test");
//			List<UserData> users = CSVParser.parseUsers(filePath);
//
//			MoodleClientApi moodleClient = new MoodleClientApiImpl();
//			List<CohortData> allCohorts = moodleClient.getAllCohortes();
//
//			logger.info("Cantidad de usuarios a procesar: " + users.size());
//			for (UserData user : users) {
//				try {
//					// validar usuario
//					CSVParser.validateUser(user);
//
//					CohortData cohortExist = getCohortIfExist(allCohorts, user.getSectorJn(), user.getCentroDeCostos());
//					if (cohortExist == null) { // crear cohort
//
//						cohortExist = moodleClient.createCohort(user.getSectorJn(), user.getCentroDeCostos());
//						if (cohortExist == null) {
//							logger.error("error al crear la cohort " + user.getSectorJn() + "-"
//									+ user.getCentroDeCostos() + " para el usuario " + user.getUsername());
//							continue;
//						} else {
//							allCohorts.add(cohortExist);
//						}
//					}
//
//					// crear usuario
//					moodleClient.createUser(user);
//
//					// asignarlo a la cohort
//					moodleClient.cohortAddMember(cohortExist.getIdnumber(), user.getUsername());
//
//				} catch (ParserUserException pe) {
//					logger.error("error al validar el usuario: " + user.getUsername(), pe);
//				} catch (Exception ex) {
//					logger.error("error inesperado procesar el usuario " + user.getUsername(), ex);
//				}
//			}
//
//			logger.info("Fin del proceso... ");
//		} catch (Exception e) {
//			logger.error("error inesperado al procesar los usuarios", e.getMessage());
//		}
//	}
//
//	private static CohortData getCohortIfExist(List<CohortData> cohortes, String sectorJn, String centroDeCostos) {
//		CohortData result = cohortes.stream()
//				.filter(c -> c.getName().contains(sectorJn) & c.getName().contains(centroDeCostos)).findFirst()
//				.orElse(null);
//
//		if (result != null) {
//			logger.info("existe cohort con id: " + result.getId() + ", nombre: " + result.getName());
//		}
//		return result;
//	}

//}