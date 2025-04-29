package ar.com.moodle.client;

import java.util.List;

import ar.com.moodle.exception.ExternalApiException;
import ar.com.moodle.model.CohortData;
import ar.com.moodle.model.UserData;

public interface MoodleClientApi {

	/**
	 * 
	 * @return los cursos disponibles en la plataforma de Moodle
	 * @throws ExternalApiException
	 */
	public String getAllCourses() throws ExternalApiException;

	/**
	 * 
	 * @return cohorts en la plataforma de moodle
	 */
	public List<CohortData> getAllCohortes() throws ExternalApiException;

	/**
	 * 
	 * @param sectorJn
	 * @param centroDeCostos
	 * @return info de cohort creada
	 * @throws ExternalApiException
	 */
	public CohortData createCohort(String sectorJn, String centroDeCostos) throws ExternalApiException;

	/**
	 * 
	 * @param user
	 * @return
	 * @throws ExternalApiException
	 */
	public Integer createUser(UserData user) throws ExternalApiException;

	/**
	 * 
	 * @param idnumberCohort
	 * @param username
	 * @return los datos del usuario asignado
	 * @throws ExternalApiException
	 */

	public Integer cohortAddMember(String idnumberCohort, String username) throws ExternalApiException;

}
