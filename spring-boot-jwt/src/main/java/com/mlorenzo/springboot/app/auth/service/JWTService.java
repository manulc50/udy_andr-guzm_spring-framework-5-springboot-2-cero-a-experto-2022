package com.mlorenzo.springboot.app.auth.service;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;

public interface JWTService {
	
	// Método que crea un token JWT a partir de la información contenida en el parámetro de entrada "auth" de tipo "Authentication"
	// Este parámetro de entrada contiene información necesaria, como el username y los roles del usuario, para poder generar el token JWT
	public String create(Authentication auth) throws IOException;
	
	// Método que valida el token JWT que se le pasa como parámetro de entrada
	public boolean validate(String token);
	
	// Método que obtiene los "claims" del token JWT que se pasa como parámetro de entrada
	public Claims getClaims(String token);
	
	// Método que obtiene el username del token JWT que se pasa como parámetro de entrada
	public String getUsername(String token);
	
	// Método que obtiene los roles del usuario del token JWT que se pasa como parámetro de entrada
	public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException;
	
	// Método que elimina el texto "Bearer " del token JWT que se le pasa como parámetro de entrada
	public String resolve(String token);

}
