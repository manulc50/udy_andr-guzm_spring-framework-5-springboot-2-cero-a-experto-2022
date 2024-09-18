package com.mlorenzo.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mlorenzo.springboot.app.models.entity.Cliente;
import com.mlorenzo.springboot.app.models.service.IClienteService;
import com.mlorenzo.springboot.app.models.service.IUploadFileService;
import com.mlorenzo.springboot.app.util.paginator.PageRender;
import com.mlorenzo.springboot.app.view.xml.ClienteList;

// NOTA: Las anotaciones usadas en esta clase @Secured("ROLE_USER") y @PreAuthorize("hasRole('ROLE_USER')") hacen extactamente lo mismo

@Controller // Indicamos que esta clase se trata de una clase Controlador de Spring para que lo almacene en su contenedor como un bean y lo gestione
//Indicamos que el objeto "cliente" va a ser un atributo de sesión para que se mantenga la información contenida en el atributo "cliente" hasta que finalice el proceso de guardar o editar un cliente en la base de datos desde que se manda la solicitud desde el formulario de la vista correspondiente
//De esta manera, evitamos que se pierda la información del cliente cuando se pasa del controlador a la vista y viceversa, y además, evitamos usar la solución alternativo de usar un campo oculto para el id del cliente en el formulario de la vista
@SessionAttributes("cliente")
public class ClienteController {
	
	// Habilitamos el uso del log para esta clase
	private final Log logger = LogFactory.getLog(this.getClass());

	// Recuperamos del contenedor o memoria de Spring el bean que implmenta nuestra interfaz 'IClienteService'.Esta interfaz es implementada por nuestra clase 'ClienteServiceImpl'
	@Autowired
	private IClienteService clienteService; // Este bean se corresponde con nuestro servicio que implementa y gestiona todo lo relacionado con nuestra entidad Cliente

	// Recuperamos del contenedor o memoria de Spring el bean que implmenta nuestra interfaz 'IUploadFileService'.Esta interfaz es implementada por nuestra clase 'UploadFileServiceImpl'
	@Autowired
	private IUploadFileService uploadFileService; // Este bean se corresponde con nuestro servicio que implementa y gestiona todo lo relacionado con la subida de imágenes para el cliente
	
	// Recuperamos del contenedor o memoria de Spring el bean que implmenta la interfaz 'MessageSource'.La implementación de esta interfaz la gestiona Spring al ser una interfaz propia de Spring
	@Autowired
	private MessageSource messageSource; // Este bean es necesario para poder traducir los textos de esta clase a partir de los archivos de idiomas 'message_'
	
	// Método handler que escucha peticiones http de tipo GET para la ruta '/uploads/' + nombre_archivo(con extensión)
	// En lugar de registrar un manejador de recursos(método 'addResourceHandlers' comentado en la clase de configuración 'MvcConfig') para hacer accesible públicamente la carpeta de subida de imágenes y su contenido,otra alternativa es usar este método handler que recibe el nombre de la imagen a localizar en dicha carpeta y la devuelve como un recurso accesible
	// Con @PathVariable recuperamos de la url la variable 'filename'(con su extensión).Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// '{filename:.+}' es una expresión regular que permite cualquier nombre de archivo junto con cualquier extensión separada por un punto
	// Este método handler no deuelve el nombre de una vista a renderizar,sino que devuelve un objeto de tipo ResponseEntity con un recurso que es la imagen obtenida de nuestro servidor(carpeta indicada por la constante 'UPLOADS_FOLDER')
	// Nota: La anotación @ResponseBody en la respuesta de un método handler se pone cuando queremos devolver un json,xml o texto plano.En este caso,aquí no queremos devolver la respuesta en ninguno de esos formatos,así que no hace falta poner dicha anotación
	// El acceso al controlador de la peticion http de tipo Get para la ruta '/uploads/{filename:.+}' solo lo tienen los usuarios con role 'ROLE_USER'
	@Secured("ROLE_USER")
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
		Resource recurso = null;

