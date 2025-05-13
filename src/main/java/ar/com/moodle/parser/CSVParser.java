package ar.com.moodle.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ar.com.moodle.exception.CSVParserException;
import ar.com.moodle.exception.ParserUserException;
import ar.com.moodle.exception.ParserValueException;
import ar.com.moodle.model.UserData;

public class CSVParser {

	private static final String SEP = ";";
	private static final String CHAR_BOB = "\uFEFF";
	private static final int NUMBER_OF_HEADER_COLUMNS = 10;
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private static final String FIRSTNAME_KEY = "firstname";
	private static final String LASTNAME_KEY = "lastname";
	private static final String EMAIL_KEY = "email";
	private static final String DNI_KEY = "dni";
	private static final String LEGAJO_ID_KEY = "legajo_id";
	private static final String SECTOR_JN_KEY = "sector_jn";
	private static final String CENTRO_DE_COSTOS_KEY = "centro_de_costos";
	private static final String PUESTO_KEY = "puesto";
	private static final String FECHA_INGRESO_KEY = "fecha_ingreso";

	private static final Logger logger = LogManager.getLogger(CSVParser.class);

	public static List<UserData> parseUsers(String filePath) throws CSVParserException {
		List<UserData> result = new ArrayList<>();
		String linea;
		int lineaNum = 0;

		if (filePath == null) {
			logger.error("error al parsear el archivo.  filePath is null");
			throw new CSVParserException("error al parsear los usuarios.  filePath is null");
		}

		logger.info("iniciando el parser del archivo {}",filePath);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
			while ((linea = br.readLine()) != null) {
				if (lineaNum == 0) {
					lineaNum++;
					validateHeader(linea);
					continue;
				}
				String[] valores = linea.split(SEP);

				UserData user = new UserData();
				user.setUsername(valores[0].replace(CHAR_BOB, "").trim());
				user.setPassword(valores[1].replace(CHAR_BOB, "").trim());
				user.setFirstname(valores[2].replace(CHAR_BOB, "").trim());
				user.setLastname(valores[3].replace(CHAR_BOB, "").trim());
				user.setEmail(valores[4].replace(CHAR_BOB, "").trim());
				validateEmail(lineaNum, user.getUsername(), user.getEmail());
				user.setDni(valores[5].replace(CHAR_BOB, "").trim());
				validateNumericValue(lineaNum, DNI_KEY, user.getDni());
				user.setLegajoId(valores[6].replace(CHAR_BOB, "").trim());
				validateNumericValue(lineaNum, LEGAJO_ID_KEY, user.getLegajoId());
				user.setSectorJn(valores[7].replace(CHAR_BOB, "").trim().toUpperCase());
				user.setCentroDeCostos(valores[8].replace(CHAR_BOB, "").trim().toUpperCase());
				user.setPuesto(valores[9].replace(CHAR_BOB, "").trim().toUpperCase());

				result.add(user);
				lineaNum++;
			}

		} catch (ParserValueException pe) {
			logger.error("Error al parsear el usuario ", pe);
			throw new CSVParserException("Error al parsear el usuario " + pe.getMessage(), pe);
		} catch (Exception ex) {
			logger.error("Error inesperado al procesar el archivo.", ex);
			throw new CSVParserException("Error inesperado al procesar el archivo. " + ex.getMessage(), ex);
		}

