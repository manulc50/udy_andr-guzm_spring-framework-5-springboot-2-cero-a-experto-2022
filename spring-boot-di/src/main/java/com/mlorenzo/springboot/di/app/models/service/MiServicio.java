package com.mlorenzo.springboot.di.app.models.service;


//@Component("miServicioSimple")
//@Primary // Como tenemos 2 implementaciones de la interfaz "IServicio", con esta anotación indicamos a Spring la implementación por defecto que debe usar cuando se vaya a inyectar un bean de la interfaz "IServicio" en otra parte del proyecto 
public class MiServicio implements IServicio {

	@Override
	public String operacion() {
		return "Ejecutando algún proceso importante simple...";
	}

	
}
