package com.mlorenzo.springboot.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	// Nota: Con "redirect", se pierde la petición http original y sus parámetros y se crea una nueva petición http
	// Sin embargo, con "forward", la petición original y sus parámetros se mantienen
	// Con "redirect", podemos usar tanto rutas internas como rutas externas a la aplicación(Por ejemplo, "www.google.es"
	// Sin embargo, con "forward", sólo podemos usar rutas internas de la aplicación
	
	@GetMapping("/")
	public String index() {
		return "forward:/app/index";
	}
	
	@GetMapping("/home")
	public String home() {
		return "redirect:/app/home";
	}


}
