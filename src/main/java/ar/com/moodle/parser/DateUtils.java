package ar.com.moodle.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DateUtils {

	private static final Logger logger = LogManager.getLogger(DateUtils.class);

	public static LocalDateTime convertToDateTime(String date) {
		LocalDateTime result = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			result = LocalDateTime.parse(date, formatter);
		} catch (DateTimeParseException e) {
			logger.error("error al parsear la fecha: " + date);
		}
		return result;
	}

	public static LocalDate convertToLocalDate(String date) {
		return convertToDateTime(date).toLocalDate();
	}
	
	public static long daysBetween(LocalDate from, LocalDate to) {
        if (from == null || to == null) return 0;
        return ChronoUnit.DAYS.between(from, to);
    }
	
}
