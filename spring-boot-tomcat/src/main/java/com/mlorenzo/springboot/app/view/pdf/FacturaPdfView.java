package com.mlorenzo.springboot.app.view.pdf;

import java.awt.Color;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mlorenzo.springboot.app.models.entity.Factura;
import com.mlorenzo.springboot.app.models.entity.ItemFactura;

/* Esta clase se trata de una clase tipo Vista para exportar los datos de una factura en formato PDF.Esto es así porque extiende de la clase abstracta 'AbstractPdfView'(para renderizar vistas en formato PDF) de Spring,que,a su vez,extiende de la clase abstracta 'AbstractView'(para renderizar vistas), también de Spring
Es decir, cuando el método handler 'ver' de nuestro controlador 'FacturaController' devuelva el nombre de la vista 'factura/ver',si existe el parámetro 'format=pdf'(habilitado en application.properties') en la url de la petición http,en lugar de renderizarse la vista HTML 'ver.html' localizada en 'templates/factura',se va a renderizar lo implementado en esta clase
Si se omite el parámetro 'format'(habilitado en application.properties') en la url de la petición('/factura/ver/ + factura_id'),por defecto se renderiza la vista HTML 'ver.html' localizada en 'templates/factura'
*/

// Como una vista siempre tiene que tener un nombre que la identifique,esta clase,que representa una vista,también tiene que tener su nombre.Para ello,lo tenemos que hacer a través de la anotación de Spring @Component.Para que Spring gestione un bean con el nombre de esta vista
// Cuando el parámetro 'format' tiene el valor "pdf",se añade en la cabecera de la respuesta de la petición http 'application/pdf' para renderizar a formato PDF a través de esta clase de tipo Vista.

/* NOTA: Cuando tenemos varias clases de tipo Vista para un mismo método handler del controlador anotadas con @Component con el mismo nombre de la vista,tenemos que hacer diferentes esos nombres de vistas
Por ejemplo,en esta aplicación tenemos el método handler 'ver' del controlador 'FacturaController', escuchando la petición http de tipo get para la url '/factura/ver + factura_id', que redirige a la vista 'factura/ver'.Además, tenemos 2 clases de tipo Vista('FacturaPdfView' y 'FacturaXlsxView') que usan esa misma vista 'factura/ver' en la anotación @Component
Entonces, tenemos que poner en esas anotaciones @Component diferentes nombres para hacer referencia a esa misma vista,y para ello, ponemos la extensión del media-type correspondiente al final del nombre de la vista,es decir,en 'FacturaPdfView' ponemos  '@Component("factura/ver.pdf")',y en 'FacturaXlsxView' ponemos '@Component("factura/ver.xlsx")'
Para que esto funcione,también tenemos que habiliar la propiedad 'media-types' para cada tipo en el archivo de configuración de Spring Boot'application.properties'
*/

// A diferencia de las vistas con xml,este tipo de clases de vistas ya estan preparadas para trabajar con listas,colecciones,etc...y no es necesario crear una clase envoltorio o wrapper

@Component("factura/ver.pdf") // Indicamos que esta clase se trata de una clase Componente de Spring pàra que lo almacene en su contenedor como un bean y lo pueda gestionar
public class FacturaPdfView extends AbstractPdfView{
	
	// Recuperamos del contenedor o memoria de Spring el bean que implmenta la interfaz 'MessageSource'.La implementación de esta interfaz la gestiona Spring al ser una interfaz propia de Spring
	@Autowired
	private MessageSource messageSource; // Este bean es necesario para poder traducir los textos de esta clase a partir de los archivos de idiomas 'message_'

	// Recuperamos del contenedor o memoria de Spring el bean que implmenta la interfaz 'LocaleResolver'.Esta interfaz es implementada por la clase 'SessionLocaleResolver'(Ver método 'localeResolver()' de la clase 'MvcConfig')'
	@Autowired
	private LocaleResolver localeResolver; // Este bean se corresponde con el adaptado de idiomas que contiene información sobre el locale almacenado en la sesión http
	
	// Sobrescribimos este método de la clase abstracta 'AbstractPdfView' para construir el documento PDF a renderizar
	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writter, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// Obtenemos el objeto 'factura' del modelo 'model' que fue añadido por el método handler 'ver' de nuestro controlador 'FacturaController'
		// El método 'get()' devuelve un objeto de tipo Object y,por ese motivo,tenemos que hacer un casting a nuestro tipo específico 'Factura'
		Factura factura = (Factura)model.get("factura");
		
		// Obtenemos el locale a partir del bean 'localeResolver' que lo recupera de la petción http
		Locale locale = localeResolver.resolveLocale(request);
		
		// Otra manera alternativa para traducir los textos de esta clase usando los archivos de idiomas 'messages_' es usar la instancia que nos devuelve el método 'getMessageSourceAccessor()' de la clase que heredamos 'AbstractPdfView'
		// Este método ya hace por debajo 'Locale locale = localeResolver.resolveLocale(request);' para obtener la información del locale,por lo que no haría falta incluirlo si usamos esta alternativa
		MessageSourceAccessor message = getMessageSourceAccessor();
		
		// Un objeto de tipo 'PdfPTable' representa una tabla en PDF.Le pasamos al constructor el número de columnas de la tabla
		// Creamos una tabla de 1 columna con los datos del cliente
		PdfPTable tabla = new PdfPTable(1);
		// Damos un espacio al final de esta tabla de 20
		tabla.setSpacingAfter(20);
		
