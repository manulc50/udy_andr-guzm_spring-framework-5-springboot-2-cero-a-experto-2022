package com.mlorenzo.springboot.app.view.json;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.mlorenzo.springboot.app.models.entity.Cliente;

/* Esta clase se trata de una clase tipo Vista para exportar los datos de los clientes en formato Json.Esto es así porque extiende de la clase abstracta 'MappingJackson2JsonView'(para renderizar vistas en formato Json) de Spring,que,a su vez,extiende de la clase abstracta 'AbstractView'(para renderizar vistas), también de Spring
Es decir, cuando el método handler 'listar' de nuestro controlador 'ClienteController' devuelva el nombre de la vista 'listar',si existe el parámetro 'format=json'(habilitado en application.properties') en la url de la petición http,en lugar de renderizarse la vista HTML 'listar.html' localizada en 'templates',se va a renderizar lo implementado en esta clase
Si se omite el parámetro 'format'(habilitado en application.properties') en la url de la petición('/cliente/listar'),por defecto se renderiza la vista HTML 'listar.html' localizada en 'templates'
*/

// Como una vista siempre tiene que tener un nombre que la identifique,esta clase,que representa una vista,también tiene que tener su nombre.Para ello,lo tenemos que hacer a través de la anotación de Spring @Component.Para que Spring gestione un bean con el nombre de esta vista
// Cuando el parámetro 'format' tiene el valor "json",se añade en la cabecera de la respuesta de la petición http 'application/json' para renderizar a formato Json a través de esta clase de tipo Vista.

/* NOTA: Cuando tenemos varias clases de tipo Vista para un mismo método handler del controlador anotadas con @Component con el mismo nombre de la vista,tenemos que hacer diferentes esos nombres de vistas
Por ejemplo,en esta aplicación tenemos el método handler 'listar' del controlador 'ClienteController', escuchando la petición http de tipo get para la url '/cliente/listar', que redirige a la vista 'listar'.Además, tenemos 3 clases de tipo Vista('AbstractCsvView','ClienteListXmlView' y 'ClienteListJsonView') que usan esa misma vista 'listar' en la anotación @Component
Entonces, tenemos que poner en esas anotaciones @Component diferentes nombres para hacer referencia a esa misma vista,y para ello, ponemos la extensión del media-type correspondiente al final del nombre de la vista,es decir,en 'AbstractCsvView' ponemos  '@Component("listar.csv")',en 'ClienteListXmlView' ponemos '@Component("listar.xml")',y en 'ClienteListJsonView' ponemos '@Component("listar.json")'
Para que esto funcione,también tenemos que habiliar la propiedad 'media-types' para cada tipo en el archivo de configuración de Spring Boot'application.properties'
*/

// A diferencia de las vistas con xml,este tipo de clases de vistas ya estan preparadas para trabajar con listas,colecciones,etc...y no es necesario crear una clase envoltorio o wrapper

@Component("listar.json") // Indicamos que esta clase se trata de una clase Componente de Spring pàra que lo almacene en su contenedor como un bean y lo pueda gestionar
public class ClienteListJsonView extends MappingJackson2JsonView{

	//Sobreescribimos este metodo para filtar la informacion del objeto model antes de renderizar la vista Json llamando al metodo filterModel de la clase que hereda
	@Override
	protected Object filterModel(Map<String, Object> model) {
		// Obtenemos el objeto 'clientes' del modelo 'model' que fue añadido por el método handler 'listar' de nuestro controlador 'ClienteController'
		// El método 'get()' devuelve un objeto de tipo Object y,por ese motivo,tenemos que hacer un casting a nuestro tipo específico 'List<Cliente>'
		@SuppressWarnings("unchecked") // Anotación para quitar un warning que no afecta para nada a la correcta ejecución de la aplicación
		List<Cliente> clientes = (List<Cliente>)model.get("clientes");
		// Limpiamos completamente el modelo de atributos por si acaso hubiera otros atributos que no nos internesa mostrar en la vista Json
		model.clear();
		// Una vez limpio el modelo, añadimos el atributo "clientes" con el listado de todos los clientes, que es la única información que queremos mostrar en la vista Json
		model.put("clientes",clientes);
		// Invocamos al metodo de la clase que hereda con el model filtrado para que haga la renderizacion de la vista
		return super.filterModel(model); 
	}
	
}
