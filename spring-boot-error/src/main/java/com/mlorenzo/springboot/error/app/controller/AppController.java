package com.mlorenzo.springboot.error.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mlorenzo.springboot.error.app.errors.UsuarioNoEncontradoException;
import com.mlorenzo.springboot.error.app.models.domain.Usuario;
import com.mlorenzo.springboot.error.app.services.UsuarioService;

@Controller
public class AppController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping("/index")
	public String index() {
		// No vamos a crear la vista(html) "index" para provocar el error 500(Internal Server Error), con mensaje "Error resolving template [index], template might not exist or might not be accessible by any of the configured Template Resolvers", y así poder manejar y personalizar este tipo de errores
		return "index";
	}
	
	@SuppressWarnings("unused")
	@GetMapping("/arithmeticexception")
	public String divisionByZero() {
		// Realizamos una división de un número entre 0(infinito) para provocar la excepción de tipo ArithmeticException y el error 500(Internal Server Error), con mensaje "/ by zero"
		Integer valor = 100/0;
		
		// La vista(html) "prueba" no existe. Este método es para probar únicamente la excepción ArithmeticException y el error 500 devuelto por la división de un número entre 0 del paso anterior
		return "prueba"; 
	}
	
	@SuppressWarnings("unused")
	@GetMapping("/numberformatexception")
	public String numberformat() {
		// Realizamos una conversión de una cadena de texto, que no representa un número, a un número entero
		Integer valor = Integer.parseInt("10x");
		
		// La vista(html) "prueba" no existe. Este método es para probar únicamente la excepción NumberFormatException y el error 500 devuelto por la conversión de una cadena de texto, que no representa un número, a un nuevo entero anterior
		return "prueba";	
	}
	
	@GetMapping("/v1/ver/{id}")
	public String verV1(@PathVariable Integer id, Model model) {
		Usuario usuario = this.usuarioService.obtenerPorId(id);
		
		if(usuario == null)
			throw new UsuarioNoEncontradoException(id.toString());
		
		model.addAttribute("usuario",usuario);
		model.addAttribute("titulo","Detalle del usuario: " + usuario.getNombre());
		
		return "ver";
	}
	
	@GetMapping("/v2/ver/{id}")
	public String verV2(@PathVariable Integer id, Model model) {
		// Con el método "orElseThrow" del objeto Optional devuelto por el método "obtenerPorIdOptional" lanzamos nuestra excepción personalizada "UsuarioNoEncontradoException" cuando el objeto de tipo Usuario contenido en ese Optional es nulo,es decir, cuando ese usuario no existe en el sistema
		// Para ello, el método "orElseThrow" recibe una función lambda donde creamos una instancia de nuestra excepción personalizada "UsuarioNoEncontradoException" y él se encarga de lanzarla 
		Usuario usuario = this.usuarioService.obtenerPorIdOptional(id).orElseThrow(() -> new UsuarioNoEncontradoException(id.toString()));
		
		model.addAttribute("usuario",usuario);
		model.addAttribute("titulo","Detalle del usuario: " + usuario.getNombre());
		
		return "ver";
	}
}
