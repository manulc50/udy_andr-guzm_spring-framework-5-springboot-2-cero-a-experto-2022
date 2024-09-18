package com.mlorenzo.springboot.app.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mlorenzo.springboot.app.models.entity.Cliente;
import com.mlorenzo.springboot.app.service.IClienteService;

@Controller // Indicamos que esta clase se trata de una clase Controlador de Spring para que lo almacene en su contenedor como un bean y lo gestione
//Indicamos que el objeto "cliente" va a ser un atributo de sesión para que se mantenga la información contenida en el atributo "cliente" hasta que finalice el proceso de guardar o editar un cliente en la base de datos desde que se manda la solicitud desde el formulario de la vista correspondiente
//De esta manera, evitamos que se pierda la información del cliente cuando se pasa del controlador a la vista y viceversa, y además, evitamos usar la solución alternativo de usar un campo oculto para el id del cliente en el formulario de la vista
@SessionAttributes("cliente")
public class ClienteController {
	
	// Recuperamos del contenedor o memoria de Spring el bean que implmenta nuestra interfaz 'IClienteService'.Esta interfaz es implementada por el bean con nombre "clienteService" que es tipo 'ClienteServiceImpl'
	@Autowired
	@Qualifier("clienteService") // Esta anotación no es necesaria,pero si tuvieramos varias implementaciones de la interfaz 'IClienteService',sí sería necesaría para indicar la implementación que queremos
	private IClienteService clienteService; // Este bean se corresponde con nuestro servicio que implementa y gestiona todo lo relacionado con nuestra entidad Cliente
	

	// Método handler que escucha peticiones http de tipo GET para las rutas '/listar' y "/"(inicio)
	// El argumento de entrada "model" de tipo "Model" nos permite pasar datos desde este controlador a las vistas
	@RequestMapping(value= {"/listar","/"},method=RequestMethod.GET) 
	public String listar(Model model){ 
		// Pasamos a la vista el texto "Listado de clientes" asociado al atributo "titulo"
		model.addAttribute("titulo","Listado de clientes");
		// Pasamos a la vista el atributo "clientes" con la lista de todos los clientes existentes en la base de datos a través de nuestro servicio "clienteService"
		model.addAttribute("clientes",clienteService.findAll());
		// Devuleve la vista "listar"
		return "listar";
	}
	
	// Método handler que sscucha peticiones http de tipo GET para la ruta '/form'
	// Version reducida de usar @RequestMapping con method=RequestMethod.GET
	// El argumento de entrada "model" de tipo "Map<String,Object>" nos permite pasar datos desde este controlador a las vistas. Es lo mismo que usar directamente "Model model"
	@GetMapping(value="/form")
	public String crear(Map<String,Object> model){
		// Creamos una instancia de tipo Cliente para almacenar los datos del cliente que se introduzcan en el formulario de la vista
		Cliente cliente = new Cliente();
		// Pasamos a la vista el atributo "cliente" con la instancia creada anteriormente
		model.put("cliente", cliente);
		// Pasamos a la vista el atributo "titulo" con el texto "Formulario de cliente"
		model.put("titulo","Formulario de cliente");
		// Pasamos a la vista el atributo "textoBoton" con el texto "Crear Cliente"
		model.put("textoBoton", "Crear Cliente");
		// Devuleve la vista "form" con el formulario
		return "form";
	}
	
	// Método handler que escucha peticiones http de tipo GET para la ruta '/form/' + id_cliente
	// Con @PathVariable recuperamos el id de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// Version reducida de usar @RequestMapping con method=RequestMethod.GET
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	// El argumento de entrada "model" de tipo "Map<String,Object>" nos permite pasar datos desde este controlador a las vistas. Es lo mismo que usar directamente "Model model"
	@GetMapping(value="/form/{id}")
	public String editar(@PathVariable(value = "id") Long id,Map<String,Object> model,RedirectAttributes flash){
		Cliente cliente = null;
		
		// Si el id recuperado de la url es válido, hacemos lo siguiente:
		if(id > 0) {
			// Recuperamos de la base de datos los datos del cliente a partir del id recuperado de la url a través de nuestro servicio "clienteService"
			cliente = clienteService.findOne(id);
			// Si el cliente no se ha localizado, hacemos lo siguiente:
			if(cliente == null) {
				// Creamos el atributo "error" de tipo Flash con el mensaje de error "El ID del cliente no existe en la BBDD!"
				// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar' y queremos mantener este atributo para esta segunda petición http
				flash.addFlashAttribute("error","El ID del cliente no existe en la BBDD!");
				// Devolvemos una redirección al método handler "listar" del controlador asociado a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mencionado)
				return "redirect:/listar"; 
			}
		}
		// En caso contrario, es decir, si el id recuperado de la url no es válido, hacemos lo siguiente:
		else {
			// Creamos el atributo "error" de tipo Flash con el mensaje de error "El ID del cliente no existe en la BBDD!"
			// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar' y queremos mantener este atributo para esta segunda petición http
			flash.addFlashAttribute("error","El ID del cliente no puede ser cero!");
			// Devolvemos una redirección al método handler "listar" del controlador asociado a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mencionado)
			return "redirect:/listar";
		}

		// Si el id es válido y se ha podido localizar el cliente en la base de datos, le pasamos a la vista el atributo "cliente" con el cliente obtenido
		model.put("cliente",cliente);
		// Le pasamos a la vista el atributo "titulo" con el texto "Editar cliente"
		model.put("titulo","Editar cliente");
		// Le pasamos a la vista el atributo "textoBoton" con el texto "Editar cliente"
		model.put("textoBoton","Editar Cliente");
		// Devuleve la vista "form" con el formulario
		return "form";
	}
	
