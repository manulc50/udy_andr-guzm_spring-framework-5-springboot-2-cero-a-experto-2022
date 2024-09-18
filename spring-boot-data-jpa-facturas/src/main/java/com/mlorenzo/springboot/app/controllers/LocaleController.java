package com.mlorenzo.springboot.app.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/* Esta clase controlador se encarga de solucionar el problema que existía cuando queriamos cambiar de idioma estando en una de las páginas de la tabla clientes de la vista 'listar.html' y nos llevaba al inicio de dicha vista con la tabla en su estado por defecto que es en la primera página
   Es decir,si por ejemplo estabamos en la página 3('/listar?page=2';el indice de la pagína empieza en 0) de dicha tabla y cambiabamos de idioma al inglés, por ejemplo,se perdía el parámetro de la url 'page' y al final nos quedaba '/listar?lang=en_US'.Entonces,en vez de mantener el estado de la tabla para ese número de página,nos llevaba al estado inical de la vista 'listar.html' con la tabla en la primera página */

@Controller // Indicamos que esta clase se trata de una clase Controlador de Spring para que lo almacene en su contenedor como un bean y lo gestione
public class LocaleController {
	
	// Método handler que rescucha peticiones http de tipo Get para la url '/locale'
	// Este método evita la perdida de parámetros de la url donde estamos actualmente cuando deseamos cambiar de idioma
	// El parámetro de entrada "request" de tipo "HttpServletRequest" se corresponde con la petición http
	@GetMapping("/locale")
	public String locale(HttpServletRequest request) {
		// Obtenemos la última url asociada a la vista donde estamos actualmente
		// Para ello,accedemos a la propiedad 'referer' de la cabecera de la petición http
		// En el caso de estar actualmente por ejemplo en la página 3 de la tabla clientes de la vista "listar.html",la última url será '/listar?page=2' con el parámetro 'page'
		String ultimaUrl = request.getHeader("referer");
		// Devolvemos una redirección al método handler asociado a la ruta indicada por "ultimaUrl" del controlador correspondiente(no se va directamente a la vista, primero pasa por el controlador mencionado)
		// Para el ejemplo anterior con la url '/listar?page=2',se ejecutaría el método handler 'listar' del controlador 'ClienteController' y, de esta manera, ya no perderíamos de la url el parámetro 'page' porque hemos recuperado la url completa de la instancia HttpServletRequest
		return "redirect:" + ultimaUrl;
	}

}
