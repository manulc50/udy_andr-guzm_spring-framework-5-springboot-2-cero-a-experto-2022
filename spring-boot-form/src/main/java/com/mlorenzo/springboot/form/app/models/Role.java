package com.mlorenzo.springboot.form.app.models;

public class Role {
	private Integer id;
	private String nombre;
	private String role;
	
	public Role() {
		
	}

	public Role(Integer id, String nombre, String role) {
		this.id = id;
		this.nombre = nombre;
		this.role = role;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	// Para que el método "contains" del objeto "lists" de Thymeleaf sepa comparar objetos de este tipo Role
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		if(!(obj instanceof Role))
			return false;
		
		Role role = (Role)obj;
		return this.id != null && this.id.equals(role.id);
	}
	
	
}
