package com.mlorenzo.springboot.app.view.xml;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.xml.MarshallingView;

import com.mlorenzo.springboot.app.models.entity.Cliente;

/* Esta clase se trata de una clase tipo Vista para exportar los datos de los clientes en formato Xml.Esto es así porque extiende de la clase abstracta 'MarshallingView'(para renderizar vistas en formato Xml) de Spring,que,a su vez,extiende de la clase abstracta 'AbstractView'(para renderizar vistas), también de Spring
 Es decir, cuando el método handler 'listar' de nuestro controlador 'ClienteController' devuelva el nombre de la vista 'listar',si existe el parámetro 'format=xml'(habilitado en application.properties') en la url de la petición http,en lugar de renderizarse la vista HTML 'listar.html' localizada en 'templates',se va a renderizar lo implementado en esta clase
 Si se omite el parámetro 'format'(habilitado en application.properties') en la url de la petición('/cliente/listar'),por defecto se renderiza la vista HTML 'listar.html' localizada en 'templates'
 */

// Como una vista siempre tiene que tener un nombre que la identifique,esta clase,que representa una vista,también tiene que tener su nombre.Para ello,lo tenemos que hacer a través de la anotación de Spring @Component.Para que Spring gestione un bean con el nombre de esta vista
// Cuando el parámetro 'format' tiene el valor "xml",se añade en la cabecera de la respuesta de la petición http 'application/xml' para renderizar a formato Xml a través de esta clase de tipo Vista.

/* NOTA: Cuando tenemos varias clases de tipo Vista para un mismo método handler del controlador anotadas con @Component con el mismo nombre de la vista,tenemos que hacer diferentes esos nombres de vistas
Por ejemplo,en esta aplicación tenemos el método handler 'listar' del controlador 'ClienteController', escuchando la petición http de tipo get para la url '/cliente/listar', que redirige a la vista 'listar'.Además, tenemos 3 clases de tipo Vista('AbstractCsvView','ClienteListXmlView' y 'ClienteListJsonView') que usan esa misma vista 'listar' en la anotación @Component
Entonces, tenemos que poner en esas anotaciones @Component diferentes nombres para hacer referencia a esa misma vista,y para ello, ponemos la extensión del media-type correspondiente al final del nombre de la vista,es decir,en 'AbstractCsvView' ponemos  '@Component("listar.csv")',en 'ClienteListXmlView' ponemos '@Component("listar.xml")',y en 'ClienteListJsonView' ponemos '@Component("listar.json")'
Para que esto funcione,también tenemos que habiliar la propiedad 'media-types' para cada tipo en el archivo de configuración de Spring Boot'application.properties'
*/

// Este tipo de clases de vista(las que heredan de 'MarshallingView') no saben tratar directamente con todo lo relacionado con colecciones(List,ArrayList<>,etc...) de objetos,y por este motivo,tenemos que crearnos una clase wrapper o envoltorio(En este caso,'ClienteList') que trate esas coleeciones

@Component("listar.xml") // Indicamos que esta clase se trata de una clase Componente de Spring pàra que lo almacene en su contenedor como un bean y lo pueda gestionar
public class ClienteListXmlView extends MarshallingView{

	// Inyectamos del contenedor de Spring el bean de tipo 'Jaxb2Marshaller', encargado de la serializacion a docmuentos xml, al constructor de esta clase
	@Autowired
	public ClienteListXmlView(Jaxb2Marshaller marshaller) {
		super(marshaller);
	}

	//Sobrescribimos este metodo para filtar la informacion del objeto model antes de renderizar la vista Xml llamando al metodo renderMergedOutputModel de la clase que hereda
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// Obtenemos el atributo 'clientes' del modelo para crear el wrapper antes de eliminarlo.Nos interesa renderizar la clase wrapper que contenga la lista de clientes, no la lista sin envolver
		// El método 'get()' devuelve un objeto de tipo Object y,por ese motivo,tenemos que hacer un casting a nuestro tipo específico 'List<Cliente>'
		@SuppressWarnings("unchecked") // Anotación para quitar un warning que no afecta para nada a la correcta ejecución de la aplicación
		List<Cliente> clientes =  (List<Cliente>)model.get("clientes");
		// Eliminamos el atributo 'clientes' del model
		model.remove("clientes");
		// Creamos un nuevo atributo en el model con la lista de clientes envuelta('ClienteList' es la clase wrapper)
		model.put("clienteList",new ClienteList(clientes));
		
		// Llamamos al metodo de la clase que hereda para que haga la renderizacion a partir del objeto model filtrado
		super.renderMergedOutputModel(model, request, response);
	}

}
