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
//			MoodleProcess process = new MoodleProcess();
//			process.executeProcess();

		} catch (Exception e) {
			logger.error("error al iniciar el proceso ", e);
		}
	}
}