		return result;
	}

	private static void validateHeader(String header) throws ParserValueException {
		String[] valores = header.split(SEP);
		if (NUMBER_OF_HEADER_COLUMNS != valores.length) {
			throw new ParserValueException("error en la cantidad de columnas");
		}
		validateColum(USERNAME_KEY, valores[0].replace(CHAR_BOB, "").trim());
		validateColum(PASSWORD_KEY, valores[1].replace(CHAR_BOB, "").trim());
		validateColum(FIRSTNAME_KEY, valores[2].replace(CHAR_BOB, "").trim());
		validateColum(LASTNAME_KEY, valores[3].replace(CHAR_BOB, "").trim());
		validateColum(EMAIL_KEY, valores[4].replace(CHAR_BOB, "").trim());
		validateColum(DNI_KEY, valores[5].replace(CHAR_BOB, "").trim());
		validateColum(LEGAJO_ID_KEY, valores[6].replace(CHAR_BOB, "").trim());
		validateColum(SECTOR_JN_KEY, valores[7].replace(CHAR_BOB, "").trim());
		validateColum(CENTRO_DE_COSTOS_KEY, valores[8].replace(CHAR_BOB, "").trim());
		validateColum(PUESTO_KEY, valores[9].replace(CHAR_BOB, "").trim());
	}

	public static void validateUser(UserData user) throws ParserUserException {
		try {
			validateNumericValue(user.getUsername(), DNI_KEY, user.getDni());
			validateNumericValue(user.getLegajoId(), LEGAJO_ID_KEY, user.getLegajoId());
			validateEmail(user.getUsername(), user.getEmail());

		} catch (ParserValueException e) {
			logger.error("error al validar los datos del usuario.", e);
			throw new ParserUserException("error al validar los datos del usuario: " + user.getUsername(), e);
		}
	}

	private static void validateColum(String key, String value) throws ParserValueException {
		if (!key.equalsIgnoreCase(value)) {
			throw new ParserValueException("error en el header con la columna " + key);
		}
	}

	private static void validateNumericValue(int linea, String key, String value) throws ParserValueException {
		if (value == null || value.isEmpty() || !value.matches("\\d+"))
			throw new ParserValueException(
					"error en el registro " + linea + " de la columna " + key + ". valor: " + value);
	}

	private static void validateNumericValue(String username, String key, String value) throws ParserValueException {
		if (value == null || value.isEmpty() || !value.matches("\\d+"))
			throw new ParserValueException(
					"error con el usuario: " + username + ", parametro: " + key + ", valor: " + value);
	}

	private static void validateEmail(String username, String value) throws ParserValueException {
		if (value == null || value.isEmpty() || !value.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
			throw new ParserValueException(
					"error con el usuario: " + username + ". email incorrecto. valor:  " + value);
		}
	}

	private static void validateEmail(int linea, String username, String value) throws ParserValueException {
		if (value != null && !value.isEmpty()) {
			if (!value.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
				throw new ParserValueException(
						"error de email en el registro " + linea + ". username: " + username + ", valor: " + value);
			}
		} else {
			logger.warn("El usuario {} de la línea {} no tiene email.", username, linea);

		}

	}

	public static void exportUsersToCsv(List<UserData> users, String filePath) throws CSVParserException {
		String header = USERNAME_KEY + SEP + PASSWORD_KEY + SEP + FIRSTNAME_KEY + SEP + LASTNAME_KEY + SEP + EMAIL_KEY
				+ SEP + DNI_KEY + SEP + LEGAJO_ID_KEY + SEP + SECTOR_JN_KEY + SEP + CENTRO_DE_COSTOS_KEY + SEP
				+ PUESTO_KEY + SEP + FECHA_INGRESO_KEY;

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			writer.write(header);
			writer.newLine();

			for (UserData user : users) {
				String line = String.join(SEP, safe(user.getUsername()), safe(user.getPassword()),
						safe(user.getFirstname()), safe(user.getLastname()), safe(user.getEmail()), safe(user.getDni()),
						safe(user.getLegajoId()), safe(user.getSectorJn()), safe(user.getCentroDeCostos()),
						safe(user.getPuesto()), safe(user.getFechaIngreso()));

				writer.write(line);
				writer.newLine();
			}

		} catch (IOException e) {
			logger.error("Error al crear el archivo", e);
			throw new CSVParserException("Error al crear el archivo", e);
		} catch (Exception ex) {
			logger.error("Error inesperado al crear el archivo", ex);
			throw new CSVParserException("Error inesperado al crear el archivo. ", ex);
		}
	}

	private static String safe(Object value) {
		return value != null ? value.toString() : "";
	}

}
