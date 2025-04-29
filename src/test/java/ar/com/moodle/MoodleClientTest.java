package ar.com.moodle;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import ar.com.moodle.client.MoodleClientApi;
import ar.com.moodle.client.impl.MoodleClientApiImpl;
import ar.com.moodle.config.Configuration;
import ar.com.moodle.exception.CSVParserException;
import ar.com.moodle.exception.ExternalApiException;
import ar.com.moodle.jnext.client.JNextClientApi;
import ar.com.moodle.jnext.client.JNextClientApiImpl;
import ar.com.moodle.model.CohortData;
import ar.com.moodle.model.LegajoData;
import ar.com.moodle.model.UserData;
import ar.com.moodle.parser.CSVParser;

public class MoodleClientTest {

	@Test
	public void testCreateUser() {
		MoodleClientApi client = new MoodleClientApiImpl();

		try {
			String dni = "11222333";
			String sectorJn = "At. al PacientePrueba";
			String centroDeCostos = "OperacionesPrueba";

			UserData user = new UserData();
			user.setUsername(dni);
			user.setPassword(dni);
			user.setFirstname("Roger");
			user.setLastname("Prueba");
			user.setEmail("RMANTILLA02@GMAIL.COM");
			user.setDni(dni);
			user.setLegajoId("163173");
			user.setSectorJn(sectorJn);
			user.setCentroDeCostos(centroDeCostos);
			user.setPuesto("Tecnico/a");

			Integer result = client.createUser(user);
			assertNotNull(result);
		} catch (Exception e) {
			assertNotNull(null);
		}

	}

	@Test
	public void testCreateCohort() {
		MoodleClientApi client = new MoodleClientApiImpl();

		try {
			String sectorJn = "At. al PacientePrueba2";
			String centroDeCostos = "OperacionesPrueba2";

			CohortData createResult = client.createCohort(sectorJn, centroDeCostos);

			assertNotNull(createResult);

		} catch (Exception e) {
			assertNotNull(null);
		}

	}

	@Test
	public void testParsearUsuarios() {

		List<UserData> result = null;
		String path = Configuration.get("users.file.path.test");

		try {
			result = CSVParser.parseUsers(path);
		} catch (Exception e) {

		}
		System.out.println("Cantidad de usuarios: " + result.size());

		assertNotNull("resultado null", result);
	}

	@Test
	public void testCreateUsersFormFile() throws ExternalApiException, CSVParserException {

		MoodleClientApi moodleClient = new MoodleClientApiImpl();
		List<UserData> users = null;
		String path = Configuration.get("users.file.path.test");
		users = CSVParser.parseUsers(path);
		for (UserData user : users) {
			moodleClient.createUser(user);
		}

		assertNotNull("resultado null", users);
	}

	@Test
	public void testGetAllCourses() throws ExternalApiException {
		MoodleClientApi client = new MoodleClientApiImpl();

		String result;
		result = client.getAllCourses();
		assertNotNull("resultado null", result);
	}

	@Test
	public void testGetAllCohortes() throws ExternalApiException {
		MoodleClientApi client = new MoodleClientApiImpl();

		List<CohortData> result;
		result = client.getAllCohortes();

		assertNotNull("resultado null", result);
	}

	@Test
	public void testGetGTLegajosByEmpresaId() {
		JNextClientApi client = new JNextClientApiImpl();
		try {
			List<LegajoData> result = client.getGTLegajosByEmpresaId(6201);
			assertNotNull(result);
		} catch (Exception e) {
			assertNotNull(null);
		}
	}

	@Test
	public void testGetLegajosCertificadoIS() {
		JNextClientApi client = new JNextClientApiImpl();

		List<LegajoData> result = null;
		try {
			result = client.getLegajosCertificadoIS(6201);
		} catch (Exception e) {
			assertNotNull(null);
		}

		System.out.println(result);
	}

	@Test
	public void testGetConfigValue() {
		String token = Configuration.get("moodle.token");
		assertNotNull("resultado null", token);
	}

}
