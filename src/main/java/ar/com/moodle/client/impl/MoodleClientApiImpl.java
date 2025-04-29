package ar.com.moodle.client.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ar.com.moodle.client.MoodleClientApi;
import ar.com.moodle.config.Configuration;
import ar.com.moodle.exception.BusinessException;
import ar.com.moodle.exception.ExternalApiException;
import ar.com.moodle.model.CohortData;
import ar.com.moodle.model.UserData;

public class MoodleClientApiImpl implements MoodleClientApi {

	private static final String MOODLE_URL = "https://campus.miscapacitaciones.com.ar/webservice/rest/server.php";
	private static final String FUNCTION_CORE_COURSE_GET_COURSES = "core_course_get_courses";
	private static final String FUNCTION_CORE_USER_CREATE_USERS = "core_user_create_users";
	private static final String FUNCTION_CORE_COHORT_GET_COHORTS = "core_cohort_get_cohorts";
	private static final String FUNCTION_CORE_COHORT_CREATE_COHORTS = "core_cohort_create_cohorts";
	private static final String FUNCTION_CORE_COHORT_ADD_COHORT_MEMBERS = "core_cohort_add_cohort_members";

	private static final Logger logger = LogManager.getLogger(MoodleClientApiImpl.class);

	@Override
	public Integer createUser(UserData user) throws ExternalApiException {
		try {
			logger.info("Dando de alta el usuario: " + user.getUsername());
			String token = Configuration.get("moodle.token");

			String url = MOODLE_URL + "?wstoken=" + token + "&wsfunction=" + FUNCTION_CORE_USER_CREATE_USERS
					+ "&moodlewsrestformat=json";

			String body = this.buildUserBody(user.getUsername(), user.getPassword(), user.getFirstname(),
					user.getLastname(), user.getEmail(), user.getDni(), user.getLegajoId(), user.getSectorJn(),
					user.getCentroDeCostos(), user.getPuesto());

			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
					.header("Content-Type", "application/x-www-form-urlencoded").POST(BodyPublishers.ofString(body))
					.build();

			logger.info("invocando a la función core_user_create_users...");
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			logger.info("Respuesta de Moodle: " + response.body());
			return response.statusCode();
		} catch (IOException ioe) {
			logger.error("Error de comunicación con el servicio externo. " + ioe.getMessage(), ioe);
			throw new ExternalApiException("Error de comunicación con el servicio: " + ioe.getMessage(), ioe);
		} catch (Exception ex) {
			logger.error("Error inesperado la dar de alta el usuario: " + user.getUsername(), ex.getCause());
			throw new ExternalApiException("Error inesperado la dar de alta el usuario: " + user.getUsername(), ex);
		}
	}

	@Override
	public CohortData createCohort(String sectorJn, String centroDeCostos) throws ExternalApiException {
		CohortData result = null;
		try {
			logger.info("Creando cohort... " + sectorJn + "-" + centroDeCostos);
			String token = Configuration.get("moodle.token");

			String url = MOODLE_URL + "?wstoken=" + token + "&wsfunction=" + FUNCTION_CORE_COHORT_CREATE_COHORTS
					+ "&moodlewsrestformat=json";

			String body = this.buildCohortBody(sectorJn, centroDeCostos);

			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
					.header("Content-Type", "application/x-www-form-urlencoded").POST(BodyPublishers.ofString(body))
					.build();

			logger.info("invocando a la función de moodle core_cohort_create_cohorts...");
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			logger.info("Response: " + response.body());
			if (response.statusCode() == HttpStatus.SC_OK && !response.body().contains("exception")) {
				Gson gson = new Gson();
				List<CohortData> cohorts = gson.fromJson(response.body(), new TypeToken<List<CohortData>>() {
				}.getType());
				result = cohorts.get(0);
			}

			return result;
		} catch (IOException ioe) {
			logger.error("Error de comunicación con el servicio externo. " + ioe.getMessage(), ioe);
			throw new ExternalApiException("Error de comunicación con el servicio: " + ioe.getMessage(), ioe);
		} catch (Exception ex) {
			logger.error("Error inesperado al crear cohort " + sectorJn + "-" + centroDeCostos, ex);
			throw new ExternalApiException("Error inesperado al crear cohort: " + ex.getMessage(), ex);
		}
	}

	private String buildUserBody(String username, String password, String firstname, String lastname, String email,
			String dni, String legajoId, String sectorJn, String centroDeCostos, String puesto) {
		StringBuilder sb = new StringBuilder();

		sb.append("users[0][username]=" + URLEncoder.encode(username, StandardCharsets.UTF_8));
		sb.append("&users[0][password]=" + URLEncoder.encode(password, StandardCharsets.UTF_8));
		sb.append("&users[0][firstname]=" + URLEncoder.encode(firstname, StandardCharsets.UTF_8));
		sb.append("&users[0][lastname]=" + URLEncoder.encode(lastname, StandardCharsets.UTF_8));
		sb.append("&users[0][email]=" + URLEncoder.encode(email, StandardCharsets.UTF_8));
		sb.append("&users[0][idnumber]=" + URLEncoder.encode("user_" + username, StandardCharsets.UTF_8));

		sb.append("&users[0][customfields][0][type]=" + URLEncoder.encode("dni", StandardCharsets.UTF_8));
		sb.append("&users[0][customfields][0][value]=" + URLEncoder.encode(dni, StandardCharsets.UTF_8));

		sb.append("&users[0][customfields][1][type]=" + URLEncoder.encode("legajo_id", StandardCharsets.UTF_8));
		sb.append("&users[0][customfields][1][value]=" + URLEncoder.encode(legajoId, StandardCharsets.UTF_8));

		sb.append("&users[0][customfields][2][type]=" + URLEncoder.encode("sector_jn", StandardCharsets.UTF_8));
		sb.append("&users[0][customfields][2][value]=" + URLEncoder.encode(sectorJn, StandardCharsets.UTF_8));

		sb.append("&users[0][customfields][3][type]=" + URLEncoder.encode("centro_de_costos", StandardCharsets.UTF_8));
		sb.append("&users[0][customfields][3][value]=" + URLEncoder.encode(centroDeCostos, StandardCharsets.UTF_8));

		sb.append("&users[0][customfields][4][type]=" + URLEncoder.encode("puesto", StandardCharsets.UTF_8));
		sb.append("&users[0][customfields][4][value]=" + URLEncoder.encode(puesto, StandardCharsets.UTF_8));

		return sb.toString();
	}

