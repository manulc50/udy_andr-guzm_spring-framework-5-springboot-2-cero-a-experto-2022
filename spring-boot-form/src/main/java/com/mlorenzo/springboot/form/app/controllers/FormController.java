package com.mlorenzo.springboot.form.app.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.mlorenzo.springboot.form.app.editors.NombreMayusculaEditor;
import com.mlorenzo.springboot.form.app.editors.PaisPropertyEditor;
import com.mlorenzo.springboot.form.app.editors.RolePropertyEditor;
import com.mlorenzo.springboot.form.app.models.Pais;
import com.mlorenzo.springboot.form.app.models.Role;
import com.mlorenzo.springboot.form.app.models.Usuario;
import com.mlorenzo.springboot.form.app.services.PaisService;
import com.mlorenzo.springboot.form.app.services.RoleService;
import com.mlorenzo.springboot.form.app.validations.UsuarioValidador;

@Controller
// Anotación para guardar el atributo "user" en una sesión http, es decir, crea el atributo de sesión "user"
// Ésto significa que el atributo de sesión "user" va a estar disponible en todas las vistas asociadas a los métodos handler de este controlador hasta que se
// invoque al método "setComplete" del objeto de tipo SessionStatus para eliminarlo
@SessionAttributes("user")
public class FormController {
	
	@Autowired
	private UsuarioValidador usuarioValidador;
	
	@Autowired
	private PaisService paisService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private PaisPropertyEditor paisPropertyEditor;
	
	@Autowired
	private RolePropertyEditor rolePropertyEditor;

	// "InitBinder" es la fase donde se pueblan los datos del formulario y se validan
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// Usamos este método anotado con la anotación "@InitBinder" para registrar nuestro validador personalizado "UsuarioValidador"
		// De esta forma, nuestro validador "UsuarioValidador" se usará automaticamente detrás de escena cuando se utilice la notación "@Valid" sobre algún objeto asociado a algún formulario
		
		// Si usamos este método "setValidator" para el registro, se reemplaza el validador por defecto por el validador que se le pasa como argumento de entrada(En este caso, el validador "UsuarioValidador")
		// Pero, de esta forma, si tenemos más validaciones, como por ejemplo las anotaciones de validación de la clase "Usuario", esas validaciones se pierden
		//binder.setValidator(usuarioValidator);
		
		// Para evitar que se pierdan el resto de validaciones, usamos el método "addValidators" en vez del método "setValidator"
		binder.addValidators(usuarioValidador);
		
		// Formateo de fechas usando @InitBinder y registrando un "CustomDateEditor"
		// Alternativa equivalente a usar la anotación "@DateTimeFormat(pattern = "yyyy-MM-dd")" a nivel de propiedad de tipo Date de una clase
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// Para que sea estricto con el formato de la fecha en la validación, es decir, si es otro formato de fecha, que lance el mensaje de error
		dateFormat.setLenient(false);
		// En este caso, este "CustomDateEditor" se aplica de forma global a todos los campos asociados a propiedades de tipo Date
		//binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
		// En este caso, este "CustomDateEditor" se aplica de forma específica al campo asociado a propiedad "fechaNacimiento" que es de tipo Date
		// Nota: El segundo argumento de entrada del constructor "CustomDateEditor" es para indicar si se permite o no valores vacíos. Entonces, en este caso, lo ponemos a "true" para que se salte esta validación y los valores vacío sean validados por la anotación "@NotNull"(ver clase "Usuario")
		binder.registerCustomEditor(Date.class, "fechaNacimiento", new CustomDateEditor(dateFormat, true));
	
		// En este caso, este "CustomDateEditor" se aplica de forma global a todos los campos asociados a propiedades de tipo String
		//binder.registerCustomEditor(String.class, new NombreMayusculaEditor());
		// En este caso, este "CustomDateEditor" se aplica de forma específica a los campos asociados a propiedades "nombre" y "apellido" que son de tipo String
		binder.registerCustomEditor(String.class, "nombre", new NombreMayusculaEditor());
		binder.registerCustomEditor(String.class, "apellido", new NombreMayusculaEditor());
		
		binder.registerCustomEditor(Pais.class, "pais", paisPropertyEditor);
		
