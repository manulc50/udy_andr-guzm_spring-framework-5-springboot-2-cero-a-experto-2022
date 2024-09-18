package com.mlorenzo.springboot.webflux.app.controllers;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.mlorenzo.springboot.webflux.app.models.dao.ProductoDao;
import com.mlorenzo.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;

// Nuestras vistas implementadas con la varsión reactiva de Thymeleaf automáticamente se suscriben,creándose los Observadores,a los distintos flujos reactivos devueltos por los distintos métodos handler de este controlador

//Anotación para registar esta clase como un controlador de Spring.Esto conlleva a que se cree un bean y se almacene en su memoria o contenedor
@Controller
public class ProductoController {

	// Habilitamos el uso del log para esta clase
	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
	
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "ProductoDao".Esta interfaz es implementada por Spring al extender de la interfaz "ReactiveMongoRepository"
	@Autowired 
	private ProductoDao productoDao; // Este bean se trata del Dao para realizar CRUD en la colección "productos" mapeada con la clase entidad "Producto"
	
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/listar" y "/"(inicio)
	@GetMapping({"/listar","/"})
	public String listar(Model model){ // El objeto Model es necesario para pasar información a la vista asociada a la ruta "/listar"
		Flux<Producto> productos = productoDao.findAll() // Recuperamos de la capa Dao mediante el bean 'productoDao' el listado de productos como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono)
		// Con el método 'map' transformamos el flujo reactivo Flux anterior en otro flujo Flux con el nombre de cada producto en letras mayúsculas
		.map(producto -> { 
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		});
		// Nos suscribimos a este flujo reactivo,creándose un Observador,para realizar la tarea de escribir en el log a modo de información el nombre de cada producto emitido por este flujo reactivo
		productos.subscribe(prod -> log.info(prod.getNombre()));
		
		model.addAttribute("productos",productos); // Asociamos al atributo 'productos' el flujo reactivo Flux con los productos recuperado anteriormente de la capa Dao "productoDao" y se lo pasamos a la vista
		model.addAttribute("titulo","Listado de productos"); // Asociamos al atributo 'titulo' el texto 'Listado de productos' y se lo pasamos a la vista
		
		return "listar"; // Devolvemos el nombre de la vista a mostrar
	}
	
