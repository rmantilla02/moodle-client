package ar.com.moodle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import ar.com.moodle.client.MoodleClientApi;
import ar.com.moodle.client.impl.MoodleClientApiImpl;
import ar.com.moodle.config.Config;
import ar.com.moodle.exception.ExternalApiException;
import ar.com.moodle.model.CohortData;
import ar.com.moodle.model.UserData;

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
		user.setEmail("RMANTILLA@sanatoriocolegiales.com.ar");
		user.setDni(dni);
		user.setLegajoId("163173");
		user.setSectorJn(sectorJn);
		user.setCentroDeCostos(centroDeCostos);
		user.setPuesto("Software Developer");

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
	void testGetAllCohortes() throws ExternalApiException {
		MoodleClientApi client = new MoodleClientApiImpl();

		List<CohortData> result;
		result = client.getAllCohortes();

		assertNotNull(result, "resultado null");
	}

	@Test
	void testGetConfigValue() {
		String token = Config.get("moodle.token");
		assertNotNull(token, "resultado null");
	}

}
