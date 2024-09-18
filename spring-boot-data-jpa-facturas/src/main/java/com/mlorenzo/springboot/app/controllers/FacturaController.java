package com.mlorenzo.springboot.app.controllers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mlorenzo.springboot.app.models.entity.Cliente;
import com.mlorenzo.springboot.app.models.entity.Factura;
import com.mlorenzo.springboot.app.models.entity.ItemFactura;
import com.mlorenzo.springboot.app.models.entity.Producto;
import com.mlorenzo.springboot.app.models.service.IClienteService;

@Secured("ROLE_ADMIN") // Todos los controladores de esta clase son accedidos por usuarios con role "ROLE_ADMIN".Por eso lo ponemos a nivel de clase en vez de poner dicha anotación a cada método.
@Controller // Indicamos que esta clase se trata de una clase Controlador de Spring para que lo almacene en su contenedor como un bean y lo gestione
@RequestMapping("/factura") // Este controlador va a tener la url base '/factura'
// Indicamos que el objeto "factura" va a ser un atributo de sesión para que se mantenga la información contenida en el atributo "factura" hasta que finalice el proceso de guardar una factura en la base de datos desde que se manda la solicitud desde el formulario de la vista correspondiente
// De esta manera, evitamos que se pierda la información de la factura cuando se pasa del controlador a la vista y viceversa, y además, evitamos usar la solución alternativo de usar un campo oculto para el id de la facturae en el formulario de la vista
@SessionAttributes("factura")
public class FacturaController {
	
	// Recuperamos del contenedor o memoria de Spring el bean que implmenta nuestra interfaz 'IClienteService'.Esta interfaz es implementada por nuestra clase 'ClienteServiceImpl'
	@Autowired
	private IClienteService clienteService; // Este bean se corresponde con nuestro servicio que implementa y gestiona todo lo relacionado con nuestra entidad Cliente
	
	// Recuperamos del contenedor o memoria de Spring el bean que implmenta la interfaz 'MessageSource'.La implementación de esta interfaz la gestiona Spring al ser una interfaz propia de Spring
	@Autowired
	private MessageSource messageSource; // Este bean es necesario para poder traducir los textos de esta clase a partir de los archivos de idiomas 'message_'
	
	// Habilitamos el uso de Log para esta clase
	private final Logger log = Logger.getLogger(getClass());
	
	// Método handler que escucha peticiones http de tipo Get para la ruta '/factura/ver/' +  id 
	// Con @PathVariable recuperamos el id de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// El argumento de entrada "model" de tipo "Map<String,Object>" nos permite pasar datos desde este controlador a las vistas
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	@GetMapping("/ver/{facturaId}")
	public String ver(@PathVariable(value="facturaId") Long id,Model model,RedirectAttributes flash, Locale locale){
		
		// Recuperamos de la base de datos los datos de la factura a partir del id recuperado de la url a través de nuestro servicio "clienteService"
		// Usamos el metodo 'fetchFacturaByIdWithClienteWhithItemFacturaWithProducto', en lugar del método 'findFacturaById', para obtener la factura de la base de datos de una manera más óptima reduciendo el número de consultas mediante el uso de 'join fetch'
		Factura factura = clienteService.fetchFacturaByIdWithClienteWhithItemFacturaWithProducto(id);
		// Si la factura no se ha localizado, hacemos lo siguiente
		if(factura == null){
			// Creamos el atributo "error" de tipo Flash con un mensaje de error al usuario
			// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar'(controlador "ClienteController") y queremos mantener este atributo para esta segunda petición http
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.factura.flash.db.error' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			flash.addFlashAttribute("error",flash.addFlashAttribute("error", messageSource.getMessage("text.factura.flash.db.error", null, locale)));
			// Devolvemos una redirección al método handler "listar" del controlador asociado("ClienteController") a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mendionado)
			return "redirect:/listar";
		}
		
		// Si se ha podido localizar la factura, le pasamos a la vista el atributo "factura" con la factura obtenida
		model.addAttribute("factura",factura);
		// Le pasamos a la vista el atributo "titulo" con el texto del título de la vista
		// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.factura.ver.titulo' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
		model.addAttribute("titulo",String.format(messageSource.getMessage("text.factura.ver.titulo", null, locale), factura.getDescripcion()));
		
		// Devolvemos la vista 'ver' que se encuentra en la carpeta "factura"
		return "factura/ver";
	}
	
