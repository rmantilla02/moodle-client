package ar.com.moodle.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ar.com.moodle.exception.CSVParserException;
import ar.com.moodle.exception.ParserUserException;
import ar.com.moodle.exception.ParserValueException;
import ar.com.moodle.model.UserData;

public class CSVParser {

	private static String SEP = ";";
	private static String CHAR_BOB = "\uFEFF";
	private static int CANT_COLUM_HEADER = 10;
	private static String KEY_USERNAME = "username";
	private static String KEY_PASSWORD = "password";
	private static String KEY_FIRSTNAME = "firstname";
	private static String KEY_LASTNAME = "lastname";
	private static String KEY_EMAIL = "email";
	private static String KEY_DNI = "dni";
	private static String KEY_LEGAJO_ID = "legajo_id";
	private static String KEY_SECTOR_JN = "sector_jn";
	private static String KEY_CENTRO_DE_COSTOS = "centro_de_costos";
	private static String KEY_PUESTO = "puesto";
	private static String KEY_FECHA_INGRESO = "fecha_ingreso";

	private static final Logger logger = LogManager.getLogger(CSVParser.class);

	public static List<UserData> parseUsers(String filePath) throws CSVParserException {
		List<UserData> result = new ArrayList<UserData>();
		String linea;
		int lineaNum = 0;

		if (filePath == null) {
			logger.error("error al parsear el archivo.  filePath is null");
			throw new CSVParserException("error al parsear los usuarios.  filePath is null");
		}

		logger.info("iniciando el parser del archivo: " + filePath);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
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
				validateNumericValue(lineaNum, KEY_DNI, user.getDni());
				user.setLegajoId(valores[6].replace(CHAR_BOB, "").trim());
				validateNumericValue(lineaNum, KEY_LEGAJO_ID, user.getLegajoId());
				user.setSectorJn(valores[7].replace(CHAR_BOB, "").trim().toUpperCase());
				user.setCentroDeCostos(valores[8].replace(CHAR_BOB, "").trim().toUpperCase());
				user.setPuesto(valores[9].replace(CHAR_BOB, "").trim().toUpperCase());

				result.add(user);
				lineaNum++;
			}

		} catch (ParserValueException pe) {
			logger.error("Error al parsear el usuario " + pe.getMessage(), pe);
			throw new CSVParserException("Error al parsear el usuario " + pe.getMessage(), pe);
		} catch (Exception ex) {
			logger.error("Error inesperado al procesar el archivo. " + ex.getMessage());
			throw new CSVParserException("Error inesperado al procesar el archivo. " + ex.getMessage(), ex);
		}

		return result;
	}

	private static void validateHeader(String header) throws ParserValueException {
		String[] valores = header.split(SEP);
		if (CANT_COLUM_HEADER != valores.length) {
			throw new ParserValueException("error en la cantidad de columnas");
		}
		validateColum(KEY_USERNAME, valores[0].replace(CHAR_BOB, "").trim());
		validateColum(KEY_PASSWORD, valores[1].replace(CHAR_BOB, "").trim());
		validateColum(KEY_FIRSTNAME, valores[2].replace(CHAR_BOB, "").trim());
		validateColum(KEY_LASTNAME, valores[3].replace(CHAR_BOB, "").trim());
		validateColum(KEY_EMAIL, valores[4].replace(CHAR_BOB, "").trim());
		validateColum(KEY_DNI, valores[5].replace(CHAR_BOB, "").trim());
		validateColum(KEY_LEGAJO_ID, valores[6].replace(CHAR_BOB, "").trim());
		validateColum(KEY_SECTOR_JN, valores[7].replace(CHAR_BOB, "").trim());
		validateColum(KEY_CENTRO_DE_COSTOS, valores[8].replace(CHAR_BOB, "").trim());
		validateColum(KEY_PUESTO, valores[9].replace(CHAR_BOB, "").trim());
	}

	public static void validateUser(UserData user) throws ParserUserException {
		try {
			validateNumericValue(user.getUsername(), KEY_DNI, user.getDni());
			validateNumericValue(user.getLegajoId(), KEY_LEGAJO_ID, user.getLegajoId());
			validateEmail(user.getUsername(), user.getEmail());

		} catch (ParserValueException e) {
			logger.error("error al validar los datos del usuario." + " - " + e.getMessage());
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
			logger.warn("el usuario " + username + " de la linea: " + linea + " no tiene email.");
		}

	}

	public static void exportUsersToCsv(List<UserData> users, String filePath) throws CSVParserException {
//		String header = "username;password;firstname;lastname;email;dni;legajo_id;sector_jn;centro_de_costos;puesto";
		String header = KEY_USERNAME + SEP + KEY_PASSWORD + SEP + KEY_FIRSTNAME + SEP + KEY_LASTNAME + SEP + KEY_EMAIL
				+ SEP + KEY_DNI + SEP + KEY_LEGAJO_ID + SEP + KEY_SECTOR_JN + SEP + KEY_CENTRO_DE_COSTOS + SEP
				+ KEY_PUESTO + SEP + KEY_FECHA_INGRESO;

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
			logger.error("Error inesperado al crear el archivo. " + ex.getMessage());
			throw new CSVParserException("Error inesperado al crear el archivo. ", ex);
		}
	}

	private static String safe(Object value) {
		return value != null ? value.toString() : "";
	}

}
