package com.mlorenzo.springboot.di.app.models.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
// Por defecto, como esta clase se trata de un componente de Spring por la anotación anterior, el bean que se almacene en su contenedor o memoria va a ser de tipo Singleton, es decir, el bean va a durar lo que dure la aplicación hasta que se haga undeploy del servidor embebido o interno Tomcat porque solo hay un bean para todas las peticiones http que se realicen mientras la aplicación se esté ejecutando en el servidor
@RequestScope // Con esta anotación, cambiamos el contexto por defecto del bean de Singleton a Request y ahora el bean va a durar lo que dure una petición http, es decir, por cada petición http, se va a crear un bean y se va a destruir cuando finalice su petición http correspondiente 
// @SessionScope // Con esta anotación, cambiamos el contexto por defecto del bean de Singleton a Session y ahora el bean va a durar lo que dure una sesión, es decir, va a durar hasta que se cierre el navegador o cuando se produce un Timeout o cuando se invalida la sesión. Los objetos o bean que usen este contexto o Scope deben implementar la interfaz Serializable
// @ApplicationScope // Con esta anotación, cambiamos el contexto por defecto del bean de Singleton a Application. Es un contexto muy similar a Singleton pero se diferencian en que el bean con contexto o scope Applicaation es un Singleton que se almacena en el contexto del servlet(puede haber varios servlets en la aplicación), sin embargo, el Singleton se almacena en el contexto de la aplicación de Spring(solo hay uno)
public class Cliente {
	
	@Value("${cliente.nombre}")
	private String nombre;
	
	@Value("${cliente.apellido}")
	private String apellido;
	
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
	
}
