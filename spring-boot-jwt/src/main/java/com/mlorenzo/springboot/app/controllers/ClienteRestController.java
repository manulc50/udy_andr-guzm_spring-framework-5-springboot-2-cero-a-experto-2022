package com.mlorenzo.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mlorenzo.springboot.app.models.entity.Cliente;
import com.mlorenzo.springboot.app.models.service.IClienteService;
import com.mlorenzo.springboot.app.models.service.IUploadFileService;

@RestController // Esta anotación indica que esta clase se trata de un Controlador Rest de Spring  para que lo almacene en su contenedor como un bean y lo gestione.Esta anotación incluye la anotación @Controller y además pone la anotación @ResponseBody en cualquier método definido en esta clase
@RequestMapping("/api") // Todas las peticiones a este controlador tienen que ser a partir de la ruta base '/api'
public class ClienteRestController {
	
	// Recuperamos del contenedor o memoria de Spring el bean que implmenta nuestra interfaz 'IClienteService'.Esta interfaz es implementada por nuestra clase 'ClienteServiceImpl'
	@Autowired
	private IClienteService clienteService; // Este bean se corresponde con nuestro servicio que implementa y gestiona todo lo relacionado con nuestra entidad Cliente
	
	// Recuperamos del contenedor de Spring el bean correspondiente que contiene la implementación de esta interfaz.En este caso,el bean que la implementa es de tipo UploadFileServiceImpl
	@Autowired
	private IUploadFileService uploadService; // Este bean se corresponde con la capa de servico para la subida y descarga de archivos de tipo imagen
	
	// Metodo handler de tipo Rest(devuelve el objeto 'List<Cliente>', y no el nombre de una vista a renderizar) que responde a las peticiones http de tipo Get '/api/clientes'
	// No hace falta poner @ResponseBody a nivel de metodo ya que se hereda de la anotacion a nivel de clase @RestController
	// Si no se especifica con la anotación @ResponseStatus ningun estado de error con "HttpStatus",por defecto es 'Httpstatus.OK' -> codigo 200 
	@GetMapping(value = "/clientes")
	public List<Cliente> listar(){
		// Realiza una consulta a la base de datos para obtener la información de todos los clientes mediante nuestro servicio 'clienteService'
		return clienteService.findAll();
	}
	
	// Método identico al anterior pero con paginación
	// Método handler de tipo Rest(devuelve el objeto 'Page<Cliente>', y no el nombre de una vista a renderizar) que responde a las peticiones http de tipo Get '/api/clientes/page/{page}'
	// No hace falta poner @ResponseBody a nivel de metodo ya que se hereda de la anotacion a nivel de clase @RestController
	// Con @PathVariable recuperamos el número de página("page") de la url. Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// Si no se especifica con la anotación @ResponseStatus ningun estado de error con "HttpStatus",por defecto es 'Httpstatus.OK' -> codigo 200 
	// Nota: La paginación empieza en el indice 0
	@GetMapping(value = "/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable(value = "page") Integer page){
		// Realiza una consulta a la base de datos para obtener la información de los clientes usando pagínación(el valor de "page" determina el número de página) mediante nuestro servicio 'clienteService'
		// De manera estática,accemos al método 'of' de la clase 'PageRequest' para que nos dé los 4 registros correspondiente a la página 'page' y los devolvemos en un objeto 'Page<Cliente>'
		return clienteService.findAll(PageRequest.of(page,4));
	}
	