		try {
			// Mediante nuestro servicio 'uploadFileService',intenta obtener del servidor(carpeta indicada por la constante 'UPLOADS_FOLDER') la imagen con nombre 'filename' como un recurso
			// Si algo va mal,este método devuelve una excepción de tipo 'MalformedURLException' que la capturamos
			recurso = uploadFileService.cargar(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Si todo ha ido bien,devolvemos un objeto ResponseEntity con estado de la peición OK(200),con el nombre de la imagen en la cabecera de la respuesta y con el recurso en el cuerpo de la respuesta
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

	// Método handler que escucha peticiones http de tipo GET para la ruta '/ver/' + id_cliente
	// Con @PathVariable recuperamos el id de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// El argumento de entrada "model" de tipo "Map<String,Object>" nos permite pasar datos desde este controlador a las vistas. Es lo mismo que usar directamente "Model model"
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	// El acceso al controlador de la peticion http de tipo Get para la ruta '/ver/{id}' solo lo tienen los usuarios con role 'ROLE_USER'
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash, Locale locale) {

		// Recuperamos de la base de datos los datos del cliente a partir del id recuperado de la url a través de nuestro servicio "clienteService"
		// Usamos el metodo 'fetchByIdWithFacturas', en lugar del método 'findOne', para obtener el cliente de la base de datos de una manera más óptima reduciendo el número de consultas mediante el uso de 'join fetch'
		Cliente cliente = clienteService.fetchByIdWithFacturas(id);
		// Si el cliente no se ha localizado, hacemos lo siguiente
		if (cliente == null) {
			// Creamos el atributo "error" de tipo Flash con un mensaje de error al usuario
			// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar' y queremos mantener este atributo para esta segunda petición http
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.flash.db.error' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
			// Devolvemos una redirección al método handler "listar" del controlador asociado a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mendionado)
			return "redirect:/listar";
		}

		// Si se ha podido localizar el cliente, le pasamos a la vista el atributo "cliente" con el cliente obtenido
		model.put("cliente", cliente);
		// Le pasamos a la vista el atributo "titulo" con el texto del título de la vista
		// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.detalle.titulo' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
		model.put("titulo", messageSource.getMessage("text.cliente.detalle.titulo", null, locale) + ": " + cliente.getNombre());
		// Devolvemos la vista 'ver'
		return "ver";
	}
	
	// Metodo handler de tipo Rest(ya que devuelve un objeto de tipo "ClienteList" en lugar del nombre de una vista) del controlador que responde a peticiones http de tipo Get para la ruta '/listar-rest'
	// @ResponseBody es una anotacion que nos permite devolver los datos de un objeto en formato json,xml o texto plano.Si no se especifica un formato en concreto por defecto es json.Por ejemplo,si ponemos 'produces = MediaType.APPLICATION_XML_VALUE' como un atributo en la anotación @RequestMapping,la respuesta nos la da en formato xml
	// Tal y como está ahora,por defecto los datos de los clientes se van a mostrar en formato xml
	// Si se desea mostrar los datos en formato json, manualmente hay que añadir el parámetro "format" con el valor "json" al url, es decir, la url sería '.../listar-rest?format=json'
	// También se muestran los datos en formato xml si manualmente se añade el parámetro "format" con el valor "xml" a la url, es decir, la url sería '.../listar-rest?format=xml'
	// Este método no tiene anotación de seguridad @Secured porque es de acceso público y así se ha configurado en el método 'configure()' de la clase de configuración de seguridad para Spring "SpringSecurityConfig"
	@RequestMapping(value = "/listar-rest", method = RequestMethod.GET)
	public @ResponseBody ClienteList listarRest(){
		// Obtiene y devuelve los datos de todos los clientes recuperados de la base de datos a través de nuestro servicio 'clienteService'
		// ClienteList es la clase envoltorio o wrapper para que funcione la renderizacion, en una vista xml, de la informacion de los clientes cuando estan almacenados en listas o colecciones.Si se quiere que este metodo sea valido tanto para json como para xml,hay que usar esta clase envoltorio.Si solo fuera para json,no haria falta,se devolveria directamente un List<Cliente>
		return new ClienteList(clienteService.findAll());
	}

	// Método handler que escucha peticiones http de tipo GET para las rutas '/listar' y '/'(inicio)
	// Con la anotación @RequestParam obtenemos el parámetro 'page' de la url que contiene el número de página a mostrar.Si dicho parámetro no tiene valor,por defecto se le asigna 0,que se corresponde con el indice de la primera página
	// Con la anotación @RequestParam obtenemos el parámetro 'format' de la url que indica el tipo de formato(HTML,JSON,CSV o XML) de la petición.Si dicho parámetro no tiene valor, por defecto se le asigna "html"
	// El argumento de entrada "model" de tipo "Model" nos permite pasar datos desde este controlador a las vistas
	// El argumento de entrada "authentication" de tipo "Authentication" contiene información del usuario autenticado
	// El argumento de entrada "request" de tipo "HttpServletRequest" contiene datos de la petición http
	// Este método no tiene anotación de seguridad @Secured porque es de acceso público y así se ha configurado en el método 'configure()' de la clase de configuración de seguridad para Spring "SpringSecurityConfig"
	@RequestMapping(value = {"/listar","/"}, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page,@RequestParam(name = "format", defaultValue = "html") String format, Model model,Authentication authentication,HttpServletRequest request,Locale locale) {

		// Dos maneras de obtener el nombre del usuario una vez que se ha autenticado
		// Primera forma
		if(authentication != null)
			logger.info("Hola usuario autenticado, tu username es " + authentication.getName());
		
		// Segunda forma
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null)
			logger.info("Utilizandp forma estática SecurityContextHolder.getContext().getAuthentication(): Hola usuario autenticado, tu username es " + auth.getName());
		
		// Tres maneras de saber si el usuario autenticado tiene acceso con role 'ROLE_ADMIN'
		// Primera forma
		if(hasRole("ROLE_ADMIN"))
			logger.info("Hola " + auth.getName() + " ,tienes acceso");
		else
			logger.info("Hola " + auth.getName() + " ,no tienes acceso");
		
		//Segunda forma
		// La instancia de Spring Security de tipo 'SecurityContextHolderAwareRequestWrapper' para comprobar la autenticacion del usuario actua como un envoltorio de la peticion http 'request' y se le pasa un prefijo de los roles 'ROLE_'(nuestros prefijos son de la forma 'ROLE_ADMIN','ROLE_USER',...)
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request,"ROLE_");
		// Comprueba si el usuario autenticado tiene role 'ROLE_ADMIN'
		// Al metodo 'isUserInRole' solo le pasamos 'ADMIN' ya que 'ROLE_' lo hemos indicado como un prefijo en la instancia de tipo 'SecurityContextHolderAwareRequestWrapper'
		if(securityContext.isUserInRole("ADMIN"))
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper: Hola " + auth.getName() + " ,tienes acceso");
		else
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper: Hola " + auth.getName() + " ,no tienes acceso");
		
