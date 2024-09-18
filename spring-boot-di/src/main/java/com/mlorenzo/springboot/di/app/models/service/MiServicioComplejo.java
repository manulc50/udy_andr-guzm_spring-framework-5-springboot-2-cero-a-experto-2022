package com.mlorenzo.springboot.di.app.models.service;

// @Component("miServicioComplejo")
public class MiServicioComplejo implements IServicio {

	@Override
	public String operacion() {
		return "Ejecutando algún proceso importante complicado...";
	}

	
}
