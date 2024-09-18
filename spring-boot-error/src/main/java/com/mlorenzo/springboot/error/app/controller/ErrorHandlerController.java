package com.mlorenzo.springboot.error.app.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mlorenzo.springboot.error.app.errors.UsuarioNoEncontradoException;

// La anotación @ControllerAdvice indica que la clase es un manejador de errores o excepciones, es decir, capturan esas excepciones y son manejadas por los método handler implementeados en esta clase
// A diferencia de una clase anotada con @Controller o @RestController donde sus métodos handler se mapean con rutas, aquí los métodos handler se mapean con excepciones o errores

@ControllerAdvice // Esta anotación también incluye la anotación @Componente y, por lo tanto, Spring va a almacenar en su contenedor un bean de esta clase para porder gestionarlo
public class ErrorHandlerController {
	
	// El parámetro de entrada "ex" de tipo ArithmeticException se corresponde con la excepción producida de tipo ArithmeticException
	// El parámetro de entrada "model" de tipo Model nos permite crear y enviar atributos con información a la vista(html)
	// Con la anotación @ExceptionHandler indicamos que este método handler se va a ejecutar cuando se produzcan exceptiones de tipo ArithmeticException
	@ExceptionHandler(ArithmeticException.class) // A esta anotación también se le puede parar un array(con las llaves {}) de tipos de excepciones para que este método handler maneje más de un tipo de excepción 
	public String aritmeticaException(ArithmeticException ex,Model model) {
		model.addAttribute("error","Error de aritmética");
		model.addAttribute("message",ex.getMessage());
		model.addAttribute("status",HttpStatus.INTERNAL_SERVER_ERROR.value()); // Error 500(Internal Server Error)
		model.addAttribute("timestamp",new Date());
		
		// Devolvemos la vista(html) "generica" que se encuentra dentro de la carpeta "error"
		return "error/generica";	
	}
	
	// El parámetro de entrada "ex" de tipo NumberFormatException se corresponde con la excepción producida de tipo NumberFormatException
	// El parámetro de entrada "model" de tipo Model nos permite crear y enviar atributos con información a la vista(html)
	// Con la anotación @ExceptionHandler indicamos que este método handler se va a ejecutar cuando se produzcan exceptiones de tipo NumberFormatException
	@ExceptionHandler(NumberFormatException.class) // A esta anotación también se le puede parar un array(con las llaves {}) de tipos de excepciones para que este método handler maneje más de un tipo de excepción 
	public String numberFormatException(NumberFormatException ex,Model model) {
		model.addAttribute("error","Error: Formato número inválido!");
		model.addAttribute("message",ex.getMessage());
		model.addAttribute("status",HttpStatus.INTERNAL_SERVER_ERROR.value()); // Error 500(Internal Server Error)
		model.addAttribute("timestamp",new Date());
		
		// Devolvemos la vista(html) "generica" que se encuentra dentro de la carpeta "error"
		return "error/generica";	
	}
	
	// El parámetro de entrada "ex" de tipo UsuarioNoEncontradoException se corresponde con nuestra excepción personalizada que se produce cuando se intenta obtener un usuario que no existe en el sistema
	// El parámetro de entrada "model" de tipo Model nos permite crear y enviar atributos con información a la vista(html)
	// Con la anotación @ExceptionHandler indicamos que este método handler se va a ejecutar cuando se produzcan exceptiones de tipo UsuarioNoEncontradoException
	@ExceptionHandler(UsuarioNoEncontradoException.class) // A esta anotación también se le puede parar un array(con las llaves {}) de tipos de excepciones para que este método handler maneje más de un tipo de excepción 
	public String usuarioNoEncontradoException(UsuarioNoEncontradoException ex,Model model) {
		model.addAttribute("error","Error: Usuario no encontrado!");
		model.addAttribute("message",ex.getMessage());
		model.addAttribute("status",HttpStatus.NOT_FOUND.value()); // Error 404(Not Found)
		model.addAttribute("timestamp",new Date());
		
		// Devolvemos la vista(html) "generica" que se encuentra dentro de la carpeta "error"
		return "error/generica";	
	}

}
