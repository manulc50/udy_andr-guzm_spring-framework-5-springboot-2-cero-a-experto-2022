package com.mlorenzo.springboot.form.app.editors;

import java.beans.PropertyEditorSupport;

// Editor o filtro personalizado que pone en mayúscula y quita los espacios a uno o varios campos del formulario asociados a propiedades de tipo String

public class NombreMayusculaEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(text.toUpperCase().trim());
	}
	
	
}
