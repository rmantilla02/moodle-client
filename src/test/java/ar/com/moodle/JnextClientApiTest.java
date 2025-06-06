package ar.com.moodle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import ar.com.moodle.client.JNextClientApi;
import ar.com.moodle.client.impl.JNextClientApiImpl;
import ar.com.moodle.model.LegajoData;

class JnextClientApiTest {

	@Test
	void testGetLegajosCertificadoIS() throws Exception {
		JNextClientApi jnextClient = new JNextClientApiImpl();

		List<LegajoData> result = null;
		result = jnextClient.getLegajosWithcertificateIS(6201);
		assertNotNull(result, "legajos null");
	}

}
