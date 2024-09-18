package com.mlorenzo.springboot.form.app.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// Esta clase es un validador personalizado para ser usado mediante una anotación sobre propiedades de un cierto tipo de una clase

// Nota: Se tiene que indicar a la interfaz "ConstraintValidator" la clase Anotación y el tipo de dato de las propiedades a validar
//       En nuestro caso, asociamos este validador personalizado con nuestra anotación "IdentificadorRegex" para validar propiedades de tipo String

public class IdentificadorRegexValidador implements ConstraintValidator<IdentificadorRegex, String> {

	// Valida que el valor de la propiedad String cumpla la expresión regular que se indica aquí
	// Ejemplo válido: "23.456.789-H"
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value.matches("[0-9]{2}[.][\\d]{3}[.][\\d]{3}[-][A-Z]{1}");
	}

}
