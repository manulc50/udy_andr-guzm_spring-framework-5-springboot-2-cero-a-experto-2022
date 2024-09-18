package com.mlorenzo.springboot.di.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.mlorenzo.springboot.di.app.models.domain.ItemFactura;
import com.mlorenzo.springboot.di.app.models.domain.Producto;
import com.mlorenzo.springboot.di.app.models.service.IServicio;
import com.mlorenzo.springboot.di.app.models.service.MiServicio;
import com.mlorenzo.springboot.di.app.models.service.MiServicioComplejo;

@Configuration
public class AppConfig {
	
	@Primary // Como tenemos 2 implementaciones de la interfaz "IServicio", con esta anotación indicamos a Spring la implementación por defecto que debe usar cuando se vaya a inyectar un bean de la interfaz "IServicio" en otra parte del proyecto 
	@Bean("miServicioSimple")
	public IServicio miServicioSimple() {
		return new MiServicio();
	}
	
	@Bean("miServicioComplejo")
	public IServicio miServicioComplejo() {
		return new MiServicioComplejo();
	}
	
	@Primary
	@Bean("itemsFactura")
	public List<ItemFactura> registrarItems(){
		Producto producto1 = new Producto("Cámara Sony",100.50f);
		Producto producto2 = new Producto("Bicicleta Bianchi aro 26",200.70f);
		
		ItemFactura linea1 = new ItemFactura(producto1,2);
		ItemFactura linea2 = new ItemFactura(producto2,4);
		
		return Arrays.asList(linea1,linea2);
	}
	
	@Bean("itemsFacturaOficina")
	public List<ItemFactura> registrarItemsOficina(){
		Producto producto1 = new Producto("Monitor LG LCD 24",250.54f);
		Producto producto2 = new Producto("Notebook Asus",500.70f);
		Producto producto3 = new Producto("Impresora HP Multifuncional",80.25f);
		Producto producto4 = new Producto("Escritorio Oficina",300.60f);
		
		ItemFactura linea1 = new ItemFactura(producto1,2);
		ItemFactura linea2 = new ItemFactura(producto2,4);
		ItemFactura linea3 = new ItemFactura(producto3,1);
		ItemFactura linea4 = new ItemFactura(producto4,1);
		
		return Arrays.asList(linea1,linea2,linea3,linea4);
	}

}
