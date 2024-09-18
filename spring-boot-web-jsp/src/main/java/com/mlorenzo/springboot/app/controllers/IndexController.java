package com.mlorenzo.springboot.app.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Marca la clase como un controlador gestionado por Spring
public class IndexController {
	
	@Value("${application.controller.titulo}") // Insertamos el valor definido en la propiedad 'application.controller.titulo' en 'titulo'
	private String titulo;
	
	// Controlador que escuha peticiones HTTP de tipo GET para el path /index
	@GetMapping("/index")
	public String index(Model model){ // El objeto model permite añadir atributos a la vista
		model.addAttribute("titulo", titulo);
		return "index";
		
	}
}
