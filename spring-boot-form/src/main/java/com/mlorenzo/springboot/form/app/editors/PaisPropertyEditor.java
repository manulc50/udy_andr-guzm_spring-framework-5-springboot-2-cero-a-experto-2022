package com.mlorenzo.springboot.form.app.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mlorenzo.springboot.form.app.services.PaisService;

// Editor o filtro personalizado que obtiene un objeto de tipo Pais a partir del id enviado desde un elemento HTML tipo Select

@Component
public class PaisPropertyEditor extends PropertyEditorSupport {

	@Autowired
	private PaisService paisService;
	
	@Override
	public void setAsText(String idString) throws IllegalArgumentException {
		try {
			Integer id = Integer.parseInt(idString);
			setValue(paisService.obtenerPorId(id));
		}
		catch(NumberFormatException ex) {
			setValue(null);
		}
	}

}
