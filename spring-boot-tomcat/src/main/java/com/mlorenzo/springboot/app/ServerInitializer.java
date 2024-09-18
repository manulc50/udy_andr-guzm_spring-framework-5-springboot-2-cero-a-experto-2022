package com.mlorenzo.springboot.app;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// Clase necesaria para poder realizar el despliegue de un war correctamente en un servidor externo como Tomcat

public class ServerInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// Aquí tenemos que pasar la clase que contiene la anotación "@SpringBootApplication"
		return builder.sources(SpringBootDataJpaFacturasApplication.class);
	}

	
}
