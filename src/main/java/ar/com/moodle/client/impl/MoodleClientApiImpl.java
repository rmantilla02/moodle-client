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
import ar.com.moodle.config.Config;
import ar.com.moodle.exception.BusinessException;
import ar.com.moodle.exception.ExternalApiException;
import ar.com.moodle.model.CohortData;
import ar.com.moodle.model.UserData;

public class MoodleClientApiImpl implements MoodleClientApi {

	private static final String MOODLE_URL = "https://campus.miscapacitaciones.com.ar/webservice/rest/server.php";
	private static final String CONF_MOODLE_TOKEN = "moodle.token";
	private static final String WS_TOKEN = "wstoken=";
	private static final String WS_FUNCTION = "wsfunction=";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	private static final String WS_MOODLE_FORMAT = "moodlewsrestformat=json";
	private static final String FUNCTION_CORE_USER_CREATE_USERS = "core_user_create_users";
	private static final String FUNCTION_CORE_COHORT_GET_COHORTS = "core_cohort_get_cohorts";
	private static final String FUNCTION_CORE_COHORT_CREATE_COHORTS = "core_cohort_create_cohorts";
	private static final String FUNCTION_CORE_COHORT_ADD_COHORT_MEMBERS = "core_cohort_add_cohort_members";

	private static final Logger logger = LogManager.getLogger(MoodleClientApiImpl.class);

	@Override
	public Integer createUser(UserData user) throws ExternalApiException {
		try {
			logger.info("Dando de alta el usuario {} ...", user.getUsername());
			String token = Config.get(CONF_MOODLE_TOKEN);

			String url = this.buildUrl(token, FUNCTION_CORE_USER_CREATE_USERS);

			String body = this.createUserRequestBody(user);

			HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
					.header(CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED).POST(BodyPublishers.ofString(body)).build();

			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			String responseBody = response.body();

			if (response.statusCode() != HttpStatus.SC_OK)
				throw new BusinessException("Error al dar de alta el usuario.  statusCode: " + response.statusCode()
						+ ". body: " + responseBody);

			logger.info("Response: {}", responseBody);
			return response.statusCode();
		} catch (IOException ioe) {
			logger.error("Error de comunicación con el servicio externo. ", ioe);
			throw new ExternalApiException("Error de comunicación con el servicio: ", ioe);
		} catch (Exception ex) {
			logger.error("Error inesperado la dar de alta el usuario: {}", user.getUsername(), ex.getCause());
			throw new ExternalApiException("Error inesperado la dar de alta el usuario: " + user.getUsername(), ex);
		}
	}

