package ar.com.moodle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import ar.com.moodle.config.Configuration;
import ar.com.moodle.jnext.client.JNextClientApi;
import ar.com.moodle.jnext.client.JNextClientApiImpl;
import ar.com.moodle.model.LegajoData;

public class JnextClientApiTest {

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
			result = client.getLegajosWithcertificateIS(6201);
			assertNotNull(result, "legajos null");
		} catch (Exception e) {
			assertNotNull(null);
		}
	}

	@Test
	public void testGetConfigValue() {
		String token = Configuration.get("moodle.token");
		assertNotNull(token, "resultado null");
	}
}