		// Para poder personalizar una celda(dar color,padding,etc...) de una tabla,tenemos que crear una celda a través de un objeto de tipo PdfPCell y luego insertarlo en la tabla
		// Creamos una celda de una tabla con el texto "Datos del Cliente"
		// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.factura.ver.datos.cliente' que se encuentra en el archivo de idioma 'message_' correspondiente al locale obtenido de la petición http
		PdfPCell cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.cliente",null, locale)));
		// A esa celda le damos un color de fondo(Azul claro)
		cell.setBackgroundColor(new Color(184,218,255));
		// A esa celda le damos un padding de 8f(float)
		cell.setPadding(8f);
		// Añadimos la celda a la tabla
		tabla.addCell(cell);
		
		// Añadimos una celda de esta columna con el nombre y apellido del cliente
		tabla.addCell(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
		// Añadimos una celda de esta columna con email del cliente
		tabla.addCell(factura.getCliente().getEmail());
		
		// Creamos una segunda tabla de 1 columna con los datos de la factura asociada al cliente
		PdfPTable tabla2 = new PdfPTable(1);
		// Damos un espacio al final de esta tabla de 20
		tabla2.setSpacingAfter(20);
		
		// Creamos una celda de una tabla con el texto "Datos de la  Factura" para personalizarla
		// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.factura.ver.datos.factura' que se encuentra en el archivo de idioma 'message_' correspondiente al locale obtenido de la petición http
		cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.factura",null, locale)));
		// A esa celda le damos un color de fondo(Verde claro)
		cell.setBackgroundColor(new Color(195,230,203));
		// A esa celda le damos un padding de 8f(float)
		cell.setPadding(8f);
		// Añadimos la celda a la tabla
		tabla2.addCell(cell);
		
		// Añadimos una celda de esta columna con el id de la factura
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.cliente.factura.folio' de los archivos de idiomas 'messages_' según la información del locale
		tabla2.addCell(message.getMessage("text.cliente.factura.folio") + ": " + factura.getId());
		// Añadimos una celda de esta columna con la descripción de la factura
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.cliente.factura.descripcion' de los archivos de idiomas 'messages_' según la información del locale
		tabla2.addCell(message.getMessage("text.cliente.factura.descripcion") + ": " + factura.getDescripcion());
		// Añadimos una celda de esta columna con la fecha de creación de la factura
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.cliente.factura.fecha' de los archivos de idiomas 'messages_' según la información del locale
		tabla2.addCell(message.getMessage("text.cliente.factura.fecha") + ": " + factura.getCreateAt());
		
		// Creamos una segunda tabla de 4 columna con los datos de los items(productos) asociados a la factura
		PdfPTable tabla3 = new PdfPTable(4);
		// Definimos la anchura de las 4 columnas de la tabla.La primera columna va a ser más grande que el resto 
		// Son medidas relativas entre ellas
		tabla3.setWidths( new float[]{3.5f,1,1,1});
		// Añadimos los textos de la cabecera de la tabla
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.form.item.nombre' de los archivos de idiomas 'messages_' según la información del locale
		tabla3.addCell(message.getMessage("text.factura.form.item.nombre"));
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.form.item.precio' de los archivos de idiomas 'messages_' según la información del locale
		tabla3.addCell(message.getMessage("text.factura.form.item.precio"));
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.form.item.cantidad' de los archivos de idiomas 'messages_' según la información del locale
		tabla3.addCell(message.getMessage("text.factura.form.item.cantidad"));
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.form.item.total' de los archivos de idiomas 'messages_' según la información del locale
		tabla3.addCell(message.getMessage("text.factura.form.item.total"));
		
		// Para cada línea de producto asociada a la factura,añadimos una celda de la tabla con el nombre del producto,su precio,cantidad del producto y el total 
		for(ItemFactura item: factura.getItems()) {
			tabla3.addCell(item.getProducto().getNombre());
			tabla3.addCell(item.getProducto().getPrecio().toString());
			// Creamos una celda de una tabla con la cantidad del producto para personalizarla
			cell = new PdfPCell(new Phrase(item.getCantidad().toString()));
			// Situamos la cantidad en el centro de la celda
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			// Añadimos la celda a la tabla
			tabla3.addCell(cell);
			tabla3.addCell(item.calcularImporte().toString());
		}
		
		// Creamos un objeto PdfPCell, que representa una celda de una tabla en PDF, con la frase 'Total: ' a partir de un objeto de tipo 'Phrase'
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.form.total' de los archivos de idiomas 'messages_' según la información del locale
		cell = new PdfPCell(new Phrase(message.getMessage("text.factura.form.total") + ": "));
		// Damos a la celda un espacio de 3 columnas de la tabla
		cell.setColspan(3);
		// Situamos la celda a la derecha de la tabla
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		// Añadimos a la tabla la celda,que ocupa 3 columnas,y el total de la factura en la siguiente celda
		tabla3.addCell(cell);
		tabla3.addCell(factura.getTotal().toString());
		
		// El objeto 'document',que se recibe en la entrada de esté método como parámetro,representa el documento PDF
		// Al documneto PDF le añadimos las tres tablas creadas anteriormente
		document.add(tabla);
		document.add(tabla2);
		document.add(tabla3);
		
		
	}



}
