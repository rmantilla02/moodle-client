package ar.com.moodle.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ar.com.moodle.exception.CSVParserException;
import ar.com.moodle.exception.ParserValueException;
import ar.com.moodle.model.UserData;

public class CSVParser {

	private static String SEPARATOR = ";";
	private static int CANT_COLUM_HEADER = 10;
	private static String KEY_USERNAME = "username";
	private static String KEY_PASSWORD = "password";
	private static String KEY_FIRSTNAME = "firstname";
	private static String KEY_LASTNAME = "lastname";
	private static String KEY_EMAIL = "email";
	private static String KEY_DNI = "dni";
	private static String KEY_LEGAJO_ID = "legajo_id";
	private static String KEY_SECTOR_JN = "sector_jn";
	private static String KEY_PUESTO = "puesto";
	private static String KEY_CENTRO_DE_COSTOS = "centro_de_costos";

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
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			while ((linea = br.readLine()) != null) {
				if (lineaNum == 0) {
					lineaNum++;
					validateHeader(linea);
					continue;
				}
				String[] valores = linea.split(SEPARATOR);

				UserData user = new UserData();
				user.setUsername(valores[0]);
				user.setPassword(valores[1]);
				user.setFirstname(valores[2]);
				user.setLastname(valores[3]);
				user.setEmail(valores[4]);
				validateEmail(lineaNum, KEY_EMAIL, user.getEmail());
				user.setDni(valores[5]);
				validateValueNumeric(lineaNum, KEY_DNI, user.getDni());
				user.setLegajoId(valores[6]);
				validateValueNumeric(lineaNum, KEY_LEGAJO_ID, user.getLegajoId());
				user.setSectorJn(valores[7].toUpperCase());
				user.setCentroDeCostos(valores[8].toUpperCase());
				user.setPuesto(valores[9].toUpperCase());

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
		String[] valores = header.split(SEPARATOR);
		if (CANT_COLUM_HEADER != valores.length) {
			throw new ParserValueException("error en la cantidad de columnas");
		}
		validateColum(KEY_USERNAME, valores[0]);
		validateColum(KEY_PASSWORD, valores[1]);
		validateColum(KEY_FIRSTNAME, valores[2]);
		validateColum(KEY_LASTNAME, valores[3]);
		validateColum(KEY_EMAIL, valores[4]);
		validateColum(KEY_DNI, valores[5]);
		validateColum(KEY_LEGAJO_ID, valores[6]);
		validateColum(KEY_SECTOR_JN, valores[7]);
		validateColum(KEY_CENTRO_DE_COSTOS, valores[8]);
		validateColum(KEY_PUESTO, valores[9]);
	}

	private static void validateColum(String key, String value) throws ParserValueException {
		if (!key.equalsIgnoreCase(value)) {
			throw new ParserValueException("error en el header con la columna " + key);
		}
	}

	private static void validateValueNumeric(int linea, String key, String value) throws ParserValueException {
		if (value == null || value.isEmpty() || !value.matches("\\d+"))
			throw new ParserValueException(
					"error en el registro " + linea + " de la columna " + key + ". valor: " + value);
	}

	private static void validateEmail(int linea, String key, String value) throws ParserValueException {
		if (value == null || value.isEmpty() || !value.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
			throw new ParserValueException(
					"error en el registro " + linea + " de la columna " + key + ". valor: " + value);
	}

}
