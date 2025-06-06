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

	private static final Logger logger = LogManager.getLogger(MoodleClient.class);

	@Bean
	public MoodleProcess taskScheduled() {
		return new MoodleProcess();
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(3);
		scheduler.setThreadNamePrefix("scheduler-thread-");
		scheduler.initialize();
		return scheduler;
	}

	public static void main(String[] args) {

		logger.info("Iniciando el proceso para dar de alta a los usuarios en la plataforma de Moodle...");
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MoodleClient.class)) {
			Thread.currentThread().join();
		} catch (Exception e) {
			logger.error("error al iniciar el proceso ", e);
		}
	}

}