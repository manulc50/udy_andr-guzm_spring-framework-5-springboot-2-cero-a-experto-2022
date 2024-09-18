package com.mlorenzo.springboot.reactor.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mlorenzo.springboot.reactor.app.model.Usuario;

import reactor.core.publisher.Flux;

// Para que esta aplicación de Spring Boot sea de escritorio o de línea de comandos, tiene que implementar la interfaz CommandLineRunner

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner{
	
	// Habilitamos el uso del log en esta clase
	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	/* El patrón de diseño 'Observable' indica que hay uno o varios sujetos observables(Por ejemplo,' Observable<Cliente>' - Cliente sería un sujeto de Observable)
	 * que son escuchados o observados por los observadores(se suscriben a los observables) y que cuando sufren algún tipo de cambio(los observables),los observadores
	 * ejecutan una determinada tarea.
	 * Con este patrón, se trabaja de forma reactiva, que es asíncrona y no bloqueante.*/
	@Override
	public void run(String... args) throws Exception {
		// Un flujo reactivo de datos de tipo "Flux" maneja una colección de 0 a N elementos
		// Un flujo reactivo de datos de tipo "Mono" maneja una colección de 0 a 1 elementos
		
		/* Ejemplo 1  - Creación de un flujo reactivo Flux con el método "just()" */
		// ejemploCrearFluxJust();
		/* Ejemplo 2 - Método "subscribe()" */
		// ejemploSubscribe();
		/* Ejemplo 3 - Método "subscribe()" con gestión de errores */
		// ejemploSubscribeError();
		/* Ejemplo 4 - Método "subscribe()" con evento onComplete */
		// ejemploSubscribeOnComplete();
		/* Ejemplo 5 - Operador Map - Primero "doOnNext" y después "map" */
		// ejemploDoOnNextMap();
		/* Ejemplo 5 - Operador Map - Primero "map" y después "doOnNext" */
		// ejemploMapDoOnNext();
		/* Ejemplo 6 - Operador Map - Primero "map",después "doOnNext" y de nuevo "map" */
		// ejemploMapDoOnNextMap();
		/* Ejemplo 7 - Operador Map - Pasar de un stream de tipo String a uno de tipo Usuario */
		// ejemploMapString2Usuario();
		/* Ejemplo 8 - Operador Filter */
		// ejemploFilter();
		/* Ejemplo 9 - Los streams son inmutables */
		// ejemploInmutables();
		/* Ejemplo 10 - Crear un flujo reactivo Flux a partir de una lista */
		 ejemploCrearFluxFromIterable();

	}
	
	private void ejemploCrearFluxJust(){
		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				.doOnNext(System.out::println); // Con el método "doOnNext()" indicamos que se muestre por pantalla cada elemento,que es un nombre,emitido por el Observable "nombres".'System.out::println' hace referencia al método 'println' de 'System.out' y es una manera más simplificada de poner la sentencia 'elemento -> System.out.println(elemento)' 
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		nombres.subscribe();
		
	}
	
	private void ejemploSubscribe() {
		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				.doOnNext(System.out::println); // Con el método "doOnNext()" indicamos que se muestre por pantalla cada elemento,que es un nombre,emitido por el Observable "nombres".'System.out::println' hace referencia al método 'println' de 'System.out' y es una manera más simplificada de poner la sentencia del ejemplo 1 'elemento -> System.out.println(elemento)' 
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		nombres.subscribe(log::info); // Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'*/
	}
	
	private void ejemploSubscribeError() {
		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(e -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(e.isEmpty())  // Comprueba si cada elemento del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(e); // Por cada elemento del stream que no sea vacío, se imprime por pantalla
				});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		nombres.subscribe(log::info,error -> log.error(error.getMessage()));
	}
	
	private void ejemploSubscribeOnComplete() {
		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Maria","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(e -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(e.isEmpty())  // Comprueba si cada elemento del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(e); // Por cada elemento del stream que no sea vacío, se imprime por pantalla
				});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		nombres.subscribe(log::info,error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploDoOnNextMap() {
		 /* En este caso, se ejecuta primero lo indicado en el método 'doOnNext' y después,en segundo lugar,se ejecuta lo indicado en el método 'map'(esto es así porque sigue un orden,primero esta el método 'doOnNext' y luego 'map').Y por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Maria","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(e -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(e.isEmpty())  // Comprueba si cada elemento del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(e); // Por cada elemento del stream que no sea vacío, se imprime por pantalla
				})
				// Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a uppercase,es decir,todos sus caracteres en mayúscula
				.map(e -> e.toUpperCase()); // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		nombres.subscribe(log::info,error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploMapDoOnNext() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map' y después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext'(esto es así porque sigue un orden,primero esta el método 'map' y luego 'doOnNext').Y por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Maria","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				// Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a uppercase,es decir,todos sus caracteres en mayúscula
				.map(e -> e.toUpperCase()) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(e -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(e.isEmpty())  // Comprueba si cada elemento del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(e); // Por cada elemento del stream que no sea vacío, se imprime por pantalla
				});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		nombres.subscribe(log::info,error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploMapDoOnNextMap() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres","Pedro","Maria","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				// Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a uppercase,es decir,todos sus caracteres en mayúscula
				.map(e -> e.toUpperCase()) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(e -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(e.isEmpty())  // Comprueba si cada elemento del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(e); // Por cada elemento del stream que no sea vacío, se imprime por pantalla
				})
				.map(nombre -> nombre.toLowerCase()); // Volvemos a convertir cada elemento(cadena de caracteres) a lowercase(minúscula)
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información cada nombre emitido por el Observable "nombres".'log::info' es la manera simplificada de poner la función lambda 'e -> log.info(e)'
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		nombres.subscribe(log::info,error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploMapString2Usuario() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos un flujo reactivo de tipo Flux de objetos Usuario.Este flujo es un Observable
		Flux<Usuario> usuarios = Flux.just("Andres","Pedro","Maria","Diego","Juan") // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				 // Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a un objeto Usuario con el nombre en uppercase,es decir,todos sus caracteres en mayúscula,y sin apellidos.Estamos transformando un stream<string> a un stream<Usuario>
				.map(nombre -> new Usuario(nombre.toUpperCase(),null)) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(usuario -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(usuario == null)  // Comprueba si cada usuario del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(usuario.getNombre()); // Por cada usuario del stream que no sea vacío, se imprime su nombre por pantalla
				})
				.map(usuario ->{ // Volvemos a convertir el nombre cada Usuario a lowercase(minúscula)
	    			String nombre = usuario.getNombre().toLowerCase();
	    			usuario.setNombre(nombre);
	    			return usuario;
	    		});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "usuarios"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información los datos de cada usuario emitido por el Observable "usuarios"
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		usuarios.subscribe(usuario -> log.info(usuario.toString()),error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploFilter() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/
		
		// Creamos un flujo reactivo de tipo Flux de objetos Usuario.Este flujo es un Observable
		Flux<Usuario> usuarios = Flux.just("Andres Guzman","Pedro Fulano","María Fulana","Diego Mengano","Juan Lorenzo","Bruce Willis", "Bruce Lee")  // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
				 // Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a un objeto Usuario con el nombre y el apellido en mayúscula.Estamos transformando un stream<string> a un stream<Usuario>
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase())) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// Filtramos cada usuario del stream para quedarnos sólo con aquellos cuyo nombre sea 'bruce' ignorando minúsculas y mayúsculas
				.filter(usuario -> usuario.getNombre().equalsIgnoreCase("bruce"))
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(usuario -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(usuario == null)  // Comprueba si cada usuario del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido())); // Por cada usuario del stream que no sea vacío, se imprime  por pantalla su nombre y su apellido
				})
				.map(usuario ->{ // Volvemos a convertir el nombre cada Usuario a lowercase(minúscula)
	    			String nombre = usuario.getNombre().toLowerCase();
	    			usuario.setNombre(nombre);
	    			return usuario;
	    		});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "usuarios"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información los datos de cada usuario emitido por el Observable "usuarios"
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		usuarios.subscribe(usuario -> log.info(usuario.toString()),error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploInmutables() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos un flujo reactivo de tipo Flux de Strings.Este flujo es un Observable
		Flux<String> nombres = Flux.just("Andres Guzman","Pedro Fulano","María Fulana","Diego Mengano","Juan Lorenzo","Bruce Willis", "Bruce Lee");  // Usamos el método "just()" que nos permite crear un flujo reactivo a partir de una fuente de datos puesta directamente "a pelo" separados por comas
		
		// Ahora,desde aquí hasta la parte indicada con la línea discontinua se corresponde con otro flujo o stream.Ya no forma parte del flujo inicial creado arriba */
		// Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a un objeto Usuario con el nombre y el apellido en mayúscula.Estamos transformando un stream<string> a un stream<Usuario>
		nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase())) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
			// Filtramos cada usuario del stream para quedarnos sólo con aquellos cuyo nombre sea 'bruce' ignorando minúsculas y mayúsculas
			.filter(usuario -> usuario.getNombre().equalsIgnoreCase("bruce"))
			// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
			.doOnNext(usuario -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
				if(usuario == null)  // Comprueba si cada usuario del stream es vacío.
					throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
				System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido())); // Por cada usuario del stream que no sea vacío, se imprime  por pantalla su nombre y su apellido
			})
			.map(usuario ->{ // Volvemos a convertir el nombre cada Usuario a lowercase(minúscula)
	    		String nombre = usuario.getNombre().toLowerCase();
	    		usuario.setNombre(nombre);
	    		return usuario;
	    	});
		
		// ---------------------------------------------
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "nombres"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información los datos de cada usuario emitido por el Observable "nombres"
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		nombres.subscribe(usuario -> log.info(usuario.toString()),error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
	
	private void ejemploCrearFluxFromIterable() {
		/* En este caso, se ejecuta primero lo indicado en el método 'map',después,en segundo lugar,se ejecuta lo indicado en el método 'doOnNext' y después,en tercer lugar, se ejecuta lo indicado en el otro 'map'(esto es así porque sigue un orden,primero esta el método 'map',luego 'doOnNext' y,por último, el otro 'map').Por último, se ejecuta la tarea del Observador con cada elemento del stream ya transformado
		 * Los Observales son inmutables,es decir,que las transfromaciones indicadas en el método 'map' realmente no afectan al estado incial de los elemento de cada stream,sino que se hacen sobre instancias nuevas para no alterar dicho estado*/

		// Creamos una lista de nombres
		List<String> nombres = new ArrayList<String>();
		nombres.add("Andres Guzman");
		nombres.add("Pedro Fulano");
		nombres.add("María Fulana");
		nombres.add("Diego Mengano");
		nombres.add("Juan Lorenzo");
		nombres.add("Bruce Willis");
		nombres.add("Bruce Lee");
		
		// Creamos un flujo reactivo de tipo Flux de objetos Usuario.Este flujo es un Observable
		Flux<Usuario> usuarios = Flux.fromIterable(nombres)  // Usamos el método "fromIterable()" que nos permite crear un flujo reactivo a partir de una lista de datos
				 // Con el método 'map' podemos transformar cada elemento del stream.En este caso,cada elemento del stream,que es una cadena de caracteres,la transformamos a un objeto Usuario con el nombre y el apellido en mayúscula.Estamos transformando un stream<string> a un stream<Usuario>
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase())) // Como los Observables son inmutables,ningún operador como "map" puede modificar o alterar los datos del Observable inicial.Lo que hace el oeprador "map" es crear un Observable nuevo con el resultado final de la operación sin alterar el Observable inicial
				// Filtramos cada usuario del stream para quedarnos sólo con aquellos cuyo nombre sea 'bruce' ignorando minúsculas y mayúsculas
				.filter(usuario -> usuario.getNombre().equalsIgnoreCase("bruce"))
				// Con el método "doOnNext()" indicamos que se haga una determinada tarea por cada elemento emitido por el Observable
				.doOnNext(usuario -> { // Cuando una función anónima de tipo flecha tiene más de una sentencia, hay que poner {}
					if(usuario == null)  // Comprueba si cada usuario del stream es vacío.
						throw new RuntimeException("El nombre no puede ser vacío"); // Y en caso afirmativo,lanza una excepción de tipo RuntimeException asociado a un mensaje personalizado.En este punto se suspende la ejecución al tratarse de una excepción de tipo "RuntimeException"
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido())); // Por cada usuario del stream que no sea vacío, se imprime  por pantalla su nombre y su apellido
				})
				.map(usuario ->{ // Volvemos a convertir el nombre cada Usuario a lowercase(minúscula)
	    			String nombre = usuario.getNombre().toLowerCase();
	    			usuario.setNombre(nombre);
	    			return usuario;
	    		});
		
		// Si un Observable no tiene un Observador suscrito,no se va a ejecutar la cadena de ejecución indicada en el Observable.
		// El método "subscribe()" nos permite crear un Observador de un Observable
		// Creamos un Observador del Observable "usuarios"
		// Al método "subscribe()" se le puede pasar una función lambda para que haga una determinada tarea por cada elemento recibido del Observale.En este caso,imprime en el log a modo de información los datos de cada usuario emitido por el Observable "usuarios"
		// Si se le pasa un segundo parámetro al método 'subscribe()', es para gestionar errores producidos en el stream emitido por el Observable.En este caso,cuando se produce un error, se ejecuta la función anónima de tipo flecha 'error -> log.error(error.getMessage())' para imprimir en el log a modo error el mensaje de la excepción producida
		// Si se le pasa un tercer parámetro al método 'subscribe()', es para lanzar un hilo('Runnable') que ejecute una tarea cuando el Observable ha terminado de emitir el último elemento del stream
		usuarios.subscribe(usuario -> log.info(usuario.toString()),error -> log.error(error.getMessage()),new Runnable(){ 

	    	// En este caso,cuando el Observable ha terminado de emitir el último elemento del stream,se va a mostrar en el log a modo de información un mensaje
			@Override
			public void run() {
				log.info("El Observable ha finalizado su ejecución con éxito.");
				
			}
	    	
	    });
	}
}
