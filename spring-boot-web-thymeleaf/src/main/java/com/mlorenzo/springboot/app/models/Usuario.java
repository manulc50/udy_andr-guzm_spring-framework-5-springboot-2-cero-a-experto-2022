package com.mlorenzo.springboot.app.models;

import java.io.Serializable;

public class Usuario implements Serializable {
	
	private static final long serialVersionUID = 8738811349089785660L;
	
	private String nombre;
	private String apellido;
	private String email;
	
	public Usuario() {
	}

	public Usuario(String nombre, String apellido, String email) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
