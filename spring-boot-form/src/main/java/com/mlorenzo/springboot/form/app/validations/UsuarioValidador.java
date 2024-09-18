package com.mlorenzo.springboot.form.app.validations;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.mlorenzo.springboot.form.app.models.Usuario;

// Nota: En vez de usar anotaciones de validación, podemos implementar nuestras validaciones personalizadas como hacemos en esta clase

@Component
public class UsuarioValidador implements Validator {

	// Aquí indicamos el tipo de objeto que queremos validar
	// En este caso, queremos validar objetos de tipo "Usuario"
	@Override
	public boolean supports(Class<?> clazz) {
		return Usuario.class.isAssignableFrom(clazz);
	}

	// Método que realiza las validaciones
	@Override
	public void validate(Object target, Errors errors) {
		//Usuario usuario = (Usuario)target;
		
		// En el objeto "errors" se registran los errores de validación
		
		// Valida el campo "nombre" del usuario y muestra el mensaje de error asociado a la propiedad "requerido.usuario.nombre" del archivo de propiedades "messages.properties" en caso de error de validaión
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nombre", "requerido.usuario.nombre");
		
		// Otra forma equivalente
		/*if(usuario.getNombre().isEmpty())
			errors.rejectValue("nombre", "NotEmpty.user.nombre");*/
		
		// Valida el campo "idEmpleado" del usuario y muestra el mensaje de error asociado a la propiedad "pattern.usuario.identificador" del archivo de propiedades "messages.properties" en caso de error de validaión
		// Ejemplo válido: "23.456.789-H"
		// Se comenta porque ahora usamos nuestro validador personalizado de la clase "IdentificadorRegexValidator" mediante nuestra anotación "IdentificadorRegex"
		/*if(!usuario.getIdEmpleado().matches("[0-9]{2}[.][\\d]{3}[.][\\d]{3}[-][A-Z]{1}"))
			errors.rejectValue("idEmpleado", "pattern.usuario.identificador"); */
	}


}
