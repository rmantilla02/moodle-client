package ar.com.moodle.client.impl;

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

import ar.com.moodle.client.JNextClientApi;
import ar.com.moodle.config.Config;
import ar.com.moodle.exception.BusinessException;
import ar.com.moodle.exception.ExternalApiException;
import ar.com.moodle.model.LegajoData;

public class JNextClientApiImpl implements JNextClientApi {

	private static final String JNEXT_BASE_URL = "https://www.cloudpayroll.com.ar";
	private static final String FUNCTION_LEGAJOS_CERT_GT = "//apiint/gtLegajos";
	private static final String FUNCTION_LEGAJOS_IADT_CERT_IS = "//apiint/LegajosSelBusIADT";

	private static final Logger logger = LogManager.getLogger(JNextClientApiImpl.class);

	@Override
	public List<LegajoData> getLegajosWithCertificateGT(Integer empresaId) throws ExternalApiException {
		StringBuilder response = new StringBuilder();
		List<LegajoData> result = null;
		try {
			logger.info("consultando los legajos de la empresaId {} ", empresaId);

			String certGTFilePath = Config.get("jnext.cert.gt.file.path");
			String certGTPassword = Config.get("jnext.cert.gt.password");

			SSLContext sslContext = this.buildSSLContext(certGTFilePath, certGTPassword);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			URL url = new URL(JNEXT_BASE_URL + FUNCTION_LEGAJOS_CERT_GT + "?EmpresaID=" + empresaId
					+ "&LastSync=1990-01-01T12:00:00");
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			if (connection.getResponseCode() != HttpStatus.SC_OK)
				throw new BusinessException("Error al consultar los legajos. responseCode: "
						+ connection.getResponseCode() + "message: " + connection.getResponseMessage());

			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			}

			Gson gson = new Gson();
			result = gson.fromJson(response.toString(), new TypeToken<List<LegajoData>>() {
			}.getType());
			return result;
		} catch (IOException ioe) {
			logger.error("Error de comunicación con el servicio externo", ioe);
			throw new ExternalApiException("Error de comunicación con el servicio: " + ioe.getMessage(), ioe);
		} catch (Exception ex) {
			logger.error("Error inesperado al consultar los legajos.", ex);
			throw new ExternalApiException("Error inesperado al consultar los legajos.", ex);
		}
	}

	@Override
	public List<LegajoData> getLegajosWithcertificateIS(Integer empresaId) throws ExternalApiException {
		StringBuilder response = new StringBuilder();
		List<LegajoData> result = null;
		try {
			String certISFilePath = Config.get("jnext.cert.is.file.path");
			String certISPassword = Config.get("jnext.cert.is.password");

			SSLContext sslContext = this.buildSSLContext(certISFilePath, certISPassword);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			URL url = new URL(JNEXT_BASE_URL + FUNCTION_LEGAJOS_IADT_CERT_IS + "?Empresa_ID=" + empresaId);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			if (connection.getResponseCode() != HttpStatus.SC_OK)
				throw new BusinessException("Error al consultar los legajos. responseCode: "
						+ connection.getResponseCode() + "message: " + connection.getResponseMessage());

			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			}

			Gson gson = new Gson();
			result = gson.fromJson(response.toString(), new TypeToken<List<LegajoData>>() {
			}.getType());
			return result;

		} catch (IOException ioe) {
			logger.error("Error de comunicación con el servicio externo. ", ioe);
			throw new ExternalApiException("Error de comunicación con el servicio: " + ioe.getMessage(), ioe);
		} catch (Exception ex) {
			logger.error("Error inesperado al consultar los legajos.", ex);
			throw new ExternalApiException("Error inesperado al consultar los legajos.", ex);
		}
	}

	private SSLContext buildSSLContext(String filepathCert, String passwordCert) throws BusinessException {
		KeyStore keyStore;
		try (InputStream keyStoreStream = new FileInputStream(filepathCert)) {
			keyStore = KeyStore.getInstance("PKCS12");
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
			logger.error("Error al crear sslContext.", e);
			throw new BusinessException("Error al crear sslContext");
		}
	}

}