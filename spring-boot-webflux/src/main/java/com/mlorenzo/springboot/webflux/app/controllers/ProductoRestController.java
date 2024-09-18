package com.mlorenzo.springboot.webflux.app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mlorenzo.springboot.webflux.app.models.dao.ProductoDao;
import com.mlorenzo.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController  // Anotamos esta clase como un controlador Rest de Spring.Esta anotacion incluye la anotación @Controller y, además, pone la anotación @ResponseBody en cualquier método handler definido en esta clase
@RequestMapping("/api/productos") // Usamos como ruta base '/api/productos' para las peticiones http
public class ProductoRestController {
	
	// Habilitamos el uso del log para esta clase
	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
	
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "ProductoDao".Esta interfaz es implementada por Spring al extender de la interfaz "ReactiveMongoRepository"
	@Autowired 
	private ProductoDao productoDao; // Este bean se trata del Dao para realizar CRUD en la colección "productos" mapeada con la clase entidad "Producto"
	

	// Método handler que responde las peticiones http de tipo Get para la ruta base,es decir, '/api/productos'
	@GetMapping
	public Flux<Producto> index(){
		Flux<Producto> productos = productoDao.findAll() // Recuperamos de la capa Dao mediante el bean 'productoDao' el listado de productos como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono)
		// Con el método 'map' transformamos el flujo reactivo Flux anterior en otro flujo Flux con el nombre de cada producto en letras mayúsculas
		.map(producto -> {
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		})
		// Con el método "doOnNext()" indicamos que se realice la tarea de escribir en el log a modo de información el nombre de cada producto del flujo reactivo
		.doOnNext(prod -> log.info(prod.getNombre()));
		
		return productos; // Devolvemos el stream de tipo Flux con los productos como elementos
	}
	
	// Método handler que responde las peticiones http de tipo Get para la ruta '/api/productos/{id}'
	// Nota: El id es de tipo String debido a que la base de datos MongoDB usa identificadores alfanuméricos
	@GetMapping("/{id}")
	public Mono<Producto> show(@PathVariable(value="id") String id){ // Con la anotación @PathVariable recuperamos el id de la url o ruta
		/* Primera manera - Devolviendo directamente un Mono<Producto> desde la capa Dao
		Mono<Producto> producto = productoDao.findById(id);
		return producto;*/
		
		// Segunda manera - Obteniendo un Flux<Producto> desde la capa Dao y filtrando hasta obtener el producto cuyo id sea igual que el de la url
		Flux<Producto> productos = productoDao.findAll(); // Recuperamos de la capa Dao mediante el bean 'productoDao' el listado de productos como un stream reactivo de tipo Flux(varios items.Si fuese sólo un item,sería de tipo Mono)
		Mono<Producto> producto = productos.filter(p -> p.getId().equals(id)).next() // El método next() devuelve el primer elemento(producto) que satisface la condición del método filter() y no se sigue mirando por los siguientes elementos.Por ello,al final vamos a obtener un Mono<Producto> y no un Flux<Producto>
		.doOnNext(prod -> log.info(prod.getNombre())); // Con el método "doOnNext()" indicamos que se realice la tarea de escribir en el log a modo de información el nombre del producto filtrado
				
		return producto; // Devolveamos un Mono<Producto> con el producto localizado por el id
		
		
	}
	
	
}