	// Metodo handler de tipo Rest(devuelve el objeto ' ResponseEntity<?>', y no el nombre de una vista a renderizar) que responde a las peticiones http de tipo Get '/api/cliente/{id}'
	// Para poder manejar errores,por ejemplo, cuando consultamos por un id que no existe en la base de datos,Spring nos proporciona la entidad ResponseEntity que nos permite devolver diferentes estados de ejecución para casos correctos y erróneos
	// Devolvemos un ResponseEntity<?>(? -> De cualquier tipo) ya que, si el resultado es correcto,vamos a devolver un ResponseEntity<Cliente>,pero si el resultado no es correcto,vamos a devolver un ResponseEntity<Map<String,Object>> con el mensaje de error
	// Con la anotación @Secured damos acceso a la ruta '/api/clientes/{id}' para peticiones http get a los usuarios autenticados con roles "ROLE_ADMIN" y "ROLE_USER".Con esta anotación,a diferencia de los método 'hasAnyRole()' y 'hasRole()', sí hay que especificar el prefijo 'ROLE_'
	// Con @PathVariable recuperamos el id de la url. Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	@Secured({"ROLE_ADMIN","ROLE_USER"})
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
		
		// Si no se ha encontrado al cliente en la base de datos, hacemos lo siguiente:
		if(cliente == null){
			// Insertamos en el map un mensjae de error asociado al atributo 'mensaje'
			response.put("mensaje","El cliente ID ".concat(id.toString()).concat(" no existe en la base de datos!")); // La función 'concat' de String hace lo mismo que el operador '+' en strings
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
	// Con la anotación @Secured damos acceso a la ruta '/api/clientes' para peticiones http post a los usuarios autenticados con role "ROLE_ADMIN".Con esta anotación,a diferencia de los método 'hasAnyRole()' y 'hasRole()', sí hay que especificar el prefijo 'ROLE_'
	@Secured("ROLE_ADMIN")	
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
	// Con la anotación @Secured damos acceso a la ruta '/api/clientes/{id}' para peticiones http put a los usuarios autenticados con role "ROLE_ADMIN".Con esta anotación,a diferencia de los método 'hasAnyRole()' y 'hasRole()', sí hay que especificar el prefijo 'ROLE_'
	@Secured("ROLE_ADMIN")
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
		
		// Si no se ha encontrado al cliente en la base de datos, hacemos lo siguiente:
		if(clienteActual == null){
			// Insertamos en el map un mensjae de error asociado al atributo 'mensaje'
			response.put("mensaje","Error: no se pudo editar, El cliente ID ".concat(id.toString()).concat(" no existe en la base de datos!")); // La función 'concat' de String hace lo mismo que el operador '+' en strings
			// Devolvemos un  ResponseEntity<Map<String,Object>> con el map que contiene el mensaje de error y el estado de error NOT_FOUND(400)
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		// Cliente a devolver en un ResponseEntity en caso de que se haya actualizado correctamente
		Cliente clienteUpdated = null;
		// try-catch para probar el acceso y conexión a la base de datos.Spring gestiona esto a través de la entidad 'DataAccessException'
		try{
			// Mapeamos la nueva informacion del cliente a la instancia recuperada de la base de datos
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			// Como la propiedad "createAt" tiene la validación @NotNull,es decir, no puede pasarse un objeto Cliente con la propiedad "createAt" vacía, se permite que esta propiedad, correspondiente a la fecha del cliente, se pueda actualizar con el nuevo contenido
			clienteActual.setCreateAt(cliente.getCreateAt());
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
	// Con la anotación @Secured damos acceso a la ruta '/api/clientes/{id}' para peticiones http delete a los usuarios autenticados con role "ROLE_ADMIN".Con esta anotación,a diferencia de los método 'hasAnyRole()' y 'hasRole()', sí hay que especificar el prefijo 'ROLE_'
	@Secured("ROLE_ADMIN")
	@DeleteMapping(value = "/clientes/{id}")
	public ResponseEntity<Map<String,Object>> delete(@PathVariable(value = "id") Long id){
		// Map para almacenar aquella información(mensajes,objetos,etc...) que se quiere enviar al usuario en la respuesta de la petición
		Map<String,Object> response = new HashMap<String,Object>();
		// try-catch para probar el acceso y conexión a la base de datos.Spring gestiona esto a través de la entidad 'DataAccessException'
		try{
			// Obtenemos el cliente de la base de datos a partir del id recuperado de la url mediante nuestro servicio 'clienteService' para poder eliminar su foto asociada de la carpeta 'uploads'
			Cliente cliente = clienteService.findById(id);
			// Verificamos que el cliente ha sido localizado en la base de datos para poder obtener el nombre de la imagen asociada,si tiene, y eliminarla
			if(cliente != null) {
				// Obtenemos el nombre de la foto anteior del cliente
				String nombreArchivo = cliente.getFoto();
				// Invocamos al método 'eliminar(nombreArchivo)' de nuestro servicio 'uploadService' para eliminar del servidor(en realidad se trata de una carpeta creada en la raíz del proyecto llamada 'uploads') la foto asociada al cliente cuyo nombre 'nombreArchivo' se le pasa como parámetro al método
				uploadService.eliminar(nombreArchivo);
			}
			// Elimina el cliente de la base de datos a partir del id recuperado de la url mediante nuestro servicio 'clienteService'
			clienteService.delete(id); // En este punto no hace falta comprobar si existe el cliente en la base de datos para su eliminación porque ya lo hace por debajo Spring al usar el método 'delete' de la interfaz CrudRpository 
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
	
	// Método handler de tipo Rest(devuelve el objeto 'ResponseEntity<Map<String,Object>>', y no el nombre de una vista a renderizar) que responde a las peticiones http de tipo Post '/api/clientes/upload'
	// Con @RequestParam recuperamos los valores de los parámetros de la url.No confundir con la anotación @PathVariable que se utiliza para recuperar el valor de una variable de la url.Cuando la url es de la forma '/clientes/{id}',id es una variable que se puede obtener con @PathVariable,pero si la url es la forma '/clientes/upload?archivo="xxx"?id=5',los parámetros 'archivo' e 'id' se pueden obtener con @RequestParam
	// Para poder manejar errores,por ejemplo, a la hora de actualizar en la base de datos un cliente y se intenta actualizar el email con uno nulo y no puede serlo,Spring nos proporciona la entidad ResponseEntity que nos permite devolver diferentes estados de ejecución para casos correctos y erróneos
	// Con la anotación @Secured damos acceso a la ruta '/api/clientes/upload' para peticiones http post a los usuarios autenticados con roles "ROLE_ADMIN" y "ROLE_USER".Con esta anotación,a diferencia de los método 'hasAnyRole()' y 'hasRole()', sí hay que especificar el prefijo 'ROLE_'
	@Secured({"ROLE_ADMIN","ROLE_USER"})	
	@PostMapping("/clientes/upload")
	public ResponseEntity<Map<String,Object>> upload(@RequestParam(name="archivo") MultipartFile archivo,@RequestParam(name="id") Long id){
		// Map para almacenar aquella información(mensajes,objetos,etc...) que se quiere enviar al usuario en la respuesta de la petición
		Map<String,Object> response = new HashMap<String,Object>();
		// Obtenemos el cliente de la base de datos a partir del id recuperado de la url mediante nuestro servicio 'clienteService' para asociale el nombre de la foto
		Cliente cliente = clienteService.findById(id);
		
		// Si el archivo viene informado, hacemos lo siguiente:
		if(!archivo.isEmpty()){ 
			String nombreArchivo = null;
			try {
				// Mediante el método 'copiar(archivo)' de nuestro servicio 'uploadService',se va a subir el archivo 'archivo' pasado como parámetro a dicho método al servidor(en realidad se trata de una carpeta creada en la raíz del proyecto llamada 'uploads')
				// Si algo va mal,este método devuelve una excepción de tipo 'IOException' que la capturamos
				nombreArchivo = uploadService.copiar(archivo);
			} catch (IOException e) {
				// Insertamos en el map un mensjae de error asociado al atributo 'mensaje'
				response.put("mensaje","Error al subir la imagen del cliente ");
				// Insertamos en el map información relativa a la excepción ocurrida en la subida de la imagen asociada al atributo 'error'
				response.put("error",e.getMessage() + ": " + e.getCause().getMessage());
				// Devolvemos un  ResponseEntity<Map<String,Object>> con el map que contiene los mensajes de error y el estado de error INTERNAL_SERVER_ERROR(500)
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			// Para garantizar que en la carpeta donde se almacenan las imágenes sólo haya una por cliente,hacemos lo siguiente:
			// Obtenemos el nombre de la foto anteior del cliente
			String nombreArchivoAnterior = cliente.getFoto();
			// Invocamos al método 'eliminar(nombreArchivoAnterior)' de nuestro servicio 'uploadService' para eliminar del servidor(en realidad se trata de una carpeta creada en la raíz del proyecto llamada 'uploads') la foto anterior del cliente cuyo nombre 'nombreArchivoAnterior' se le pasa como parámetro al método
			uploadService.eliminar(nombreArchivoAnterior);
			
			// Si el copiado a ido bien, asociamos la imagen al cliente pasándole el nombre de la imagen en 'nombreArchivo'
			cliente.setFoto(nombreArchivo);
			
			// Actualizamos los datos del cliente en la base de datos con el nombre de la foto
			clienteService.save(cliente);
			
			// En el map de respuestas insertamos un mensaje de éxito asociado al atributo 'mensaje'
			response.put("mensaje","Has subido correctamente la imagen: " + nombreArchivo);
			// En el map de respuestas insertamos el objeto Cliente con los datos actualizados con el nombre de la foto
			response.put("cliente",cliente);
		}
		// Devolvemos un ResponseEntity<Map<String,Object>> con el mensaje de éxito, el objeto con los datos actualizados del cliente y el estado creado con éxito CREATED(201) 
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
		
	// Método handler de tipo Rest(devuelve el objeto 'ResponseEntity<Resource>', y no el nombre de una vista a renderizar) que responde a las peticiones http de tipo Get '/api/uploads/img/{nombreFoto:.+}'
	// Para poder manejar errores,por ejemplo, a la hora de actualizar en la base de datos un cliente y se intenta actualizar el email con uno nulo y no puede serlo,Spring nos proporciona la entidad ResponseEntity que nos permite devolver diferentes estados de ejecución para casos correctos y erróneos
	// Con @PathVariable recuperamos el nombre de la foto con la extensión de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// La parte variable de la ruta que mapea este método 'nombreFoto:.+' tiene al final la expresión regular '.+' que permite recibir cualuier tipo de extensión de imñagenes(.png,.jpeg,...)
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable(name="nombreFoto") String nombreFoto){
		// Recurso(en realidad es imagen) que vamos a retornar en el objeto ResponseEntity
		Resource recurso = null; 
		
		try {
			// Mediante el método 'cargar(nombreFoto)' de nuestro servicio 'uploadService',se va a recuperar la imagen con nombre 'nombreFoto' del servidor(en realidad se trata de una carpeta creada en la raíz del proyecto llamada 'uploads')
			// Si algo va mal,este método devuelve una excepción de tipo 'MalformedURLException' que la capturamos
			recurso = uploadService.cargar(nombreFoto);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		// Creamos una cabecera mediante una instancia de HttpHeaders para forzar en la respuesta que se descargue la imagen
		HttpHeaders cabecera = new HttpHeaders();
		// Al atributo 'HttpHeaders.CONTENT_DISPOSITION' le asociamos como valor "attachment" seguido del nombre de la foto,que fuerza en la respuesta que se descargue dicha imagen en el elemnto 'img' de HTML
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + recurso.getFilename() + "\"");
		
		// Devolvemos un ResponseEntity<Resource> con el recurso,la cabecera y el estado de éxito OK(200) 
		return new ResponseEntity<Resource>(recurso,cabecera,HttpStatus.OK);
	}

}
