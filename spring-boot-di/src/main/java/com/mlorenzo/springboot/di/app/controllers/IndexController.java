package com.mlorenzo.springboot.di.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mlorenzo.springboot.di.app.models.service.IServicio;

@Controller
public class IndexController {
	
	@Autowired
	// A pesar de que la clase "MiServicio" o el método de configuración "miServicioSimple", que implementan la interfaz "IServicio", se corresponden con la implementación por defecto(es por defecto ya que tanto la clase como el método están anotados con @Primary) a la hora de inyectar un bean para esta interfaz, con la anotación @Qualifier pordemos indicar que se use otra implementación de dicha interfaz
	// En este caso, queremos usar la implementación de la interfaz "IServicio" de la clase "MiServicioComplejo" o del método de configuración "miServicioComplejo" en lugar de la implementación por defecto
	// Si no usamos esta anotación, por defecto se va a usar la implementación de la clase "MiServicio" o del método de configuración "miServicioSimple" porque están anotados con @Primary
	@Qualifier("miServicioComplejo") 
	private IServicio servicio;

	@GetMapping({"/","","/index"})
	public String index(Model model) {
		model.addAttribute("objeto",servicio.operacion());
		return "index";
	}

}