	/* En la versión reactiva de Thymeleaf,podemos configurar la contrapresión en la vistas para evitar cuellos de botella cuando el origen o fuente de los datos(este controlador) sufre demoras o hay una gran cantidad de datos.
	 * Existen 3 modos de manejar dicha contrapresión que vamos a ver en los siguientes métodos handler: Modo Data-Driver(bloques especificados por el número de elementos),Modo Chunked(bloques indicados por tamaño en bytes) y Modo Full(se trata del modo normal sin contrapresión)
	 */
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/listar-datadriver"
	// DataDriver es una forma recomendable de manejar la contrapresión en la vista que muestra los datos cuando estos sufren demoras o se trata de una gran cantidad de ellos.Nos permite mostrar directamente los datos en la vista por bloques(número de elementos del stream) en vez de esperar a que estén todos ellos listos para ser mostrados.
	// Los bloques se definen en número de elementos del stream y no en bytes(modo 'Chunked')
	@GetMapping("/listar-datadriver")
	public String listarDataDriver(Model model){ // El objeto Model es necesario para pasar información a la vista asociada a la ruta "/listar"
		Flux<Producto> productos = productoDao.findAll() // Recuperamos de la capa Dao mediante el bean 'productoDao' el listado de productos como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono)
		// Con el método 'map' transformamos el flujo reactivo Flux anterior en otro flujo Flux con el nombre de cada producto en letras mayúsculas
		.map(producto -> {
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		})
		// Damos un retardo de 1 segundo entre cada elemento del stream para simular un contexto 'DataDriver'
		.delayElements(Duration.ofSeconds(1)); 
		
		// Nos suscribimos a este flujo reactivo,creándose un Observador,para realizar la tarea de escribir en el log a modo de información el nombre de cada producto emitido por este flujo reactivo
		productos.subscribe(prod -> log.info(prod.getNombre()));
		
		// Con la instancia 'ReactiveDataDriverContextVariable' configuramos un contexto de tipo 'DataDriver' con bloques de 2 en 2 productos para ser mostrados en la vista
		// Dicha instancia la asociamos al atributo 'productos' y se lo pasamos a la vista
		model.addAttribute("productos",new ReactiveDataDriverContextVariable(productos,2));
		model.addAttribute("titulo","Listado de productos"); // Asociamos al atributo 'titulo' el texto 'Listado de productos' y se lo pasamos a la vista
		
		return "listar"; // Devolvemos el nombre de la vista a mostrar
	}
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/listar-full"
	// Método handler que simula un entorno con un stream o flujo de datos muy grande sin configurar ningún contexto 'DataDriver'(bloques especificados por el número de elementos), ni 'Chunked'(bloques indicados por tamaño en bytes)
	@GetMapping("/listar-full")
	public String listarFull(Model model){ // El objeto Model es necesario para pasar información a la vista asociada a la ruta "/listar"
		Flux<Producto> productos = productoDao.findAll() // Recuperamos de la capa Dao mediante el bean 'productoDao' el listado de productos como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono)
		// Con el método 'map' transformamos el flujo reactivo Flux anterior en otro flujo Flux con el nombre de cada producto en letras mayúsculas
		.map(producto -> {
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		// Repetimos los elementos del stream o flujo reactivo 5000 veces para simular un entorno con un stream de datos muy grande sin configurar nada, ni 'DataDriver'(bloques especificados por el número de elementos), ni 'Chunked'(bloques indicados por tamaño en bytes)
		}).repeat(5000);
		
		// Nos suscribimos a este flujo reactivo,creándose un Observador,para realizar la tarea de escribir en el log a modo de información el nombre de cada producto emitido por este flujo reactivo
		productos.subscribe(prod -> log.info(prod.getNombre()));
		
		model.addAttribute("productos",productos); // Asociamos al atributo 'productos' el flujo reactivo Flux con los productos recuperado anteriormente de la capa Dao "productoDao" y se lo pasamos a la vista
		model.addAttribute("titulo","Listado de productos"); // Asociamos al atributo 'titulo' el texto 'Listado de productos' y se lo pasamos a la vista
		
		return "listar"; // Devolvemos el nombre de la vista a mostrar
	}
	
	// Método handler que responde las peticiones http de tipo Get para las rutas "/listar-chunked"
	// Método handler que simula un enotrno con un stream o flujo de datos muy grande configurando un contexto 'Chunked'(bloques indicados por tamaño en bytes)
	@GetMapping("/listar-chunked")
	public String listarChunked(Model model){ // El objeto Model es necesario para pasar información a la vista asociada a la ruta "/listar-chunked"
		Flux<Producto> productos = productoDao.findAll() // Recuperamos de la capa Dao mediante el bean 'productoDao' el listado de productos como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono)
		// Con el método 'map' transformamos el flujo reactivo Flux anterior en otro flujo Flux con el nombre de cada producto en letras mayúsculas
		.map(producto -> {
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		// Repetimos los elementos del stream o flujo 5000 veces para simular un entorno con un stream de datos muy grande con configuración'Chunked'(bloques indicados por tamaño en bytes)
		}).repeat(5000);
		
		// Nos suscribimos a este flujo reactivo,creándose un Observador,para realizar la tarea de escribir en el log a modo de información el nombre de cada producto emitido por este flujo reactivo
		productos.subscribe(prod -> log.info(prod.getNombre()));
		
		model.addAttribute("productos",productos); // Asociamos al atributo 'productos' el flujo reactivo Flux con los productos recuperado anteriormente de la capa Dao "productoDao" y se lo pasamos a la vista
		model.addAttribute("titulo","Listado de productos"); // Asociamos al atributo 'titulo' el texto 'Listado de productos' y se lo pasamos a la vista
		
		return "listar-chunked"; // Devolvemos el nombre de la vista a mostrar
	}

}
