package ar.com.moodle.config;

import java.io.InputStream;
import java.util.Properties;

public class Config {

	private static final Properties props = new Properties();

	static {
		try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
			if (input == null) {
				throw new RuntimeException("No se pudo encontrar el archivo config.properties");
			}
			props.load(input);
			// Reemplazar variables del tipo ${VAR_ENV} por su valor real
			for (String name : props.stringPropertyNames()) {
				String value = props.getProperty(name);
				if (value != null && value.matches("\\$\\{.+}")) {
					String envVar = value.substring(2, value.length() - 1);
					String envValue = System.getenv(envVar);
					if (envValue != null) {
						props.setProperty(name, envValue);
					} else {
						throw new RuntimeException("La variable de entorno " + envVar + " no está definida");
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error cargando configuración: " + e.getMessage(), e);
		}
	}

	public static String get(String key) {
		return props.getProperty(key);
	}
}
