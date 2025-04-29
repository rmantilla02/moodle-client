package ar.com.moodle.jnext.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ar.com.moodle.config.Configuration;
import ar.com.moodle.exception.BusinessException;
import ar.com.moodle.exception.ExternalApiException;
import ar.com.moodle.model.LegajoData;

public class JNextClientApiImpl implements JNextClientApi {

	private final String JNEXT_BASE_URL = "https://www.cloudpayroll.com.ar";
	private final String FUNCTION_GT_LEGAJOS = "//apiint/gtLegajos";

	private final String FUNCTION_IS_LEGAJOS_SELBUS = "//apiint/Legajos";

	private static final Logger logger = LogManager.getLogger(JNextClientApiImpl.class);

	@Override
	public List<LegajoData> getGTLegajosByEmpresaId(Integer empresaId) throws ExternalApiException {
		StringBuilder respuesta = new StringBuilder();
		try {
			logger.info("consultando los legajos de la empresaId: " + empresaId);

			String cert_gt_file_path = Configuration.get("jnext.cert.gt.file.path");
			String cert_gt_pass = Configuration.get("jnext.cert.gt.password");

			SSLContext sslContext = this.buildSSLContext(cert_gt_file_path, cert_gt_pass);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			URL url = new URL(
					JNEXT_BASE_URL + FUNCTION_GT_LEGAJOS + "?EmpresaID=" + empresaId + "&LastSync=1990-01-01T12:00:00");
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			if (connection.getResponseCode() != HttpStatus.SC_OK)
				throw new BusinessException("Error al consultar los legajos");

			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					respuesta.append(inputLine);
				}
			}
			String resultAux = respuesta.toString();
			System.out.println("response: " + resultAux);

			Gson gson = new Gson();
			List<LegajoData> resultLejagos = gson.fromJson(resultAux, new TypeToken<List<LegajoData>>() {
			}.getType());
			return resultLejagos;
		} catch (IOException ioe) {
			logger.error("Error de comunicación con el servicio externo. " + ioe.getMessage(), ioe);
			throw new ExternalApiException("Error de comunicación con el servicio: " + ioe.getMessage(), ioe);
		} catch (Exception ex) {
			logger.error("Error inesperado al consultar los legajos.", ex.getCause());
			throw new ExternalApiException("Error inesperado al consultar los legajos.", ex);
		}
	}

	@Override
	public List<LegajoData> getLegajosCertificadoIS(Integer empresaId) throws ExternalApiException {
		StringBuilder respuesta = new StringBuilder();
		try {
			String cert_is_file_path = Configuration.get("jnext.cert.is.file.path");
			String cert_is_pass = Configuration.get("jnext.cert.is.password");

			SSLContext sslContext = this.buildSSLContext(cert_is_file_path, cert_is_pass);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			URL url = new URL(JNEXT_BASE_URL + FUNCTION_IS_LEGAJOS_SELBUS + "?Empresa_ID=" + empresaId);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			if (connection.getResponseCode() != HttpStatus.SC_OK)
				throw new BusinessException("Error al consultar los legajos");

			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					respuesta.append(inputLine);
				}
			}

			String resultAux = respuesta.toString();
			System.out.println("Código de respuesta: " + resultAux);

			Gson gson = new Gson();
			List<LegajoData> resultLejagos = gson.fromJson(resultAux, new TypeToken<List<LegajoData>>() {
			}.getType());
			return resultLejagos;
		} catch (IOException ioe) {
			logger.error("Error de comunicación con el servicio externo. " + ioe.getMessage(), ioe);
			throw new ExternalApiException("Error de comunicación con el servicio: " + ioe.getMessage(), ioe);
		} catch (Exception ex) {
			logger.error("Error inesperado al consultar los legajos.", ex.getCause());
			throw new ExternalApiException("Error inesperado al consultar los legajos.", ex);
		}
	}

	private SSLContext buildSSLContext(String filepathCert, String passwordCert) throws BusinessException {
		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance("PKCS12");

			InputStream keyStoreStream = new FileInputStream(filepathCert);
			keyStore.load(keyStoreStream, passwordCert.toCharArray());

			KeyManagerFactory kmf;
			kmf = KeyManagerFactory.getInstance("SunX509");

			kmf.init(keyStore, passwordCert.toCharArray());
			KeyManager[] keyManagers = kmf.getKeyManagers();

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init((KeyStore) null); // Usa el truststore por defecto
			TrustManager[] trustManagers = tmf.getTrustManagers();
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagers, trustManagers, null);
			return sslContext;
		} catch (Exception e) {

			throw new BusinessException("Error al crear sslContext");
		}
	}

}