	// Método handler que escucha peticiones http de tipo POST para la ruta '/form'
	// Version reducida de usar @RequestMapping con method=RequestMethod.POST
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	// El argumento de entrada "model" de tipo "Model" nos permite pasar datos desde este controlador a las vistas
	// El parámetro de entrada "status" de tipo "SessionStatus" nos permite liberar un atributo de sesión
	// @ Valid es para que se validen,antes de ejecutarse el método, las restricciones de validación(indicada por anotaciones) del objeto 'cliente' que recibimos cuya entidad es 'Cliente'
	// El objeto 'result' de tipo 'BindingResult' contiene la información sobre el resultado de las validaciones
	// Nota: Es importante que el objeto result' de tipo 'BindingResult' esté justo a continuación del objeto que se quiere validar.En este caso,'cliente' de tipo 'Cliente'
	@PostMapping(value="/form")
	public String guardar(@Valid Cliente cliente, BindingResult result,Model model,SessionStatus status,RedirectAttributes flash){
		// Si hay errores en las validaciones,hacemos lo siguiente:
		if(result.hasErrors()){
			// Pasamos a la vista el atributo "titulo" con el texto "Formulario de cliente"
			model.addAttribute("titulo","Formulario de cliente");
			// Si el cliente recibido como parámetro de entrada tiene el id definido, significa que se trata de una edición o actualización de dicho cliente
			if(cliente.getId() != null && cliente.getId().longValue() > 0)
				// Pasamos a la vista el atributo "textoBoton" con el texto "Editar Cliente"
				model.addAttribute("textoBoton","Editar Cliente");
			// En caso contrario, es decir, si el cliente que se le pasa como argumento de entrada no tiene el id definido, significa que se trata de una nueva creación
			else
				// Pasamos a la vista el atributo "textoBoton" con el texto "Crear Cliente"
				model.addAttribute("textoBoton","Crear Cliente");
			// Devuleve la vista "form" con el formulario
			return "form";
		}
		
		// Si no hay errores de validación, hacemos lo siguiente:
		// Si el el id del cliente ya existe, creamos el mensaje "Cliente editado con éxito!" porque estamos editando un cliente.En caso contrario, creamos el mensaje "Cliente creado con éxito" porque estamos persistiendo un nuevo cliente
		String mensajeFlash = (cliente.getId() != null) ? "Cliente editado con éxito!" : "Cliente creado con éxito";
		// Creamos el atributo "success" de tipo Flash con el mensaje de éxito creado en el paso anterior
		// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar' y queremos mantener este atributo para esta segunda petición http
		flash.addFlashAttribute("success",mensajeFlash);
		// Persistimos o editamos el cliente en la base de datos a partir de los datos contenidos en el parámetro de entrada "cliente" a través de nuestro servicio "clienteService"
		clienteService.save(cliente);
		// En este momento ya se ha finalizado el proceso de creación o edición de un cliente y, por lo tanto, ya podemos liberar el atributo "cliente" de ser un atributo de sesión
		// Para ello, hacemos uso del método "setComplete()" del parámetro de entrada "status"
		status.setComplete();
		// Devolvemos una redirección al método handler "listar" del controlador asociado a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mencionado)
		return "redirect:/listar";
	}
	
	// Método handler que escucha peticiones http de tipo GET para la ruta '/eliminar/' + id_cliente
	// Version reducida de usar @RequestMapping con method=RequestMethod.GET
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	// Con @PathVariable recuperamos el id de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	@GetMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id,RedirectAttributes flash){
		// Si el id recuperado de la url es válido, hacemos lo siguiente:
		if(id > 0) {
			// Elimina el cliente a partir del id recuperado de la url a través de nuestro servicio "clienteService"
			clienteService.delete(id);
			// Creamos el atributo "success" de tipo Flash con el mensaje de éxito "Cliente eliminado con éxito!".Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar' y queremos mantener este atributo para esta segunda petición http
			flash.addFlashAttribute("success","Cliente eliminado con éxito!");
		}
		// Devolvemos una redirección al método handler "listar" del controlador asociado a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mencionado)
		return "redirect:/listar";
	}

}
