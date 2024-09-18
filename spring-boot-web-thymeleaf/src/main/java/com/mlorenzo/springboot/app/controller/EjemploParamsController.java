package com.mlorenzo.springboot.app.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/params")
public class EjemploParamsController {
	
	@GetMapping
	public String index(Model model) {
		model.addAttribute("titulo","Enviar parámetros del Request HTTP GET - URL");
		return "params/index";
	}
	
	// En este caso, el atributo "name" de la anotación "@RequestParam" es opcional porque su valor coincide con el nombre del argumento de entrada
	@GetMapping("/string")
	public String param(@RequestParam(name = "texto", required = false, defaultValue = "algún valor") String texto, Model model) {
		model.addAttribute("titulo", "Recibir parámetros del Request HTTP GET - URL");
		model.addAttribute("resultado", "El texto enviado es: " + texto);
		return "params/ver";
	}
	
	// Primera manera de obtener parámetros de una petición http - Anotación @RequestParam 
	@GetMapping("/mix-params")
	public String param(@RequestParam String saludo, @RequestParam Integer numero, Model model) {
		model.addAttribute("titulo", "Recibir parámetros del Request HTTP GET - URL");
		model.addAttribute("resultado", "El saludo enviado es: '" + saludo + "' y el número es: '" + numero + "'");
		return "params/ver";
	}
	
	// Segunda manera de obtener parámetros de una petición http - Parámetro de entrada de tipo HttpServletRequest
	// Spring de manera automática inyecta el bean del parámetro de entrada "request" de tipo "HttpServletRequest" que se corresponde con la petición http
	@GetMapping("/mix-params-request")
	public String param(HttpServletRequest request, Model model) {
		String saludo = request.getParameter("saludo");
		Integer numero = null;
		try {
			numero = Integer.parseInt(request.getParameter("numero"));
		}
		catch(NumberFormatException e) {
			numero = 0;
		}
		model.addAttribute("titulo", "Recibir parámetros del Request HTTP GET - URL");
		model.addAttribute("resultado", "El saludo enviado es: '" + saludo + "' y el número es: '" + numero + "'");
		return "params/ver";
	}

}