		// Tercera forma
		// En este caso,como no usamos la instancia de tipo 'SecurityContextHolderAwareRequestWrapper' de Spring Security y no hemos indicado ningun prefijo para los roles,al metodo 'isUserInRole' hay que pasarle el nombre completo del role
		if(request.isUserInRole("ROLE_ADMIN"))
			logger.info("Forma usando HttpServletRequest: Hola " + auth.getName() + " ,tienes acceso");
		else
			logger.info("Forma usando HttpServletRequest: Hola " + auth.getName() + " ,no tienes acceso");
		
		// Si el formato de la petición es de tipo HTML, significa que los datos de los clientes se van a mostrar en una tabla con paginación en la vista
		if(format.equals("html")) {
			// Obtenemos una instancia de tipo Pageable para establecer la paginación a partir del valor del número de página, que nos llega desde el parámetro "page" de la url, y a partir del número total de páginas establecido en 4
			Pageable pageRequest = PageRequest.of(page, 4);
	
			// Recuperamos de la base de datos los clientes con paginación a través de nuestro servicio 'clienteService' a partir de los datos de paginación contenidos en la instancia pageRequest de tipo Pageable
			Page<Cliente> clientes = clienteService.findAll(pageRequest);
	
			// Creamos una instancia de nuestro paginador implementado en nuestra clase 'PageRender' para nuestra entidad 'Cliente'.Para ello,le pasamos la url de la vista donde vamos a mostrar el paginador y la instancia 'clientes' de tipo 'Page<Cliente>' que contiene la información para poder crear nuestro paginador
			PageRender<Cliente> pageRender = new PageRender<Cliente>("/listar", clientes);
			// Pasamos a la vista el atributo "titulo" con el texto del título de la vista
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.listar.titulo' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			model.addAttribute("titulo", messageSource.getMessage("text.cliente.listar.titulo",null, locale));
			// Pasamos a la vista el atributo 'clientes' con los datos de los clientes con paginación
			model.addAttribute("clientes", clientes);
			// Pasamos a la vista el atributo "page" con nuestro paginador
			model.addAttribute("page", pageRender);
		}
		// En caso contrario, es decir, si el formato de la petición es de tipo JSON,CSV o XML, significa que el usuario quiere el listado(sin paginación) de todos los clientes existentes en la base de datos en alguno de esos formatos
		else
			// Pasamos a la vista un atributo 'clientes' con los datos de todos los clientes recuperados de la base de datos sin paginación a través de nuestro servicio 'clienteService'
			model.addAttribute("clientes", clienteService.findAll());
			