		binder.registerCustomEditor(Role.class, "roles", rolePropertyEditor);
	}
	
	@GetMapping("form/v1")
	public String getformV1(Model model) {
		model.addAttribute("titulo", "Formulario usuarios");
		return "form-v1";
	}
	
	@GetMapping("form/v2")
	public String getFormV2(Model model) {
		model.addAttribute("titulo", "Formulario usuarios");
		return "form-v2";
	}
	
	// En este caso, esta anotación "@ModelAttribute" crea un atributo de vista global llamado "generos" en todas las vistas asociadas a todos los métodos handler de este controlador
	@ModelAttribute("generos") // Si se omite el nombre del atributo, por defecto se usa el nombre del método para el nombre del atributo global
	public List<String> genero() {
		return Arrays.asList("Hombre", "Mujer");
	}
	
	// En este caso, esta anotación "@ModelAttribute" crea un atributo de vista global llamado "listaRoles", es decir, está disponible en todas las vistas asociadas a todos los métodos handler de este controlador
	@ModelAttribute("listaRoles") // Si se omite el nombre del atributo, por defecto se usa el nombre del método para el nombre del atributo global
	public List<Role> listaRoles() {
		return roleService.listar();
	}
	
	// En este caso, esta anotación "@ModelAttribute" crea un atributo de vista global llamado "listaRolesString", es decir, está disponible en todas las vistas asociadas a todos los métodos handler de este controlador
	@ModelAttribute("listaRolesString") // Si se omite el nombre del atributo, por defecto se usa el nombre del método para el nombre del atributo global
	public List<String> listaRolesString() {
		List<String> roles = new ArrayList<>();
		roles.add("ROLE_ADMIN");
		roles.add("ROLE_USER");
		roles.add("ROLE_MODERATOR");
		
		return roles;
	}
	
	// En este caso, esta anotación "@ModelAttribute" crea un atributo de vista global llamado "rolesMap", es decir, está disponible en todas las vistas asociadas a todos los métodos handler de este controlador
	@ModelAttribute("rolesMap") // Si se omite el nombre del atributo, por defecto se usa el nombre del método para el nombre del atributo global
	public Map<String, String> rolesMap() {
		return Map.of("ROLE_ADMIN", "Administrador", "ROLE_USER", "Usuario", "ROLE_MODERATOR", "Moderador");
	}
	
	// En este caso, esta anotación "@ModelAttribute" crea un atributo de vista global llamado "listaPaises", es decir, está disponible en todas las vistas asociadas a todos los métodos handler de este controlador
	@ModelAttribute("listaPaises") // Si se omite el nombre del atributo, por defecto se usa el nombre del método para el nombre del atributo global
	public List<Pais> listaPaises() {
		return paisService.listar();
	}
	
	// En este caso, esta anotación "@ModelAttribute" crea un atributo de vista global llamado "paises", es decir, está disponible en todas las vistas asociadas a todos los métodos handler de este controlador
	@ModelAttribute("paises") // Si se omite el nombre del atributo, por defecto se usa el nombre del método para el nombre del atributo global
	public List<String> paises() {
		return List.of("España", "México", "Chile", "Argentina", "Perú", "Colombia", "Venezuela");
	}
	
	// En este caso, esta anotación "@ModelAttribute" crea un atributo de vista global llamado "paisesMap", es decir, está disponible en todas las vistas asociadas a todos los métodos handler de este controlador
	@ModelAttribute("paisesMap") // Si se omite el nombre del atributo, por defecto se usa el nombre del método para el nombre del atributo global
	public Map<String, String> paisesMap() {
		Map<String, String> paises = new HashMap<>();
		paises.put("ES", "España");
		paises.put("MX", "México");
		paises.put("CL", "Chile");
		paises.put("ARG", "Argentina");
		paises.put("PE", "Perú");
		paises.put("C0", "Colombia");
		paises.put("Ve", "Venezuela");
		
		return paises;
	}
	
	@GetMapping("form/v3")
	public String getFormV3(Model model) {
		model.addAttribute("titulo", "Formulario usuarios");
		Usuario usuario = new Usuario();
		usuario.setNombre("John");
		usuario.setApellido("Doe");
		// Esta propiedad no estás asociada a ningún campo del formulario en la vista pero se quiere mantener su valor hasta que se procese dicho formulario en el método "procesarFormV3"
		// Entonces, para no perder ese valor, usamos la anotación "@SessionAttributes" a nivel de clase para guardar el atributo "user" en la sesión http
		usuario.setIdentificador("123.456.789-K");
		usuario.setPais(new Pais(3, "CL", "Chile"));
		usuario.setRoles(Arrays.asList(new Role(2, "Usuarior", "ROLE_USER")));
		usuario.setGenero("Hombre");
		usuario.setHabilitar(true);
		usuario.setValorSecreto("Algún valor secreto ***");
		model.addAttribute("user", usuario);
		return "form-v3";
	}
	
	@PostMapping("form/v1")
	public String procesarFormV1(@RequestParam String username, @RequestParam String password,
			@RequestParam String email, Model model) {
		
		model.addAttribute("titulo", "Resultado form");
		model.addAttribute("username", username);
		model.addAttribute("password", password);
		model.addAttribute("email", email);
		
		return "resultado-v1";
	}
	
	@PostMapping("form/v2")
	public String procesarFormV2(@RequestParam String nombre, @RequestParam String apellido, 
			@RequestParam String username, @RequestParam String password,
			@RequestParam String email, Model model) {
		
		Usuario usuario = new Usuario();
		usuario.setNombre(nombre);
		usuario.setApellido(apellido);
		usuario.setUsername(username);
		usuario.setPassword(password);
		usuario.setEmail(email);
		
		model.addAttribute("titulo", "Resultado form");
		model.addAttribute("usuario", usuario);
		
		return "resultado-v2";
	}
	
	// Nota sobre la validación de campos: El argumento de entrada de tipo "BindingResult" siempre tiene que ir justo después del argumento de entrada a validar
	// En este caso, con la anotación "@ModelAttribute" podemos personalizar el nombre del atributo para la vista asociado al argumento de entrada "Usuario usuario"
	@PostMapping("form/v3")
	public String procesarFormV3(@Valid @ModelAttribute("user") Usuario usuario, BindingResult result, Model model) {
		// Valida nuestras validaciones personalizadas. Si hay errores, se registran en el objeto "result"
		// Se comenta porque ahora esta validación se realiza de forma automática a través de la anotación "@Valid" debido a que, ahora, nuestro validador personalizado "UsuarioValidador" ha sido registrado en el método "initBinder" mediante la anotación "@InitBinder"
		//usuarioValidador.validate(usuario, result);
		
		if(result.hasErrors()) {
			// Forma manual y explícita
			/*Map<String, String> errores = new HashMap<>();
			
			result.getFieldErrors()
				.forEach(fieldError -> errores.put(fieldError.getField(),
						String.format("El campo %s %s", fieldError.getField(), fieldError.getDefaultMessage())));
			
			model.addAttribute("errores", errores);*/
			
			// La forma recomendada, automática e implícita es usar directamente en la vista del formulario el objeto "fields" de Thymeleaf y su método "hasErrors"
			
			model.addAttribute("titulo", "Resultado form");
			
			return "form-v3";
		}
		
		// No se debe dirigir directamente a una vista justo después de procesar un formulario porque el usuario podría refrescar la página del navegador varias veces provocando un nuevo procesamiento del éste por cada refresco
		// De esta manera, si por ejemplo el procesamiento del formulario consiste en crear registros en la base de datos, estos refrescos provocarían que haya registros duplicados
		//return "resultado-v3";
		// Solución: Hacer una redirección en vez de devolver el nombre de la vista directamente
		return "redirect:/ver";
	}
	
	// Nota: Aquí aún tenemos el atributo de sesión "user" disponible para la vista "resultado-v3"
	// Con la anotación "@SessionAttribute" establecemos un mapeao entre el atributo de sesión "user" y el argumento de entrada de tipo Usuario. Si el nombre del atributo de sesión es igual al nombre del argumento de entrada, se puede omitir
	// El atributo "required" con valor "false" de la anotación "@SessionAttribute" es para indicar que la existencia del atributo de sesión "user" es opcional. Y ésto lo hacemos porque si el usuario refresca la página del navegador, el atributo
	// "user" ya no existe debido a que se invocó al método "setComplete" una vez y lo elimina
	@GetMapping("/ver")
	public String ver(@SessionAttribute(name = "user", required = false) Usuario usuario, Model model, SessionStatus status) {
		// Verificamos el argumento de entrada de tipo Usuario porque el atributo de sesión "user" es opcional y puede no existir
		if(usuario == null)
			return "redirect:/form/v3";
		
		model.addAttribute("titulo", "Resultado form");
		
		// No hace falta porque, en este caso, se crea de forma automática un atributo para la vista asociado al argumento de entrada "Usuario usuario"
		//model.addAttribute("usuario", usuario);
		
		// Se supone que en este punto ya se ha procesado la propiedad "identificador" del usuario y, por lo tanto, ya puede ser eliminado el atributo de sesión "user"
		status.setComplete();
		
		return "resultado-v3";
	}
}
