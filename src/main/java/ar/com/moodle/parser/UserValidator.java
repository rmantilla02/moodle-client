package ar.com.moodle.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import ar.com.moodle.exception.ParserUserException;
import ar.com.moodle.exception.ParserValueException;
import ar.com.moodle.model.UserData;

public class UserValidator {

	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private static final String FIRSTNAME_KEY = "firstname";
	private static final String LASTNAME_KEY = "lastname";
	private static final String DNI_KEY = "dni";
	private static final String LEGAJO_ID_KEY = "legajo_id";

	private static final Logger logger = LogManager.getLogger(UserValidator.class);

	private UserValidator() {
		throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
	}

	public static void validateUser(UserData user) throws ParserUserException {
		try {
			validateNumericValue(user.getUsername(), USERNAME_KEY, user.getUsername());
			validateNumericValue(user.getUsername(), PASSWORD_KEY, user.getPassword());
			validateStrValue(user.getUsername(), FIRSTNAME_KEY, user.getFirstname());
			validateStrValue(user.getUsername(), LASTNAME_KEY, user.getLastname());
			validateNumericValue(user.getUsername(), DNI_KEY, user.getDni());
			validateNumericValue(user.getLegajoId(), LEGAJO_ID_KEY, user.getLegajoId());
			validateEmail(user.getUsername(), user.getEmail());

		} catch (ParserValueException e) {
			logger.error("error al validar los datos del usuario.", e);
			throw new ParserUserException("error al validar los datos del usuario: " + user.getUsername(), e);
		}
	}

	private static void validateNumericValue(String username, String key, String value) throws ParserValueException {
		if (value == null || value.isEmpty() || !value.matches("\\d+"))
			throw new ParserValueException(
					"error con el usuario: " + username + ", parametro: " + key + ", valor: " + value);
	}

	private static void validateStrValue(String username, String key, String value) throws ParserValueException {
		if (!StringUtils.hasText(value))
			throw new ParserValueException(
					"error con el usuario: " + username + ", parametro: " + key + ", valor: " + value);
	}

	private static void validateEmail(String username, String value) throws ParserValueException {
		if (value == null || value.isEmpty() || !value.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
			throw new ParserValueException("error con el usuario: " + username + ". email incorrecto, valor: " + value);
		}
	}

}
