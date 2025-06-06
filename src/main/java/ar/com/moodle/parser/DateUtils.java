package ar.com.moodle.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DateUtils {

	private static final Logger logger = LogManager.getLogger(DateUtils.class);

	private DateUtils() {
		throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
	}

	public static LocalDateTime convertToDateTime(String date) {
		LocalDateTime result = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			result = LocalDateTime.parse(date, formatter);
		} catch (DateTimeParseException e) {
			logger.error("error al parsear la fecha {}", date);
		}
		return result;
	}

	public static LocalDate convertToLocalDate(String date) {
		LocalDateTime localDate = convertToDateTime(date);
		if (localDate != null) {
			return localDate.toLocalDate();
		} else {
			throw new IllegalArgumentException("La fecha no pudo convertirse a LocalDate");
		}
	}

}
