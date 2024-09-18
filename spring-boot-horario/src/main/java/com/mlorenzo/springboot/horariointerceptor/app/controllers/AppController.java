package com.mlorenzo.springboot.horariointerceptor.app.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {
	
	// Nota: Para poder inyectar valores de propiedades definidas en el archivo de configuración "application.properties" mediante la anotación @Value, es necesario anotar esta clase con @Componente(En este caso no hace falta porque la anotación @Controller ya incluye esa anotación) para que sea un componente de Spring y la almacene como un bean en su contendor
	
	@Value("${config.horario.apertura}")
	private Integer apertura;
	
	@Value("${config.horario.cierre}")
	private Integer cierre;
	
	@GetMapping({"/","index"})
	public String index(Model model) {
		model.addAttribute("titulo","Bienvenido al horario de atención al cliente");
		return "index";
	}
	
	@GetMapping({"/cerrado"})
	public String cerrado(Model model) {
		StringBuilder mensaje = new StringBuilder("Cerrado, por favor visítenos desde las ");
		mensaje.append(apertura);
		mensaje.append(" y las ");
		mensaje.append(cierre);
		mensaje.append(" hrs. Gracias");
		
		model.addAttribute("titulo","Fuera del horaio de atención al cliente");
		model.addAttribute("mensaje",mensaje.toString());
		
		return "cerrado";
	}
}
