package com.mlorenzo.springboot.webflux.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.mlorenzo.springboot.webflux.app.models.dao.ProductoDao;
import com.mlorenzo.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;

/* A diferencia de las bases de datos relaciones donde teníamos un archivo import.sql(en resources) con los inserts iniciales para poblar las tablas con los datos iniciales,
 * en este caso,con la base de datos MongoDB(no relaciones y reactivas),para insertar los documentos iniciales en las colecciones de dicha base de datos es necesario hacerlo usando la interfaz "CommandLineRunner"
 */
//Esta interfaz nos permite ejecutar las tareas indicadas en el método run() antes de la aplicación Spring Boot desde el método main
@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner{
	
	// Habilitamos el uso de log en esta clase
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);
	
	// Recuperamos de la memoria o contendor de Spring el bean que implementa la interfaz "ProductoDao".Esta interfaz es implementada por Spring al extender de la interfaz "ReactiveMongoRepository"
	@Autowired
	private ProductoDao productoDao; // Este bean se trata del Dao para realizar CRUD en la colección "productos" mapeada con la clase entidad "Producto"
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate; // Este bean es propio de Spring y nos permite,entre otras cosas,eliminar una colección de una base de datos MongoDB
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Antes de insertar los datos de prueba en la colección "productos" de la base de datos MongoDB,eliminamos dicha colección con sus documentos anteriores para volver a creala a continuación
		// El método "dropCollection()" nos devuelve un flujo reactivo Mono,y por eso,tenemos que suscribirnos a este flujo para crear un Observador y se ejecute la tarea de eliminar la colección
		mongoTemplate.dropCollection("productos").subscribe();
		
		// Como estamos usando una base de datos MongoDB que es reactiva,definimos un flujo o stream reactivo Flux con los datos de prueba tipo Producto para insertarlos en la colección "productos"
		Flux.just(new Producto("TV Panasonic Pantalla LCD", 456.89),
				new Producto("Sony Camara HD Digital", 177.89),
				new Producto("Apple iPod", 46.89),
				new Producto("Sony Notebook", 846.89),
				new Producto("Hewlett Packard Multifuncional", 200.89),
				new Producto("Bianchi Bicicleta", 70.89),
				new Producto("HP Notebook Omen 17", 2500.89),
				new Producto("Mica Cómoda 5 Cajones", 150.89),
				new Producto("TV Sony Bravia OLED 4K Ultra HD", 2255.89))
		// Usamos el operador "flatMap" en vez de "map" ya que el método "save()" de nuestra capa Dao "productoDao" nos devuelve un flujo reactivo Modo de tipo Producto por cada producto que se acaba de insertar en la base de datos
		// Entonces,al final tendríamos un flujo reactivo Flux de elementos que son a su vez flujos reactivos Mono de tipo Producto y,por lo tanto,es necesario realizar un proceso de aplanamiento para obtener un único flujo reactivo Flux cuyos elementos sean directamente objetos no reactivos de tipo Producto
		.flatMap(producto ->{
			producto.setCreateAt(new Date()); // Registramos la fecha actual en la propiedad 'createAt' del producto antes de persistirlo en la base de datos
			return productoDao.save(producto); // Persistimos el objeto 'producto' en la base de datos
		}) 
		// Nos suscribimos,creándose un Observador de este flujo reactivo,y realizamos la tarea de escribir en el log a modo de información el nombre y el precio de cada elemento de tipo Producto emitido por este flujo reactivo
		.subscribe(producto -> log.info("Insert: " + producto.getNombre() + " " + producto.getPrecio()));
		
	}
}
