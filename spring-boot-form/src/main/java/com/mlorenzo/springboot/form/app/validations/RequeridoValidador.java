package com.mlorenzo.springboot.form.app.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

// Esta clase es un validador personalizado para ser usado mediante una anotación sobre propiedades de un cierto tipo de una clase

// Nota: Se tiene que indicar a la interfaz "ConstraintValidator" la clase Anotación y el tipo de dato de las propiedades a validar
// En nuestro caso, asociamos este validador personalizado con nuestra anotación "Requerido" para validar propiedades de tipo String

public class RequeridoValidador implements ConstraintValidator<Requerido, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		//return !value.isBlank();
		return StringUtils.hasText(value); // Otra alternativa equivalente
	}

}
