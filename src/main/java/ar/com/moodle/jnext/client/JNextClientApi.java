package ar.com.moodle.jnext.client;

import java.util.List;

import ar.com.moodle.exception.ExternalApiException;
import ar.com.moodle.model.LegajoData;

public interface JNextClientApi {

	/**
	 * 
	 * @param empresaId
	 * @return los legajos segun empresaId
	 * @throws ExternalApiException
	 */
	public List<LegajoData> getGTLegajosByEmpresaId(Integer empresaId) throws ExternalApiException;

	/**
	 * 
	 * @param empresaId
	 * @return los legajos segun empresaId
	 * @throws ExternalApiException
	 */
	public List<LegajoData> getLegajosCertificadoIS(Integer empresaId) throws ExternalApiException;

}