		// Devolvemos la vista "listar"
		return "listar";
	}

	// Método handler que escucha peticiones http de tipo GET para la ruta '/form'
	// Por defecto, si no se indica nada en el atributo 'method' de la anotación @RequestMapping, el tipo de la petición http es Get
	// El argumento de entrada "model" de tipo "Map<String,Object>" nos permite pasar datos desde este controlador a las vistas. Es lo mismo que usar directamente "Model model"
	// El acceso al controlador de la peticion http de tipo Get para la ruta '/form' solo lo tienen los usuarios con role 'ROLE_ADMIN'
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model,Locale locale) { 
		// Creamos una instancia de tipo Cliente para almacenar los datos del cliente que se introduzcan en el formulario de la vista
		Cliente cliente = new Cliente();
		// Pasamos a la vista el atributo "cliente" con la instancia creada anteriormente
		model.put("cliente", cliente);
		// Pasamos a la vista el atributo "titulo" con el texto del título de la vista
		// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.form.titulo.crear' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
		model.put("titulo", messageSource.getMessage("text.cliente.form.titulo.crear", null, locale));
		
		// Devuleve la vista "form" con el formulario
		return "form";
	}

	// Método handler que escucha peticiones http de tipo GET para la ruta '/form/' + id_cliente
	// Por defecto, si no se indica nada en el atributo 'method' de la anotación @RequestMapping, el tipo de la petición http es Get
	// Con @PathVariable recuperamos el id de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// El argumento de entrada "model" de tipo "Map<String,Object>" nos permite pasar datos desde este controlador a las vistas. Es lo mismo que usar directamente "Model model"
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	// El acceso al controlador de la peticion http de tipo Get para la ruta '/form/{id}' solo lo tienen los usuarios con role 'ROLE_ADMIN'
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash, Locale locale) {

		Cliente cliente = null;
		
		// Si el id recuperado de la url es válido, hacemos lo siguiente:
		if (id > 0) {
			// Recuperamos de la base de datos los datos del cliente a partir del id recuperado de la url a través de nuestro servicio "clienteService"
			cliente = clienteService.findOne(id);
			// Si el cliente no se ha localizado, hacemos lo siguiente:
			if (cliente == null) {
				// Creamos el atributo "error" de tipo Flash con un mensaje de error
				// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar' y queremos mantener este atributo para esta segunda petición http
				// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.flash.db.error' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
				flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
				// Devolvemos una redirección al método handler "listar" del controlador asociado a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mencionado)
				return "redirect:/listar";
			}
		}
		// En caso contrario, es decir, si el id recuperado de la url no es válido, hacemos lo siguiente:
		else {
			// Creamos el atributo "error" de tipo Flash con un mensaje de error
			// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar' y queremos mantener este atributo para esta segunda petición http
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.flash.id.error' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.id.error", null, locale));
			// Devolvemos una redirección al método handler "listar" del controlador asociado a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mencionado)
			return "redirect:/listar";
		}
		
		// Si el id es válido y se ha podido localizar el cliente en la base de datos, le pasamos a la vista el atributo "cliente" con el cliente obtenido
		model.put("cliente", cliente);
		// Le pasamos a la vista el atributo "titulo" con el texto del título de la vista
		// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.form.titulo.editar' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
		model.put("titulo", messageSource.getMessage("text.cliente.form.titulo.editar", null, locale));
		// Devuleve la vista "form" con el formulario
		return "form";
	}

	// Método handler que escucha peticiones http de tipo POST para la ruta '/form'
	// Con @RequestParam recuperamos el valor del parámetro 'file'(Es 'file' porque así hemos llamado al input correspondiente del formulario de la vista), que viaja en la url de la petición http y lo asociamos a la instancia 'foto' de tipo 'MultipartFile'
	// El argumento de entrada "model" de tipo "Model" nos permite pasar datos desde este controlador a las vistas
	// El parámetro de entrada "status" de tipo "SessionStatus" nos permite liberar un atributo de sesión
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	// @ Valid es para que se validen,antes de ejecutarse el método, las restricciones de validación(indicada por anotaciones) del objeto 'cliente' que recibimos cuya entidad es 'Cliente'
	// El objeto 'result' de tipo 'BindingResult' contiene la información sobre el resultado de las validaciones
	// Nota: Es importante que el objeto result' de tipo 'BindingResult' esté justo a continuación del objeto que se quiere validar.En este caso,'cliente' de tipo 'Cliente'
	// El acceso al controlador de la peticion http de tipo Post para la ruta '/form' solo lo tienen los usuarios con role 'ROLE_ADMIN'
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form", method = RequestMethod.POST) 
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status, Locale locale) {

		// Si hay errores en las validaciones, hacemos lo siguiente:
		if (result.hasErrors()) {
			// Le pasamos a la vista el atributo "titulo" con el texto del título de la vista
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.form.titulo' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			model.addAttribute("titulo", messageSource.getMessage("text.cliente.form.titulo", null, locale));
			// Devuleve la vista "form" con el formulario
			return "form";
		}

		// Si no hay errores de validación, hacemos lo siguiente:
		// Si la imagen recuperada de la url viene informada, hacemos lo siguiente:
		if (!foto.isEmpty()) {
			// Si el cliente que recibimos de la petición http existe(se comprueba que tenga un id definido y válido), y además, tiene una foto asociada(se comprueba que tenga un nombre de imagen definido y válido),
			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {
				// Eliminamos la imagen actual del cliente a través de nuestro servicio "uploadFileService" para posteriormente asignarle una nueva
				uploadFileService.eliminar(cliente.getFoto());
			}

			String uniqueFilename = null;
			try {
				// Subimos la nueva imagen del cliente, contenida en el parámetro de entrada "foto", a través de nuestro servicio "uploadFileService"
				// Si algo va mal, este servicio devuelve una excepción de tipo 'IOException' que la capturamos
				uniqueFilename = uploadFileService.copiar(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Si la subida de la imagen ha ido bien, hacemos lo siguiente:
			// Creamos el atributo "info" de tipo Flash con un mensaje de informción
			// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar' y queremos mantener este atributo para esta segunda petición http
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.flash.foto.subir.success' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			flash.addFlashAttribute("info", messageSource.getMessage("text.cliente.flash.foto.subir.success", null, locale) + "'" + uniqueFilename + "'");

			// Asociamos la imagen que se acaba de subir con el cliente pasándole el nombre de dicha imagen contenida en 'uniqueFilename'
			cliente.setFoto(uniqueFilename);

		}
		else
			cliente.setFoto("");
		
		// Si el el id del cliente ya existe, creamos un mensaje de éxito sobre la edición porque estamos editando un cliente.En caso contrario, creamos un mensaje de éxtio sobre la creación porque estamos persistiendo un nuevo cliente
		// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.flash.editar.success' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
		// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.flash.crear.success' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
		String mensajeFlash = (cliente.getId() != null) ? messageSource.getMessage("text.cliente.flash.editar.success", null, locale) : messageSource.getMessage("text.cliente.flash.crear.success", null, locale);
		// Creamos el atributo "success" de tipo Flash con el mensaje de éxito creado en el paso anterior
		// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar' y queremos mantener este atributo para esta segunda petición http
		flash.addFlashAttribute("success", mensajeFlash);
		// Persistimos o editamos el cliente en la base de datos a partir de los datos contenidos en el parámetro de entrada "cliente" a través de nuestro servicio "clienteService"
		clienteService.save(cliente);
		// En este momento ya se ha finalizado el proceso de creación o edición de un cliente y, por lo tanto, ya podemos liberar el atributo "cliente" de ser un atributo de sesión
		// Para ello, hacemos uso del método "setComplete()" del parámetro de entrada "status"
		status.setComplete();
		// Devolvemos una redirección al método handler "listar" del controlador asociado a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mencionado)
		return "redirect:listar";
	}

	// Método handler que escucha peticiones http de tipo GET para la ruta '/eliminar/' + id_cliente
	// Por defecto, si no se indica nada en el atributo 'method' de la anotación @RequestMapping, el tipo de la petición http es Get
	// "RedirectAttributes flash" nos permite crear atributos de tipo Flash, que son aquellos que se almacenan en la petición http para este método handler y para la siguiente petición http que se haga desde este método.Los atributos de la vista(model) únicamente se almacenan para esta petción http
	// Con @PathVariable recuperamos el id de la url.Si el nombre de la propiedad Java coincide con el nombre de la variable, no hace falta usar el atributo "value" en esta anotación
	// El acceso al controlador de la peticion http de tipo Get para la ruta '/eliminar/{id}' solo lo tienen los usuarios con role 'ROLE_ADMIN'
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/eliminar/{id}")  
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {
		
		// Si el id recuperado de la url es válido, hacemos lo siguiente:
		if (id > 0) {
			// Antes de eliminar el cliente, lo recuperamos de la base de datos para poder posteriormente eliminar su imagen sociada si la tuviese
			// Para ello, obtenemos el cliente a partir del id recuperado de la url a través de nuestro servicio 'clienteService'
			Cliente cliente = clienteService.findOne(id);
			// Eliminamos el cliente a partir del id recuperado de la url a través de nuestro servicio "clienteService"
			clienteService.delete(id); 
			
			// Creamos el atributo "success" de tipo Flash con un mensaje de éxito
			// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar' y queremos mantener este atributo para esta segunda petición http
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.flash.eliminar.success' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			flash.addFlashAttribute("success",  messageSource.getMessage("text.cliente.flash.eliminar.success", null, locale));
			
			// Eliminamos la imagen asociada al cliente que acabamos de eliminar a través de nuestro servicio "uploadFileService"
			// Si todo ha ido bien, hacemos lo siguiente:
			if (uploadFileService.eliminar(cliente.getFoto())) {
				// Creamos un mensaje de información al usuario
				// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.cliente.flash.foto.eliminar.success' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
				String mensajeFotoEliminar = String.format(messageSource.getMessage("text.cliente.flash.foto.eliminar.success", null, locale), cliente.getFoto());
				// Creamos un atributo de tipo Flash con el mensaje anterior
				// Es de tipo Flash ya que a continuación devolvemos un redirect a '/listar'.Entonces,no vamos directamente a la vista '/listar',sino que volvemos a lanzar una segunda petición http al método handler "listar" asociado a la ruta '/listar' y queremos mantener este atributo para esta segunda petición http
				flash.addFlashAttribute("info", mensajeFotoEliminar);
			}

		}
		// Devolvemos una redirección al método handler "listar" del controlador asociado a la ruta '/listar'(no se va directamente a la vista "listar", primero pasa por el controlador mencionado)
		return "redirect:/listar";
	}
	
	// Método privado que comprueba si un usuario autenticado tiene un determinado role que se le pasa como parámetro de entrada a este método
	private boolean hasRole(String role){
		// Obtenemos el contexto de seguridad y lo almacenamos en la variable "context" de tipo "SecurityContext"
		SecurityContext context = SecurityContextHolder.getContext();
		
		// Si no hay contexto de seguridad, el usuario no se ha podido autenticar y devolvermos false
		if(context == null) 
			return false;
		
		// En caso contrario, es decir, si hay contexto de seguridad, obtenemos los datos de la autenticación
		Authentication auth = context.getAuthentication();
		
		// Si el usuario no está autenticado, devolvemos false
		if(auth == null) 
			return false;
		
		// En caso contrario, es decir, si el usuario está autenticado, obtenemos la lista de roles de dicho usuario
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		
		//Primera forma
		// Para cada role de la lista de roles del usuario, hacemos lo siguiente:
		/*for(GrantedAuthority authoritie:authorities){
		 * 	// Comprobamos si alguno coincide con el role que le pasamos al método y,en caso afirmativo,devolvemos true
			if(role.equals(authoritie.getAuthority()))
				return true;
		}
		
		// Si no ha encontrado ninguna coincidencia en la comprobación anterior,devolvemos false
		return false;
		*/
		// Segunda forma usando el método "contains"
		// A partir de la lista de roles del usuario autenticado obtenida anteriormente, mediante el método "contains" se comprueba si el nombre de alguno de esos roles coincide con el que se le pasa como parámetro de entrada a este método
		// Este método devuelve true o false en función de si localiza la instancia 'SimpleGrantedAuthority(role)' en la lista de roles 'authorities' 
		return authorities.contains(new SimpleGrantedAuthority(role));
	}
}