	// Método handler que escucha peticiones http de tipo Get para la ruta '/factura/form/' + id_cliente
	// Con @PathVariable recuperamos el id del cliente de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// El argumento de entrada "model" de tipo "Map<String,Object>" nos permite pasar datos desde este controlador a las vistas. Es lo mismo que usar directamente "Model model"
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable(value="clienteId") Long clienteId,Map<String,Object> model,RedirectAttributes flash,Locale locale){
		// Obtenemos el cliente de la base de datos a partir del id recuperado de la url a través de nuestro servicio "clienteService"
		Cliente cliente = clienteService.findOne(clienteId);
		// Si el cliente no se ha localizado, hacemos lo siguiente:
		if(cliente == null){
			// Creamos el atributo "error" de tipo Flash con un mensaje de error
			// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar'(controlador "ClienteController") y queremos mantener este atributo para esta segunda petición http
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.flash.db.error' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			flash.addFlashAttribute("error",messageSource.getMessage("text.cliente.flash.db.error", null, locale));
			// Devolvemos una redirección al método handler "listar" del controlador asociado("ClienteController") a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mencionado)
			return "redirect:/listar";
		}
		
		// Si se ha podido localizar el cliente, hacemos lo siguiente:
		// Creamos una instancia de tipo Factura para almacenar los datos de la factura que se introduzcan en el formulario de la vista
		Factura factura = new Factura();
		// Antes de pasar esta instacia de tipo Factura a la vista, asociamos el cliente obtenido anteriormente a dicha instancia
		factura.setCliente(cliente);
		// Pasamos a la vista el atributo "factura" con la instancia creada anteriormente
		model.put("factura",factura);
		// Pasamos a la vista el atributo "titulo" con el texto del título de la vista
		// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.factura.form.titulo' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
		model.put("titulo",messageSource.getMessage("text.factura.form.titulo", null, locale));
		
		// Devuleve la vista "form" con el formulario que se encuentra en la carpeta "factura"
		return "factura/form";
	}
	
	// Método handler que escucha peticiones http de tipo Get para la ruta '/factura/cargar-productos/' + term
	// @ResponseBody es una anotacion que nos permite devolver los datos de un objeto en formato json,xml o texto plano.Si no se especifica un formato en concreto por defecto es json.Por ejemplo,si ponemos 'produces = MediaType.APPLICATION_XML_VALUE' como un atributo en la anotación @RequestMapping,la respuesta nos la da en formato xml
	// Con @PathVariable recuperamos el término de búsqueda de productos de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	@GetMapping(value="/cargar_productos/{term}",produces={"application/json"})
	public @ResponseBody List<Producto> cargarProductos(@PathVariable(value="term") String termino){
		// Obtenemos los productos de la base de datos a partir del término de búsqueda recuperado de la url a través de nuestro servicio "clienteService"
		return clienteService.findByNombre(termino);
	}
	
	// Método handler que escucha peticiones http de tipo Post para la ruta '/form' + clienteId
	// Con @RequestParam recuperamos el parámetro del formulario 'item_id[]', que se corresponde con los identificadores de los productos asociados a la factura. Es opcional(required=false) porque es posible que se envíe el formulario sin que haya productos asociados a la factura.Los parámetros viajan en la url de la siguiente manera;'/form?param=valor'
	// Con @RequestParam recuperamos el parámetro del formulario 'cantidad[], que se corresponde con las cantidades de los productos asociados a la factura. Es opcional(required=false) porque es posible que se envíe el formulario sin que haya productos asociados a la factura.Los parámetros viajan en la url de la siguiente manera;'/form?param=valor'
	// El argumento de entrada "model" de tipo "Model" nos permite pasar datos desde este controlador a las vistas
	// El parámetro de entrada "status" de tipo "SessionStatus" nos permite liberar un atributo de sesión
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	// @ Valid es para que se validen,antes de ejecutarse el método, las restricciones de validación(indicada por anotaciones) del objeto 'factura' que recibimos cuya entidad es 'Factura'
	// El objeto 'result' de tipo 'BindingResult' contiene la información sobre el resultado de las validaciones
	// Nota: Es importante que el objeto result' de tipo 'BindingResult' esté justo a continuación del objeto que se quiere validar.En este caso,'factura' de tipo 'Factura'
	// No se hace nada con la variable "clienteId" que se le pasa a la url.Solo se la pasamos para que se mantenga la url "/factura/form/" + cliente_id si se desea cambiar de idioma durante el relleno del formulario de facturas y así evitar un error.
	// Este error se produce si no le pasamos el cliente_id a la url y se decide cambiar de idioma durante el relleno del formulario de facturas, porque dicho cambio de idioma conlleva una redirección, mediante una petición htto Get, a la url '/factura/form' que no es controlada por ningún método handler de este controlador.
	// Pero si se hace la redirección a la url /factura/form' + cliente_id mediante una petición http Get, sí hay un método handler(método "crear") de este controlador que la trata
	@PostMapping(value="/form/{clienteId}")
	public String guardar(@Valid Factura factura,BindingResult result,Model model,@RequestParam(name="item_id[]",required=false) Long[] productoIds,@RequestParam(name="cantidad[]",required=false) Integer[] cantidades,RedirectAttributes flash,SessionStatus status, Locale locale){
		
		// Si hay errores en las validaciones, hacemos lo siguiente:
		if(result.hasErrors()){
			// Le pasamos a la vista el atributo "titulo" con el texto del título de la vista
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.factura.form.titulo' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			model.addAttribute("titulo",messageSource.getMessage("text.factura.form.titulo", null, locale));
			// Devuleve la vista "form" con el formulario que se encuentra en la carpeta "factura"
			return "factura/form";
		}
		
		// Si no hay errores de validación, hacemos lo siguiente:
		// Comprobamos si no hay identificadores de productos asociados a la factura y hacemos lo siguiente:
		if((productoIds == null) || (productoIds.length == 0)){
			// Le pasamos a la vista el atributo "titulo" con el texto del título de la vista
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.factura.form.titulo' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			model.addAttribute("titulo",messageSource.getMessage("text.factura.form.titulo", null, locale));
			// Le pasamos a la vista el atributo "error" con un mensaje de error
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.factura.flash.lineas.error' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			model.addAttribute("error",messageSource.getMessage("text.factura.flash.lineas.error", null, locale));
			// Devuleve la vista "form" con el formulario que se encuentra en la carpeta "factura"
			return "factura/form";
		}
		
		// Si hay identificadores de productos asociados a la factura, hacemos lo siguiente:
		// Iteramos los identificadores de los productos y, para cada uno de ellos, hacemos lo siguiente:
		for(int i=0;i<productoIds.length;i++){
			// Obtenemos el producto de la base de datos a partir de cada identificador a través de nuestro servicio 'clienteService'
			Producto producto = clienteService.findProductoById(productoIds[i]);
			// Creamos una instancia de ItemFactua que se corresponde con una línea de producto
			ItemFactura itemFactura = new ItemFactura();
			// Asociamos el producto obtenido anteriormente a la línea de producto
			itemFactura.setProducto(producto);
			// Asociamos la cantidad del producto correspondiente a la línea de producto
			itemFactura.setCantidad(cantidades[i]);
			// Asociamos la línea de producto a la factura que recibimos como parámetro del método
			factura.addItemFactura(itemFactura);
			// Escribimos en el log a modo de información el identificador del producto y su cantidad
			log.info("ID: " + productoIds[i].toString() + " CANTIDAD: " + cantidades[i].toString());
		}
		
		// Persistimos la factura en la base de datos a partir de los datos contenidos en el parámetro de entrada "factura" a través de nuestro servicio "clienteService"
		clienteService.saveFactura(factura);
		// En este momento ya se ha finalizado el proceso de creación de una factura y, por lo tanto, ya podemos liberar el atributo "factura" de ser un atributo de sesión
		// Para ello, hacemos uso del método "setComplete()" del parámetro de entrada "status"
		status.setComplete();	
		// Creamos el atributo "success" de tipo Flash con un mensaje de éxito
		// Es de tipo Flash ya que a continuación devolvemos un redirect a '/ver/' + clienteId.Entonces,no vamos directamente a la vista '/ver',sino que volvemos a lanzar una segunda petición http al método handler "ver" asociado a la ruta '/ver/' + clienteId(controlador "ClienteController") y queremos mantener este atributo para esta segunda petición http
		// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.factura.flash.crear.success' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
		flash.addFlashAttribute("success", messageSource.getMessage("text.factura.flash.crear.success", null, locale));
		
		// Devolvemos una redirección al método handler "ver" del controlador asociado("ClienteController") a la ruta '/ver/' + clienteId(no se va directamente a la vista "ver", primero pasa por el controlador mencionado)
		return "redirect:/ver/" + factura.getCliente().getId();
	}
	
	// Método handler que escucha peticiones http de tipo Get a la ruta '/eliminar/' + id_factura
	// Con @PathVariable recuperamos el id de la factura de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	@GetMapping("/eliminar/{idFactura}")
	public String eliminar(@PathVariable(value="idFactura") Long id,RedirectAttributes flash, Locale locale){
		// Obtenemos la factura de la base de datos a partir del id recuperado de la url a través de nuestro servicio 'clienteService'
		Factura factura = clienteService.findFacturaById(id);
		// Si se ha lozalizado la factura, hacemos lo siguiente:
		if(factura != null){ 
			// Eliminamos la factura de la base de datos a partir del id recuperado de la url a través de nuestro servicio "clienteService"
			clienteService.deleteFactura(id);
			// Creamos el atributo "success" de tipo Flash con un mensaje de éxito
			// Es de tipo Flash ya que a continuación devolvemos un redirect a '/ver/' + clienteId.Entonces,no vamos directamente a la vista '/ver',sino que volvemos a lanzar una segunda petición http al método handler "ver" asociado a la ruta '/ver/' + clienteId(controlador "ClienteController") y queremos mantener este atributo para esta segunda petición http
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.factura.flash.eliminar.success' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			flash.addFlashAttribute("success",messageSource.getMessage("text.factura.flash.eliminar.success", null, locale));
			// Devolvemos una redirección al método handler "ver" del controlador asociado("ClienteController") a la ruta '/ver/' + clienteId(no se va directamente a la vista "ver", primero pasa por el controlador mencionado)
			return "redirect:/ver/" + factura.getCliente().getId();
		}
		
		// Si no se ha lozalizado la factura, hacemos lo siguiente:
		// Creamos el atributo "error" de tipo Flash con un mensaje de error
		// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar'(controlador "ClienteController") y queremos mantener este atributo para esta segunda petición http
		// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.factura.flash.db.error' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
		flash.addFlashAttribute("error",messageSource.getMessage("text.factura.flash.db.error", null, locale));
		// Devolvemos una redirección al método handler "listar" del controlador asociado("ClienteController") a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mencionado)
		return "redirect:/listar";
	}
	

}
