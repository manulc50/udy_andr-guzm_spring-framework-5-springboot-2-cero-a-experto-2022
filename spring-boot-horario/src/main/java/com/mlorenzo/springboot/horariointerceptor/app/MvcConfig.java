package com.mlorenzo.springboot.horariointerceptor.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer{
	
	@Autowired
	@Qualifier("horario")
	private HandlerInterceptor horarioInterceptor;

	// Sobrescribimos este método de la clase padre "WebMvcConfigurer" que heredamos para registrar nuestro interceptor de horarios
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// Excluimos el interceptor para la ruta "/cerrado" para evitar un loop o bucle infinito de llamadas a este interceptor cuando el horario de atención al cliente no se cumple(está cerrado)
		// Es decir, este interceptor captura cualquier petición http a las rutas del controlador de esta aplicación y cuando el horario de atención al cliente no se cumple(está cerrado), se redirige a la ruta "/cerrado" asociada al método handler "cerrado" del controllador, pero, antes, esta redirección es capturada otra vez por este interceptor y vuelve a pasar lo mismo produciéndose un bucle infinito  
		// Por esta razón, hacemos que no se aplique este interceptor para peticiones http a la ruta "/cerrado"
		registry.addInterceptor(horarioInterceptor).excludePathPatterns("/cerrado");
	}
	

}
