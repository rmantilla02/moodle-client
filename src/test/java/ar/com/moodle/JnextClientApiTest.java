package ar.com.moodle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import ar.com.moodle.client.JNextClientApi;
import ar.com.moodle.client.impl.JNextClientApiImpl;
import ar.com.moodle.config.Config;
import ar.com.moodle.model.LegajoData;

public class JnextClientApiTest {

	@Test
	void testGetGTLegajosByEmpresaId() throws Exception {
		JNextClientApi client = new JNextClientApiImpl();
		List<LegajoData> result = client.getLegajosWithCertificateGT(6201);
		assertNotNull(result);
	}

	@Test
	void testGetLegajosCertificadoIS() throws Exception {
		JNextClientApi jnextClient = new JNextClientApiImpl();

		List<LegajoData> result = null;
		result = jnextClient.getLegajosWithcertificateIS(6201);
		assertNotNull(result, "legajos null");
	}

	@Test
	void testGetConfigValue() {
		String token = Config.get("moodle.token");
		assertNotNull(token, "resultado null");
	}

}
