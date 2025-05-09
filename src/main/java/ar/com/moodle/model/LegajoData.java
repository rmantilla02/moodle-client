package ar.com.moodle.model;

import java.time.LocalDate;

import org.springframework.util.StringUtils;

import com.google.gson.annotations.SerializedName;

import ar.com.moodle.parser.DateUtils;

public class LegajoData {

	@SerializedName("Empresa_ID")
	String empresaId;

	@SerializedName("Legajo_ID")
	String legajoId;

	@SerializedName("Nombre")
	String nombre;

	@SerializedName("Apellido")
	String apellido;

	@SerializedName("Sectores_Descripcion")
	String sector;

	@SerializedName("CentroDeCostos_Descripcion")
	String centroDeCostos;

	@SerializedName("Puestos_Descripcion")
	String puesto;

	@SerializedName("Email")
	String email;

	@SerializedName("TiposDeDocumentos_Descripcion")
	String docDescripcion;

	@SerializedName("NroDocumento")
	String nroDocumento;

	@SerializedName("FechaIngreso")
	String fechaIngreso;

	public String getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(String empresaId) {
		this.empresaId = empresaId;
	}

	public String getLegajoId() {
		return legajoId;
	}

	public void setLegajoId(String legajoId) {
		this.legajoId = legajoId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getCentroDeCostos() {
		return centroDeCostos;
	}

	public void setCentroDeCostos(String centroDeCostos) {
		this.centroDeCostos = centroDeCostos;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDocDescripcion() {
		return docDescripcion;
	}

	public void setDocDescripcion(String docDescripcion) {
		this.docDescripcion = docDescripcion;
	}

	public String getNroDocumento() {
		return nroDocumento;
	}

	public void setNroDocumento(String nroDocumento) {
		this.nroDocumento = nroDocumento;
	}

	public String getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(String fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public UserData mapperToUserData() {
		UserData user = new UserData();
		user.setUsername(this.nroDocumento);
		user.setPassword(this.nroDocumento);
		user.setFirstname(this.nombre);
		user.setLastname(this.apellido);
		user.setEmail(this.email);
		user.setDni(this.nroDocumento);
		user.setLegajoId(this.legajoId);
		user.setSectorJn(this.sector);
		user.setCentroDeCostos(this.centroDeCostos);
		user.setPuesto(this.puesto);
		if (StringUtils.hasText(fechaIngreso)) {
			LocalDate dateLegajo = DateUtils.convertToLocalDate(fechaIngreso);
			user.setFechaIngreso(dateLegajo.toString());
		}

		return user;
	}

}
