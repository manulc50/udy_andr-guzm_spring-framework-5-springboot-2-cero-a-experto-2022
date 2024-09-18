package com.mlorenzo.springboot.app.controllers;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller // Indicamos que esta clase se trata de una clase Controlador de Spring para que lo almacene en su contenedor como un bean y lo gestione
public class LoginController {
	
	// Recuperamos del contenedor o memoria de Spring el bean que implmenta la interfaz 'MessageSource'.La implementación de esta interfaz la gestiona Spring al ser una interfaz propia de Spring
	@Autowired
	private MessageSource messageSource; // Este bean es necesario para poder traducir los textos de esta clase a partir de los archivos de idiomas 'message_'

	// NOTA: Los parámetros en las urls se recuperan con @RequestParam y viajan en la url de la siguiente manera;'/login?error'.A partir del caracter '?',todo lo que haya a la derecha son parámetros.Los datos introducidos en el fomrulario,viajan como parámetros en la url de esta forma.
	// NOTA: Las variables en una url se recuperan con @PathVariable y están en la url de la siguiente manera;'/factura/123'->123 sería la variable
	
	// Método handler que escucha peticiones http de tipo GET para la ruta '/login'
	// El argumento de entrada "model" de tipo "Model" nos permite pasar datos desde este controlador a las vistas
	// El parámetro de entrada "principal" de tipo "Principal" nos da información sobre si el usuario ya ha hecho login anteriormente o todavía no lo ha hecho
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	// Cuando se introduce un nombre de usuario incorrecto o una contraseña incorrecta durante el login ,SpringSecurity redirecciona a la mima ruta('/login') con el parámetro 'error' -> '/login?error'
	// Cuando se realiza el proceso de logout,SpringSecurity redirecciona a la mima ruta('/login') con el parámetro 'logout' -> '/login?logout'
	// Con @RequestParam recuperamos el parámetro 'error', que no siempre viene(required=false).Solo aparece cuando se produce un error en el proceso de login
	// Con @RequestParam recuperamos el parámetro 'logout', que no siempre viene(required=false).Solo aparece cuando se ha hecho logout
	@GetMapping("/login")
	public String login(@RequestParam(value="error",required=false) String error,@RequestParam(value="logout",required=false) String logout,Model model,Principal principal,RedirectAttributes flash, Locale locale){		
		
		// Comprueba si el usuario ya inicio sesion anteriormente para evitar un doble login y, en caso afirmativo, hacemos lo siguiente:
		if(principal != null){
			// Creamos un atributo con un mensaje de información al usuario de tipo Flash
			// Es de tipo Flash ya que a continuación devolvemos un redirect a "/".Entonces,no vamos directamente a ninguna vista,sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta "/" del controlador "ClienteController" y queremos mantener este atributo para esta segunda petición http
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.login.already' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			flash.addFlashAttribute("info",messageSource.getMessage("text.login.already", null, locale));
			// Devolvemos una redirección al método handler "listar" del controlador "ClienteController" asociado a la ruta '/'(no se va directamente a la vista "listar", primero pasa por el controlador mencionado)
			return "redirect:/";
		}
		
		// En caso de haber algún error de validacion en el login, hacemos lo siguiente:
		if(error != null){
			// Pasamos a la vista el atributo "error" con un mensaje de error
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.login.error' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			model.addAttribute("error",messageSource.getMessage("text.login.error", null, locale));
		}
		
		// En caso de que el usuario haya cerrado la sesion, hacemos lo siguiente:
		if(logout != null){
			// Pasamos a la vista el atributo "success" con un mensaje de éxito
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.login.logout' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			model.addAttribute("success",messageSource.getMessage("text.login.logout", null, locale));
		}
		
		// Tanto si se ha producido un error en el login o se ha hecho logout,devolvemos la vista "login"
		return "login";
	}
}
