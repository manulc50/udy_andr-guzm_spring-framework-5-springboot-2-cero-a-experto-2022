package com.mlorenzo.springboot.backend.apirest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mlorenzo.springboot.backend.apirest.models.entity.Cliente;
import com.mlorenzo.springboot.backend.apirest.models.services.IClienteService;

// @CrossOrigin es una anotación que indica qué clientes(órigenes) pueden realizar peticiones a este servidor,qué tipos de peticiones(Get,Post,Put,...) pueden realizar,se puede especificar restricciones en la cabecera,...,entre otras cosas.Es decir,no pone restricciones de consumo de esta API Rest a las peticiones cuyos orígenes esten indicados en 'origins'
@CrossOrigin(origins="http://localhost:4200") // 'http://localhost:4200' es la ruta donde se ejecuta nuestra aplicación cliente en Angular(origen de la petición).Con esto,le damos acceso a nuestra aplicación cliente para que pueda soliciar a este backend o servidor peticiones de todo tipo(aunque este servidor solo escucha peticiones de tipo Get)
@RestController // Esta anotación indica que esta clase se trata de un Controlador Rest de Spring  para que lo almacene en su contenedor como un bean y lo gestione.Esta anotación incluye la anotación @Controller y además pone la anotación @ResponseBody en cualquier método definido en esta clase
@RequestMapping("/api") // Todas las peticiones a este controlador tienen que ser a partir de la ruta base '/api'
public class ClienteRestController {
	
	// Recuperamos del contenedor de Spring el bean correspondiente que contiene la implementación de esta interfaz.En este caso,el bean que la implementa es de tipo ClienteServiceImpl
	@Autowired
	private IClienteService clienteService; // Este bean se corresponde con la capa de servico para nuestra entidad Cliente
	
	// Metodo handler de tipo Rest(devuelve el objeto 'List<Cliente>', y no el nombre de una vista a renderizar) que responde a las peticiones http de tipo Get '/api/clientes'
	// No hace falta poner @ResponseBody a nivel de metodo ya que se hereda de la anotacion a nivel de clase @RestController
	// Si no se especifica con la anotación @ResponseStatus ningun estado de error con "HttpStatus",por defecto es 'Httpstatus.OK' -> codigo 200 
	@GetMapping(value = "/clientes")
	public List<Cliente> index(){
		// Realiza una consulta a la base de datos para obtener la información de todos los clientes mediante nuestro servicio 'clienteService'
		return clienteService.findAll();
	}
	
	// Metodo handler de tipo Rest(devuelve el objeto ' ResponseEntity<?>', y no el nombre de una vista a renderizar) que responde a las peticiones http de tipo Get '/api/cliente/{id}'
	// Para poder manejar errores,por ejemplo, cuando consultamos por un id que no existe en la base de datos,Spring nos proporciona la entidad ResponseEntity que nos permite devolver diferentes estados de ejecución para casos correctos y erróneos
	// Devolvemos un ResponseEntity<?>(? -> De cualquier tipo) ya que, si el resultado es correcto,vamos a devolver un ResponseEntity<Cliente>,pero si el resultado no es correcto,vamos a devolver un ResponseEntity<Map<String,Object>> con el mensaje de error
	// Con @PathVariable recuperamos el id de la url. Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	@GetMapping(value = "/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable(value = "id") Long id){
		// Cliente a devolver en un ResponseEntity en caso de localizarse en la base de datos
		Cliente cliente = null;
		 // Map para almacenar aquella información(mensajes,objetos,etc...) que se quiere enviar al usuario en la respuesta de la petición
		Map<String,Object> response = new HashMap<String,Object>();
		
