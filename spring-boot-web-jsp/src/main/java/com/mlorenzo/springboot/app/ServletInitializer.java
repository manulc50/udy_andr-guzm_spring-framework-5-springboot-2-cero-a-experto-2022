package com.mlorenzo.springboot.app;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// Esta clase se genera automáticamente para proyectos de tipo War y es necesario para desplegar la app en un servidor.Por detrás genera el fichero web.xml
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootWebJspApplication.class);
	}

}
