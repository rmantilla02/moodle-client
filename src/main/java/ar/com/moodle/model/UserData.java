package ar.com.moodle.model;

public class UserData {

	String username;
	String password;
	String firstname;
	String lastname;
	String email;
	String dni;
	String sectorJn;
	String puesto;
	String centroDeCostos;
	String legajoId;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getSectorJn() {
		return sectorJn;
	}

	public void setSectorJn(String sectorJn) {
		this.sectorJn = sectorJn;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public String getCentroDeCostos() {
		return centroDeCostos;
	}

	public void setCentroDeCostos(String centroDeCostos) {
		this.centroDeCostos = centroDeCostos;
	}

	public String getLegajoId() {
		return legajoId;
	}

	public void setLegajoId(String legajoId) {
		this.legajoId = legajoId;
	}

	public String getCohort() {
		return sectorJn + "-" + centroDeCostos;
	}

}
