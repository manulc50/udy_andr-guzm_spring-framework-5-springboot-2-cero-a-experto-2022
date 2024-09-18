package com.mlorenzo.springboot.di.app.models.domain;

public class Producto {
	
	private String nombre;
	private Float precio;
	
	public Producto(String nombre, Float precio) {
		this.nombre = nombre;
		this.precio = precio;
	}

	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public Float getPrecio() {
		return precio;
	}
	
	public void setPrecio(Float precio) {
		this.precio = precio;
	}
	
}
