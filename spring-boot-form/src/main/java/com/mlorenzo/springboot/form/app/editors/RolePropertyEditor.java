package com.mlorenzo.springboot.form.app.editors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mlorenzo.springboot.form.app.services.RoleService;

// Editor o filtro personalizado que obtiene un objeto de tipo Role a partir del id enviado desde un elemento HTML tipo Checkbox

@Component
public class RolePropertyEditor extends PropertyEditorSupport {

	@Autowired
	private RoleService roleService;
	
	@Override
	public void setAsText(String idString) throws IllegalArgumentException {
		try {
			Integer id = Integer.parseInt(idString);
			setValue(roleService.obtenerPorId(id));
		}
		catch(NumberFormatException ex) {
			setValue(null);
		}
	}

	
}
