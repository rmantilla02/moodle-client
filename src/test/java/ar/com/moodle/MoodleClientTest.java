package ar.com.moodle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ar.com.moodle.client.MoodleClientApi;
import ar.com.moodle.client.impl.MoodleClientApiImpl;
import ar.com.moodle.config.Config;
import ar.com.moodle.exception.ExternalApiException;
import ar.com.moodle.model.CohortData;
import ar.com.moodle.model.UserData;
import ar.com.moodle.parser.CSVParser;

class MoodleClientTest {

	@Test
	void testCreateUserOk() throws Exception {
		MoodleClientApi client = new MoodleClientApiImpl();

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
	}

	@Test
	void testCreateCohort() throws Exception {
		MoodleClientApi client = new MoodleClientApiImpl();

		String sectorJn = "At. al PacientePrueba2";
		String centroDeCostos = "OperacionesPrueba2";

		CohortData createResult = client.createCohort(sectorJn, centroDeCostos);

		assertNotNull(createResult);

	}

	@Test
	void testParsearUsuarios() throws Exception {

		List<UserData> result = null;
		String path = Config.get("users.file.path.test");
		result = CSVParser.parseUsers(path);

		System.out.println("Cantidad de usuarios: " + result.size());

		Assertions.assertNotNull(result, "resultado null");
	}

	@Test
	void testCreateUsersFormFile() throws Exception {

		MoodleClientApi moodleClient = new MoodleClientApiImpl();
		List<UserData> users = null;
		String path = Config.get("users.file.path.test");
		users = CSVParser.parseUsers(path);
		for (UserData user : users) {
			moodleClient.createUser(user);
		}

		assertNotNull(users, "resultado null");
	}

	@Test
	void testGetAllCourses() throws ExternalApiException {
		MoodleClientApi client = new MoodleClientApiImpl();

		String result;
		result = client.getAllCourses();
		assertNotNull("resultado null", result);
	}

	@Test
	void testGetAllCohortes() throws ExternalApiException {
		MoodleClientApi client = new MoodleClientApiImpl();

		List<CohortData> result;
		result = client.getAllCohortes();

		assertNotNull(result, "resultado null");
	}

}