	private String buildCohortBody(String sectorJx, String centroDeCostos) {
		StringBuilder sb = new StringBuilder();

		sb.append("cohorts[0][name]=" + URLEncoder.encode(sectorJx + "-" + centroDeCostos, StandardCharsets.UTF_8));
		sb.append(
				"&cohorts[0][idnumber]=" + URLEncoder.encode(sectorJx + "-" + centroDeCostos, StandardCharsets.UTF_8));

		sb.append("&cohorts[0][description]=" + URLEncoder.encode("", StandardCharsets.UTF_8));
		sb.append("&cohorts[0][descriptionformat]=" + URLEncoder.encode("1", StandardCharsets.UTF_8));
		sb.append("&cohorts[0][categorytype][type]=" + URLEncoder.encode("system", StandardCharsets.UTF_8));
		sb.append("&cohorts[0][categorytype][value]=" + URLEncoder.encode("system", StandardCharsets.UTF_8));

		return sb.toString();
	}

	private String buildCohortAddMemberBody(String idnumberCohort, String username) {
		StringBuilder sb = new StringBuilder();

		sb.append("members[0][cohorttype][type]=" + URLEncoder.encode("idnumber", StandardCharsets.UTF_8));
		sb.append("&members[0][cohorttype][value]=" + URLEncoder.encode(idnumberCohort, StandardCharsets.UTF_8));

		sb.append("&members[0][usertype][type]=" + URLEncoder.encode("username", StandardCharsets.UTF_8));
		sb.append("&members[0][usertype][value]=" + URLEncoder.encode(username, StandardCharsets.UTF_8));

		return sb.toString();
	}

	@Override
	public String getAllCourses() throws ExternalApiException {
		String result;
		try {
			logger.info("Consultando los cursos... ");
			String token = Configuration.get("moodle.token");

			String url = MOODLE_URL + "?wstoken=" + token + "&wsfunction=" + FUNCTION_CORE_COURSE_GET_COURSES
					+ "&moodlewsrestformat=json";

			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			result = response.body();
			return result;
		} catch (IOException ioe) {
			throw new ExternalApiException("Error de comunicación con el servicio externo: " + ioe.getMessage(), ioe);
		} catch (Exception ex) {
			logger.error("Error inesperado al consultar los cursos . Message: " + ex.getMessage());
			throw new ExternalApiException("Error inesperado al consultar los cursos. " + ex.getMessage(), ex);
		}
	}

	@Override
	public List<CohortData> getAllCohortes() throws ExternalApiException {
		try {
			logger.info("invocando al servicio core_cohort_get_cohorts...");
			String token = Configuration.get("moodle.token");

			String url = MOODLE_URL + "?wstoken=" + token + "&wsfunction=" + FUNCTION_CORE_COHORT_GET_COHORTS
					+ "&moodlewsrestformat=json";

			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != HttpStatus.SC_OK)
				throw new BusinessException("Error al consultar las cohortes", null);

			Gson gson = new Gson();
			List<CohortData> cohorts = gson.fromJson(response.body(), new TypeToken<List<CohortData>>() {
			}.getType());

			return cohorts;
		} catch (IOException e) {
			throw new ExternalApiException("Error de comunicación con el servicio externo: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error("error inesperado al consultar las cohortes: " + e.getCause(), e);
			throw new ExternalApiException("error inesperado al consultar las cohortes: " + e.getMessage(), e);
		}
	}

	@Override
	public Integer cohortAddMember(String idnumberCohort, String username) throws ExternalApiException {
		try {
			logger.info("agregando el usuario " + username + " al cohort " + idnumberCohort);
			String token = Configuration.get("moodle.token");

			String url = MOODLE_URL + "?wstoken=" + token + "&wsfunction=" + FUNCTION_CORE_COHORT_ADD_COHORT_MEMBERS
					+ "&moodlewsrestformat=json";

			String body = this.buildCohortAddMemberBody(idnumberCohort, username);

			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
					.header("Content-Type", "application/x-www-form-urlencoded").POST(BodyPublishers.ofString(body))
					.build();

			logger.info("invocando a la función " + FUNCTION_CORE_COHORT_ADD_COHORT_MEMBERS + "...");
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			logger.info("response: " + response.body());
			return response.statusCode();
		} catch (IOException ioe) {
			throw new ExternalApiException("Error de comunicación con el servicio externo: " + ioe.getMessage(), ioe);
		} catch (Exception ex) {
			logger.error("Error inesperado al asignar el usuario " + username + "- al cohort " + idnumberCohort
					+ ". Message: " + ex.getMessage());
			throw new ExternalApiException("Error inesperado al asignar el usuario. " + ex.getMessage(), ex);
		}
	}

}
