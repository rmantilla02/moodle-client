package ar.com.moodle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ar.com.moodle.config.Config;
import ar.com.moodle.jnext.client.JNextClientApi;
import ar.com.moodle.jnext.client.JNextClientApiImpl;
import ar.com.moodle.model.LegajoData;
import ar.com.moodle.model.UserData;
import ar.com.moodle.parser.CSVParser;
import ar.com.moodle.parser.DateUtils;

public class JnextClientApiTest {

	@Test
	public void testGetGTLegajosByEmpresaId() {
		JNextClientApi client = new JNextClientApiImpl();
		try {
			List<LegajoData> result = client.getLegajosWithCertificateGT(6201);
			assertNotNull(result);
		} catch (Exception e) {
			assertNotNull(null);
		}
	}

	@Test
	public void testGetLegajosCertificadoIS() {
		JNextClientApi jnextClient = new JNextClientApiImpl();

		List<LegajoData> result = null;
		try {
			result = jnextClient.getLegajosWithcertificateIS(6201);
			assertNotNull(result, "legajos null");
		} catch (Exception e) {
			assertNotNull(null);
		}
	}

	@Test
	public void testGenerateFileFromLegajos() {
		JNextClientApi jnextClient = new JNextClientApiImpl();

		List<LegajoData> legajos = null;
		List<UserData> users = new ArrayList<UserData>();
		try {
			legajos = jnextClient.getLegajosWithcertificateIS(6201);
//			LocalDate now = LocalDate.now();
			LocalDate yesterday = LocalDate.now().minusDays(1);
			for (LegajoData legajo : legajos) {
				LocalDate dateAux = DateUtils.convertToLocalDate(legajo.getFechaIngreso());
				if (DateUtils.daysBetween(dateAux, yesterday) < 45) {
					users.add(legajo.mapperToUserData());
				}
			}
			assertNotNull(users, "users null");
			String filePath = Config.get("jnext.file.path.new.users");
			CSVParser.exportUsersToCsv(users, filePath);
		} catch (Exception e) {
			assertNotNull(null);
		}
	}

	@Test
	public void testGenerateFileFromLegajosJNext() {
		JNextClientApi jnextClient = new JNextClientApiImpl();

		List<LegajoData> legajos = null;
		List<UserData> newUsers = new ArrayList<UserData>();
		try {
			legajos = jnextClient.getLegajosWithcertificateIS(6201);
			LocalDate now = LocalDate.now();
			newUsers = legajos.stream().filter(legajo -> {
				LocalDate dateLegajo = DateUtils.convertToLocalDate(legajo.getFechaIngreso());
				return DateUtils.daysBetween(dateLegajo, now) < 5;
			}).map(LegajoData::mapperToUserData).collect(Collectors.toList());
			assertNotNull(newUsers, "users null");
			String filePath = Config.get("jnext.file.path.new.users");
			CSVParser.exportUsersToCsv(newUsers, filePath);
		} catch (Exception e) {
			assertNotNull(null);
		}
	}

	@Test
	public void testGetConfigValue() {
		String token = Config.get("moodle.token");
		assertNotNull(token, "resultado null");
	}

	@Test
	public void testParseDateStr() {
		String fechaStr = "2012-08-01T00:00:00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(fechaStr, formatter);

		LocalDate now = LocalDate.now();
		if (DateUtils.daysBetween(dateTime.toLocalDate(), now) < 30) {
			System.out.println("ok");
		}

		LocalDate date = LocalDateTime.parse(fechaStr).toLocalDate();
		assertNotNull(date, "date null");
		System.out.println(date);
	}

}
