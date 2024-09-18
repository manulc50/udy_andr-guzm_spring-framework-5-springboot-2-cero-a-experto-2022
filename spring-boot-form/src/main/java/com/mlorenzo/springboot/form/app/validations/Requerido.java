package com.mlorenzo.springboot.form.app.validations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
// Con esta anotación "@Constraint" asociamos esta anotación con nuestro validador personalizado "RequeridoValidador"
@Constraint(validatedBy = RequeridoValidador.class)
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Requerido {
	// Mensaje de error de validación por defecto
	String message() default "El campo es requerido - usando anotaciones";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
