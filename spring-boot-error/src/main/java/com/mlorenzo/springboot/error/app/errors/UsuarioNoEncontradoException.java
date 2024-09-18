package com.mlorenzo.springboot.error.app.errors;

public class UsuarioNoEncontradoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 466621873898373096L;
	
	public UsuarioNoEncontradoException(String id) {
		super("Usuario con ID: ".concat(id).concat(" no existe en el sistema"));
	}

}
