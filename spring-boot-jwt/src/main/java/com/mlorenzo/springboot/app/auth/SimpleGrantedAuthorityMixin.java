package com.mlorenzo.springboot.app.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/* Esta clase realiza un mapeo entre la propiedad 'authority' del Json correspondiente al payload del token JWT, que contiene los roles del usuario, con la propiedad 'String role' que se le pasa al contructor.
 * Esto es así porque la clase 'SimpleGrantedAuthorities' tiene un sólo constructor donde se le pasa un String role y necesitamos usar ese constructor para pasarle la información de los roles que tenemos en formato Json en la propiedad 'authority'.
 * Esta clase es abstracta porque no hay métodos,tan sólo existe un constructor*/

public abstract class SimpleGrantedAuthorityMixin {
	
	// Con la anotación @JsonProperty asociamos la propiedad 'authority' del Json al parámetro de entrada del constructor 'String role'
	@JsonCreator // Con esta anotación indicamos que este va a ser nuestro constructor por defecto para crear los objetos authorities a partir de la información del Json
	public SimpleGrantedAuthorityMixin(@JsonProperty("authority") String role){
		
	}

}
