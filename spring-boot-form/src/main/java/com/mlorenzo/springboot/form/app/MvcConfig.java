package com.mlorenzo.springboot.form.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	@Autowired
	private HandlerInterceptor tiempoTranscurridoInterceptor;

	// Registramos nuestro interceptor personalizado "TiempoTranscurridoInterceptor"
	// Este interceptor se aplicará a las peticiones http que hagan "match", o coincidan, con las rutas de la expresión "/form/**"
	// Nota: Si no usamos el método "addPathPatterns", el interceptor se aplicaría a todas las peticiones http de forma global
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(tiempoTranscurridoInterceptor).addPathPatterns("/form/**");
	}

	
	
}