		// try-catch para probar el acceso y conexión a la base de datos.Spring gestiona esto a través de la entidad 'DataAccessException'
		try{
			// Realiza una consulta a la base de datos para obtener el cliente a partir del id recuperado de la url mediante nuestro servicio 'clienteService'
			cliente = clienteService.findById(id);
		}
		catch(DataAccessException e){
			// Insertamos en el map un mensjae de error asociado al atributo 'mensaje'
			response.put("mensaje","Error en el acceso a la base de datos");
			// Insertamos en el map información relativa a la excepción ocurrida en el acceso a la base de datos asociada al atributo 'error'
			response.put("error",e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
			// Devolvemos un  ResponseEntity<Map<String,Object>> con el map que contiene los mensajes de error y el estado de error INTERNAL_SERVER_ERROR(500)
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// Si no se ha encontrado al cliente en la base de datos,hacemos lo siguiente:
		if(cliente == null) {
			// Insertamos en el map un mensaje personalizado de error asociado al atributo 'mensaje'
			response.put("mensaje","El cliente ID ".concat(id.toString()).concat(" no existe en la base de datos!")); // La función 'concat()' de String hace lo mismo que el operador '+' en strings
			// Devolvemos un  ResponseEntity<Map<String,Object>> con el map que contiene el mensaje de error y el estado de error NOT_FOUND(400)
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		// Si todo ha ido bien en la localización del cliente en la base de datos,devolvemos un ResponseEntity<Cliente> con un objeto Cliente que contiene sus datos y el estado OK(200)
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
	}
	
	// Metodo handler de tipo Rest(devuelve el objeto 'ResponseEntity<Map<String,Object>>', y no el nombre de una vista a renderizar) que responde a las peticiones http de tipo Post '/api/clientes'
	// Para poder manejar errores,por ejemplo, a la hora de insertar en la base de datos un nuevo cliente y este posee un email nulo y en la tabla de la base de datos no pude serlo,Spring nos proporciona la entidad ResponseEntity que nos permite devolver diferentes estados de ejecución para casos correctos y erróneos
	// La anotación @RequestBody es para indicar que el objeto 'cliente' va a llegar al metodo en formato Json y que Spring lo tiene que mapear a una instancia de tipo 'Cliente'
	// @ Valid es para que se validen,antes de ejecutarse el método, las restricciones de validación(indicada por anotaciones) del objeto 'cliente' que recibimos cuya entidad es 'Cliente'
	// El objeto 'result' de tipo 'BindingResult' contiene la información sobre el resultado de las validaciones
	// Nota: Es importante que el objeto result' de tipo 'BindingResult' esté justo a continuación del objeto que se quiere validar.En este caso,'cliente' de tipo 'Cliente'
	@PostMapping(value="/clientes")
	public ResponseEntity<Map<String,Object>> create(@Valid @RequestBody Cliente cliente,BindingResult result){
		// Cliente a devolver en un ResponseEntity en caso de crearse correctamente en la base de datos
		Cliente clienteNew = null;
		// Map para almacenar aquella información(mensajes,objetos,etc...) que se quiere enviar al usuario en la respuesta de la petición
		Map<String,Object> response = new HashMap<String,Object>();
		
		// Si hay errores de validación, hacemos lo siguiente:
		if(result.hasErrors()){
			/* Primera manera,usando programación 'clásica'(anterior a Java 8)
			// Creamos un arrayList para almacenar los errores en formato string
			List<String> errors = new ArrayList<String>();
			
			// Recorremos la lista de errores producidos durante la validación
			for(FieldError error:result.getFieldErrors())
				// Para cada error, almacenamos en el arraylist la información relativa al campo donde se ha producido dicho error y su correpndiente mensaje 
				errors.add("El campo '" + error.getField() + "' " +  error.getDefaultMessage());
			*/
			
			// Segunda manera,usando streams y progrmación funcional de Java 8
			// De la lista de elementos 'FieldError', que nos devuelve el método 'getFieldErrors()', creamos un stream de elementos 'FieldError' con el método 'stream()' y con el método 'map()' convertimos cada elemento de tipo 'FieldError' en elementos de tipo String con el contenido '"El campo '" + error.getField() + "' " +  error.getDefaultMessage()'.Por último, agrupamos todos los elementos convertidos en String en una lista de tipo String usando el método 'collect()' y el tipo de colección 'Collectors.toList()''
			List<String> errors = result.getFieldErrors().stream().map(error -> "El campo '" + error.getField() + "' " +  error.getDefaultMessage()).collect(Collectors.toList());
			// Insertamos en el map el arraylist de errores en formato string
			response.put("errors",errors);
			// Devolvemos un  ResponseEntity<Map<String,Object>> con el map que contiene el arraylist de mensajes de error y el estado de error BAD_REQUEST(400)
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		// try-catch para probar el acceso y conexión a la base de datos.Spring gestiona esto a través de la entidad 'DataAccessException'
		try{
			// Creamos un nuevo cliente en la base de datos a partir de la informacion almacenada en la instancia 'cliente' mediante nuestro servicio 'clienteService'
			clienteNew = clienteService.save(cliente);
		}
		catch(DataAccessException e){
			// Insertamos en el map un mensjae de error asociado al atributo 'mensaje'
			response.put("mensaje","Error al realizar el insert en la base de datos");
			// Insertamos en el map información relativa a la excepción ocurrida en el acceso a la base de datos asociada al atributo 'error'
			response.put("error",e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
			// Devolvemos un  ResponseEntity<Map<String,Object>> con el map que contiene los mensajes de error y el estado de error INTERNAL_SERVER_ERROR(500)
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// Si todo ha ido bien,es decir, si se ha podido crear correctamente el nuevo cliente en la base de datos, en el map de respuestas insertamos un mensaje de éxito asociado al atributo 'mensaje'
		response.put("mensaje","El cliente ha sido creado con éxito!");
		// En el map de respuestas insertamos el objeto Cliente con los datos del nuevo cliente asociado al atributo 'cliente'
		response.put("cliente",clienteNew);
		// Devolvemos un ResponseEntity<Map<String,Object>> con el mensaje de éxito, el objeto con los datos del nuevo cliente y el estado creado con éxito CREATED(201) 
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	// Método handler de tipo Rest(devuelve el objeto 'ResponseEntity<Map<String,Object>>', y no el nombre de una vista a renderizar) que responde a las peticiones http de tipo Put '/api/clientes/{id}'
	// La anotación @RequestBody es para indicar que el objeto 'cliente' va a llegar al metodo en formato Json y que Spring lo tiene que mapear a una instancia de tipo 'Cliente'
	// Con @PathVariable recuperamos el id de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// Para poder manejar errores,por ejemplo, a la hora de actualizar en la base de datos un cliente y se intenta actualizar el email con uno nulo y no puede serlo,Spring nos proporciona la entidad ResponseEntity que nos permite devolver diferentes estados de ejecución para casos correctos y erróneos
	// La instancia 'cliente' contiene la informacion nueva a modificar en el cliente correspondiente de la base de datos
	// @ Valid es para que se validen,antes de ejecutarse el método, las restricciones de validación(indicada por anotaciones) del objeto 'cliente' que recibimos cuya entidad es 'Cliente'
	// El objeto 'result' de tipo 'BindingResult' contiene la información sobre el resultado de las validaciones
	// Nota: Es importante que el objeto result' de tipo 'BindingResult' esté justo a continuación del objeto que se quiere validar.En este caso,'cliente' de tipo 'Cliente'
	@PutMapping(value = "/clientes/{id}")
	public ResponseEntity<Map<String,Object>> update(@Valid @RequestBody Cliente cliente,BindingResult result,@PathVariable(value = "id") Long id){
		// Realiza una consulta a la base de datos para obtener el cliente a partir del id recuperado de la url mediante nuestro servicio 'clienteService'
		Cliente clienteActual = clienteService.findById(id);
		// Map para almacenar aquella información(mensajes,objetos,etc...) que se quiere enviar al usuario en la respuesta de la petición
		Map<String,Object> response = new HashMap<String,Object>();
		
		// Si hay errores de validación, hacemos lo siguiente:
		if(result.hasErrors()){
			// Usando streams y progrmación funcional de Java 8:
			// De la lista de elementos 'FieldError', que nos devuelve el método 'getFieldErrors()', creamos un stream de elementos 'FieldError' con el método 'stream()' y con el método 'map()' convertimos cada elemento de tipo 'FieldError' en elementos de tipo String con el contenido '"El campo '" + error.getField() + "' " +  error.getDefaultMessage()'.Por último, agrupamos todos los elementos convertidos en String en una lista de tipo String usando el método 'collect()' y el tipo de colección 'Collectors.toList()''
			List<String> errors = result.getFieldErrors().stream().map(error -> "El campo '" + error.getField() + "' " +  error.getDefaultMessage()).collect(Collectors.toList());
			// Insertamos en el map el arraylist de errores en formato string
			response.put("errors",errors);
			// Devolvemos un  ResponseEntity<Map<String,Object>> con el map que contiene el arraylist de mensajes de error y el estado de error BAD_REQUEST(400)
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		// Si no se ha encontrado al cliente en la base de datos,hacemos lo siguiente:
		if(clienteActual == null) {
			// Insertamos en el map un mensaje personalizado de error asociado al atributo 'mensaje'
			response.put("mensaje","Error: no se pudo editar, El cliente ID ".concat(id.toString()).concat(" no existe en la base de datos!")); // La función 'concat' de String hace lo mismo que el operador '+' en strings
			// Devolvemos un  ResponseEntity<Map<String,Object>> con el map que contiene el mensaje de error y el estado de error NOT_FOUND(400)
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		// Cliente a devolver en un ResponseEntity en caso de que se haya actualizado correctamente
		Cliente clienteUpdated = null;
		// try-catch para probar el acceso y conexión a la base de datos.Spring gestiona esto a través de la entidad 'DataAccessException'
		try{
			// Mapeamos la nueva informacion del cliente a la instancia recuperada de la base de datos
			// La fecha, que se encuentra en la propiedad "createAt", no se actualiza porque se trata de una fecha de creación del cliente,y no de modificación
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			// Actualizamos el cliente en la base de datos a través de nuestro servicio 'clienteService'
			clienteUpdated = clienteService.save(clienteActual); // El metodo save(), si no existe el cliente en la base de datos, lo crea, y si existe, hace un merge y lo modifica
		}
		catch(DataAccessException e){
			// Insertamos en el map un mensjae de error asociado al atributo 'mensaje'
			response.put("mensaje","Error al actualizar el cliente en la base de datos");
			// insertamos en el map información relativa a la excepción ocurrida en el acceso a la base de datos asociada al atributo 'error'
			response.put("error",e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
			// Devolvemos un  ResponseEntity<Map<String,Object>> con el map que contiene los mensajes de error y el estado de error INTERNAL_SERVER_ERROR(500)
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
		// Si todo ha ido bien,es decir, si se ha podido actualizar correctamente el cliente en la base de datos, en el map de respuestas insertamos un mensaje de éxito asociado al atributo 'mensaje'
		response.put("mensaje","El cliente ha sido actualizado con éxito!");
		// En el map de respuestas insertamos el objeto Cliente con los datos actualizados del cliente asociado al atributo 'cliente'
		response.put("cliente",clienteUpdated);
		// Devolvemos un ResponseEntity<Map<String,Object>> con el mensaje de éxito, el objeto con los datos actualizados del cliente y el estado creado con éxito CREATED(201) 
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	// Método handler de tipo Rest(devuelve el objeto 'ResponseEntity<Map<String,Object>>', y no el nombre de una vista a renderizar) que responde a las peticiones http de tipo Delete '/api/clientes/{id}'
	// Con @PathVariable recuperamos el id de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// Para poder manejar errores,por ejemplo, a la hora de actualizar en la base de datos un cliente y se intenta actualizar el email con uno nulo y no puede serlo,Spring nos proporciona la entidad ResponseEntity que nos permite devolver diferentes estados de ejecución para casos correctos y erróneos
	@DeleteMapping(value = "/clientes/{id}")
	public ResponseEntity<Map<String,Object>> delete(@PathVariable(value = "id") Long id){
		// Map para almacenar aquella información(mensajes,objetos,etc...) que se quiere enviar al usuario en la respuesta de la petición
		Map<String,Object> response = new HashMap<String,Object>();
		// try-catch para probar el acceso y conexión a la base de datos.Spring gestiona esto a través de la entidad 'DataAccessException'
		try{
			// Elimina el cliente de la base de datos a partir del id recuperado de la url mediante nuestro servicio 'clienteService'
			clienteService.delete(id); // No hace falta comprobar antes si existe el cliente en la base de datos,ya que lo hace por debajo Spring al usar el método 'delete' de la interfaz CrudRpository 
		}
		catch(DataAccessException e){
			// Insertamos en el map un mensjae de error asociado al atributo 'mensaje'
			response.put("mensaje","Error al eliminar el cliente en la base de datos");
			// Insertamos en el map información relativa a la excepción ocurrida en el acceso a la base de datos asociada al atributo 'error'
			response.put("error",e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
			// Devolvemos un  ResponseEntity<Map<String,Object>> con el map que contiene los mensajes de error y el estado de error INTERNAL_SERVER_ERROR(500)
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// Si todo ha ido bien,es decir, si se ha podido eliminar correctamente el cliente en la base de datos, en el map de respuestas insertamos un mensaje de éxito asociado al atributo 'mensaje'
		response.put("mensaje","El cliente ha sido eliminado con éxito!");
		// Devolvemos un ResponseEntity<Map<String,Object>> con el mensaje de éxito y el estado de éxito OK(200)
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}

}
