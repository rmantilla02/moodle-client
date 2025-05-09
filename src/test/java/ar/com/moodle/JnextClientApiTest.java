package ar.com.moodle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ar.com.moodle.client.JNextClientApi;
import ar.com.moodle.client.impl.JNextClientApiImpl;
import ar.com.moodle.config.Config;
import ar.com.moodle.model.LegajoData;
import ar.com.moodle.model.UserData;
import ar.com.moodle.parser.CSVParser;
import ar.com.moodle.parser.DateUtils;

public class JnextClientApiTest {

	@Test
	public void testGetGTLegajosByEmpresaId() throws Exception {
		JNextClientApi client = new JNextClientApiImpl();
		List<LegajoData> result = client.getLegajosWithCertificateGT(6201);
		assertNotNull(result);
	}

	@Test
	public void testGetLegajosCertificadoIS() throws Exception {
		JNextClientApi jnextClient = new JNextClientApiImpl();

		List<LegajoData> result = null;
		result = jnextClient.getLegajosWithcertificateIS(6201);
		assertNotNull(result, "legajos null");
	}

	@Test
	public void testGenerateFileFromLegajosJNext() throws Exception {
		JNextClientApi jnextClient = new JNextClientApiImpl();

		List<UserData> newUsers = new ArrayList<UserData>();
		List<LegajoData> legajos = jnextClient.getLegajosWithcertificateIS(6201);
		LocalDate now = LocalDate.now();
		newUsers = legajos.stream().filter(legajo -> {
			LocalDate dateLegajo = DateUtils.convertToLocalDate(legajo.getFechaIngreso());
			return DateUtils.daysBetween(dateLegajo, now) < 5;
		}).map(LegajoData::mapperToUserData).collect(Collectors.toList());
		assertNotNull(newUsers, "users null");
		String filePath = Config.get("jnext.file.path.new.users");
		CSVParser.exportUsersToCsv(newUsers, filePath);
	}

	@Test
	public void testGetConfigValue() {
		String token = Config.get("moodle.token");
		assertNotNull(token, "resultado null");
	}

}
