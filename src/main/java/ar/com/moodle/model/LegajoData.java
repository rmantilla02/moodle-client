package ar.com.moodle.model;

import com.google.gson.annotations.SerializedName;

public class LegajoData {

	@SerializedName("Empresa_ID")
	String empresaId;

	@SerializedName("Legajo_ID")
	String legajoId;
	@SerializedName("Nombre")
	String nombre;
	@SerializedName("Apellido")
	String apellido;
	@SerializedName("Sector")
	String sector;
	@SerializedName("CCosto")
	String centroDeCostos;
	@SerializedName("Puesto")
	String puesto;
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

}
