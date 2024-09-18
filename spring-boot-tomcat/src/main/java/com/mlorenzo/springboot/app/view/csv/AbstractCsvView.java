package com.mlorenzo.springboot.app.view.csv;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.mlorenzo.springboot.app.models.entity.Cliente;

/* Esta clase se trata de una clase tipo Vista para exportar los datos de los clientes en formato CVS(texto plano).Esto es así porque extiende de la clase abstracta 'AbstractView'(para renderizar vistas) de Spring
  Es decir, cuando el método handler 'listar' de nuestro controlador 'ClienteController' devuelva el nombre de la vista 'listar',si existe el parámetro 'format=csv'(habilitado en application.properties') en la url de la petición http,en lugar de renderizarse la vista HTML 'listar.html' localizada en 'templates',se va a renderizar lo implementado en esta clase
  Si se omite el parámetro 'format'(habilitado en application.properties') en la url de la petición('/cliente/listar'),por defecto se renderiza la vista HTML 'listar.html' localizada en 'templates'
*/

// Como una vista siempre tiene que tener un nombre que la identifique,esta clase,que representa una vista,también tiene que tener su nombre.Para ello,lo tenemos que hacer a través de la anotación de Spring @Component.Para que Spring gestione un bean con el nombre de esta vista
// Cuando el parámetro 'format' tiene el valor "csv",se añade en la cabecera de la respuesta de la petición http 'text/csv' para renderizar a formato CSV a través de esta clase de tipo Vista.

/* NOTA: Cuando tenemos varias clases de tipo Vista para un mismo método handler del controlador anotadas con @Component con el mismo nombre de la vista,tenemos que hacer diferentes esos nombres de vistas
	Por ejemplo,en esta aplicación tenemos el método handler 'listar' del controlador 'ClienteController', escuchando la petición http de tipo get para la url '/cliente/listar', que redirige a la vista 'listar'.Además, tenemos 3 clases de tipo Vista('AbstractCsvView','ClienteListXmlView' y 'ClienteListJsonView') que usan esa misma vista 'listar' en la anotación @Component
	Entonces, tenemos que poner en esas anotaciones @Component diferentes nombres para hacer referencia a esa misma vista,y para ello, ponemos la extensión del media-type correspondiente al final del nombre de la vista,es decir,en 'AbstractCsvView' ponemos  '@Component("listar.csv")',en 'ClienteListXmlView' ponemos '@Component("listar.xml")',y en 'ClienteListJsonView' ponemos '@Component("listar.json")'
	Para que esto funcione,también tenemos que habiliar la propiedad 'media-types' para cada tipo en el archivo de configuración de Spring Boot'application.properties'
*/

// Un archivo csv nos permite cargarlo fácilmente en una tabla de excel

// A diferencia de las vistas con xml,este tipo de clases de vistas ya estan preparadas para trabajar con listas,colecciones,etc...y no es necesario crear una clase envoltorio o wrapper

// A diferencia de una clase de tipo Vista que extiende de 'AbstractPdfView' o 'AbstractXlsView' para exportar datos en PDF o XLS,en Spring no existe ninguna clase especifica que podamos extender para exportar datos en CSV.Por eso,la tenemos que crear nosotros a partir de una clase('AbstractView') de Spring para vistas menos especifica

@Component("listar.csv") // Indicamos que esta clase se trata de una clase Componente de Spring pàra que lo almacene en su contenedor como un bean y lo pueda gestionar
public class AbstractCsvView extends AbstractView{
	
	// Creamos un constructor para establcer el tipo de contenido(o media-types) a 'text/csv'
	public AbstractCsvView(){
		setContentType("text/csv");
	}

	// Sobrescribimos este método booleano para indicar,devolviendo true,que genere un contenido descargable
	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	// Sobrescribimos este método para establecer el nombre de nuestro archivo csv a 'clientes.csv',el tipo de contenido a 'text/csv', y para crear nuestro archivo y escribir la información de los clientes
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// Para establecer el nombre de nuestro archivo csv a 'clientes.csv',tenemos que modificar la cabecera de la respuesta
		response.setHeader("Content-Disposition","attachment; filename=\"clientes.csv\"");
		// En la respuesta establecemos el tipo de contenido indicado en el constructor de esta clase.Para ello,lo recuperamos usando el método 'getContentType()' de la clase que heredamos 'AbstractView'
		response.setContentType(getContentType());
		
		// Obtenemos el objeto 'clientes' del modelo 'model' que fue añadido por el método handler 'listar' de nuestro controlador 'ClienteController'
		// El método 'get()' devuelve un objeto de tipo Object y,por ese motivo,tenemos que hacer un casting a nuestro tipo específico 'List<Cliente>'
		@SuppressWarnings("unchecked") // Anotación para quitar un warning que no afecta para nada a la correcta ejecución de la aplicación
		List<Cliente> clientes = (List<Cliente>)model.get("clientes");
		
		// La instancia 'beanWriter' es un recurso que nos va a permitir escribir la información de los clientes el archivo 'clientes.csv'
		// La clase 'CsvBeanWriter' es una implementación de la interfaz 'ICsvBeanWriter'
		// El primer parámetro del constructor 'CsvBeanWriter' indica el lugar donde queremos guardar el archivo csv.Nosotros los queremos guardar en la respuesta de la petción para que se pueda descargar.Pero otra posibilidad es indicar directamente la ruta y el nombre del archivo donde se guardarán los datos
		// El segundo parámetro de este constructor indica el tipo de configuración(tipos de separadores de datos,tips de saltos de líneas,etc...) del archivo csv.'STANDARD_PREFERENCE' es una constante con la configuración por defecto
		ICsvBeanWriter beanWriter = new CsvBeanWriter(response.getWriter(),CsvPreference.STANDARD_PREFERENCE);
		
		// Creamos la cabecera de nuestro archivo csv
		// Aquí indicamos que datos del cliente queremos mostrar en el archivo csv.Los nombre de estos datos tienen que coincidir con las propiedades correspondientes de la clase entidad 'Cliente'
		String[] header = {"id","nombre","apellido","email","createAt"};
		// Mediante la instancia 'beanWriter' escribimos una línea en el archivo para la cabecera anterior 
		beanWriter.writeHeader(header);
		
		// Para cada cliente en la lista 'clientes' obtenida del modelo 'model',escribimos una línea en el archivo csv a partir de los nombres de los datos que hemos especificado en el objeto cabecera 'header'
		for(Cliente cliente: clientes)
			beanWriter.write(cliente,header);
		
		// Hemos finalizado la escritura de información en el archiv csv y cerramos el recurso 'beanWriter'
		beanWriter.close();
	}

}
