package com.mlorenzo.springboot.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mlorenzo.springboot.app.models.service.IClienteService;
import com.mlorenzo.springboot.app.view.xml.ClienteList;

@RestController // Esta anotación indica que esta clase se trata de un Controlador Rest de Spring  para que lo almacene en su contenedor como un bean y lo gestione.Esta anotación incluye la anotación @Controller y además pone la anotación @ResponseBody en cualquier método definido en esta clase
@RequestMapping("/api/clientes") // Todas las peticiones a este controlador tienen que ser a partir de la ruta base '/api/clientes'
public class ClienteRestController {
	
	// Recuperamos del contenedor o memoria de Spring el bean que implmenta nuestra interfaz 'IClienteService'.Esta interfaz es implementada por nuestra clase 'ClienteServiceImpl'
	@Autowired
	private IClienteService clienteService; // Este bean se corresponde con nuestro servicio que implementa y gestiona todo lo relacionado con nuestra entidad Cliente
	
	// Metodo handler de tipo Rest(devuelve el objeto 'ClienteList', y no el nombre de una vista a renderizar) que escucha las peticiones http de tipo Get a la ruta '/apli/clientes/listar'
	// No hace falta poner la anotación @ResponseBody a nivel de método ya que se hereda de la anotacion a nivel de clase @RestController
	// Tal y como está ahora,por defecto los datos de los clientes se van a mostrar en formato xml
	// Si se desea mostrar los datos en formato json, manualmente hay que añadir el parámetro "format" con el valor "json" a la url, es decir, la url sería '.../api/clientes/listar?format=json'
	// También se muestran los datos en formato xml si manualmente se añade el parámetro "format" con el valor "xml" a la url, es decir, la url sería '.../api/clientes/listar?format=xml'
	@GetMapping(value = "/listar")
	public ClienteList listar(){
		// Obtiene y devuelve los datos de todos los clientes recuperados de la base de datos a través de nuestro servicio 'clienteService'
		return new ClienteList(clienteService.findAll()); // ClienteList es la clase envoltorio o wrapper para que funcione la renderizacion, en una vista xml, de la informacion de los clientes cuando estan almacenados en listas o colecciones.Si se quiere que este metodo sea valido tanto para json como para xml,hay que usar esta clase envoltorio.Si solo fuera para json,no haria falta,se devolveria directamente un List<Cliente>
	}

}