	@Override
	public CohortData createCohort(String sectorJn, String centroDeCostos) throws ExternalApiException {
		CohortData result = null;
		try {
			logger.info("Creando cohort... sector {} - centroDeCostos {}", sectorJn, centroDeCostos);
			String token = Config.get(CONF_MOODLE_TOKEN);

			String url = this.buildUrl(token, FUNCTION_CORE_COHORT_CREATE_COHORTS);

			String body = this.buildCohortBody(sectorJn.toUpperCase(), centroDeCostos.toUpperCase());

			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
					.header(CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED).POST(BodyPublishers.ofString(body)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			String responseBody = response.body();

			if (response.statusCode() != HttpStatus.SC_OK)
				throw new BusinessException("Error al consultar las cohorts.  statusCode: " + response.statusCode()
						+ ". body: " + responseBody);

			logger.info("Response: {}", responseBody);
			if (!responseBody.contains("exception")) {
				Gson gson = new Gson();
				List<CohortData> cohorts = gson.fromJson(responseBody, new TypeToken<List<CohortData>>() {
				}.getType());

				result = cohorts.get(0);
			}

			return result;
		} catch (IOException ioe) {
			logger.error("Error de comunicación con el servicio externo.", ioe);
			throw new ExternalApiException("Error de comunicación con el servicio: " + ioe.getMessage(), ioe);
		} catch (Exception ex) {
			logger.error("Error inesperado al crear cohort {} - {} ", sectorJn, centroDeCostos, ex);
			throw new ExternalApiException("Error inesperado al crear cohort: " + ex.getMessage(), ex);
		}
	}

	@Override
	public List<CohortData> getAllCohortes() throws ExternalApiException {
		List<CohortData> cohorts = null;
		try {
			logger.info("consultando las cohorts...");
			String token = Config.get(CONF_MOODLE_TOKEN);

			String url = this.buildUrl(token, FUNCTION_CORE_COHORT_GET_COHORTS);

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			String responseBody = response.body();

			if (response.statusCode() != HttpStatus.SC_OK)
				throw new BusinessException("Error al consultar las cohorts.  statusCode: " + response.statusCode()
						+ ". body: " + responseBody);

			Gson gson = new Gson();
			cohorts = gson.fromJson(responseBody, new TypeToken<List<CohortData>>() {
			}.getType());

			return cohorts;
		} catch (IOException e) {
			logger.error("Error de comunicación con el servicio de moodle", e);
			throw new ExternalApiException("Error de comunicación con el servicio externo: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error("error inesperado al consultar las cohortes: {}", e.getMessage(), e);
			throw new ExternalApiException("error inesperado al consultar las cohortes: " + e.getMessage(), e);
		}
	}

	@Override
	public Integer cohortAddMember(String idnumberCohort, String username) throws ExternalApiException {
		try {
			logger.info("agregando el usuario {} al cohort {}", username, idnumberCohort);
			String token = Config.get(CONF_MOODLE_TOKEN);

			String url = this.buildUrl(token, FUNCTION_CORE_COHORT_ADD_COHORT_MEMBERS);

			String body = this.buildCohortAddMemberBody(idnumberCohort, username);

			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
					.header(CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED).POST(BodyPublishers.ofString(body)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			String responseBody = response.body();

			if (response.statusCode() != HttpStatus.SC_OK)
				throw new BusinessException("Error al consultar las cohorts.  statusCode: " + response.statusCode()
						+ ". body: " + responseBody);

			logger.info("response: {}", responseBody);
			return response.statusCode();
		} catch (IOException ioe) {
			throw new ExternalApiException("Error de comunicación con el servicio externo: " + ioe.getMessage(), ioe);
		} catch (Exception ex) {
			logger.error("Error inesperado al asignar el usuario {} al cohort {} ", username, idnumberCohort, ex);
			throw new ExternalApiException("Error inesperado al asignar el usuario. " + ex.getMessage(), ex);
		}
	}

	private String buildUrl(String token, String function) {
		StringBuilder sb = new StringBuilder();
		sb.append(MOODLE_URL);
		sb.append("?" + WS_TOKEN + token);
		sb.append("&" + WS_FUNCTION + function);
		sb.append("&" + WS_MOODLE_FORMAT);

		return sb.toString();
	}

	private String createUserRequestBody(UserData user) {
		StringBuilder sb = new StringBuilder();

		sb.append("users[0][username]=" + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8));
		sb.append("&users[0][password]=" + URLEncoder.encode(user.getPassword(), StandardCharsets.UTF_8));
		sb.append("&users[0][firstname]=" + URLEncoder.encode(user.getFirstname(), StandardCharsets.UTF_8));
		sb.append("&users[0][lastname]=" + URLEncoder.encode(user.getLastname(), StandardCharsets.UTF_8));
		sb.append("&users[0][email]=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8));
		sb.append("&users[0][idnumber]=" + URLEncoder.encode("user_" + user.getUsername(), StandardCharsets.UTF_8));

		sb.append("&users[0][customfields][0][type]=" + URLEncoder.encode("dni", StandardCharsets.UTF_8));
		sb.append("&users[0][customfields][0][value]=" + URLEncoder.encode(user.getDni(), StandardCharsets.UTF_8));

		sb.append("&users[0][customfields][1][type]=" + URLEncoder.encode("legajo_id", StandardCharsets.UTF_8));
		sb.append("&users[0][customfields][1][value]=" + URLEncoder.encode(user.getLegajoId(), StandardCharsets.UTF_8));

		sb.append("&users[0][customfields][2][type]=" + URLEncoder.encode("sector_jn", StandardCharsets.UTF_8));
		sb.append("&users[0][customfields][2][value]=" + URLEncoder.encode(user.getSectorJn(), StandardCharsets.UTF_8));

		sb.append("&users[0][customfields][3][type]=" + URLEncoder.encode("centro_de_costos", StandardCharsets.UTF_8));
		sb.append("&users[0][customfields][3][value]="
				+ URLEncoder.encode(user.getCentroDeCostos(), StandardCharsets.UTF_8));

		sb.append("&users[0][customfields][4][type]=" + URLEncoder.encode("puesto", StandardCharsets.UTF_8));
		sb.append("&users[0][customfields][4][value]=" + URLEncoder.encode(user.getPuesto(), StandardCharsets.UTF_8));

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

}
