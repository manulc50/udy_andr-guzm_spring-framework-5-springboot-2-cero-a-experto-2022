package com.mlorenzo.springboot.form.app.models;

public class Pais {
	private Integer id;
	private String codigo;
	private String nombre;
	
	public Pais() {
		
	}
	
	public Pais(Integer id, String codigo, String nombre) {
		this.id = id;
		this.codigo = codigo;
		this.nombre = nombre;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	// Para que en el elemento HTML de tipo Select de los países pueda realizarse la comparación de los ids de esos países con el id del objeto de tipo País que se establece como valor por defecto
	@Override
	public String toString() {
		return this.id.toString();
	}
	
	
}
