package com.mlorenzo.springboot.app.view.xlsx;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.mlorenzo.springboot.app.models.entity.Factura;
import com.mlorenzo.springboot.app.models.entity.ItemFactura;

/* Esta clase se trata de una clase tipo Vista para exportar los datos de una factura en formato XLSX(Versión más moderna de XLS tradicional).Esto es así porque extiende de la clase abstracta 'AbstractXlsxView'(para renderizar vistas en formato XLSX) de Spring,que,a su vez,extiende de la clase abstracta 'AbstractView'(para renderizar vistas), también de Spring
Es decir, cuando el método handler 'ver' de nuestro controlador 'FacturaController' devuelva el nombre de la vista 'factura/ver',si existe el parámetro 'format=xlsx'(habilitado en application.properties') en la url de la petición http,en lugar de renderizarse la vista HTML 'ver.html' localizada en 'templates/factura,se va a renderizar lo implementado en esta clase
Si se omite el parámetro 'format'(habilitado en application.properties') en la url de la petición('/factura/ver/ + factura_id'),por defecto se renderiza la vista HTML 'ver.html' localizada en 'templates/factura'
*/

// Como una vista siempre tiene que tener un nombre que la identifique,esta clase,que representa una vista,también tiene que tener su nombre.Para ello,lo tenemos que hacer a través de la anotación de Spring @Component.Para que Spring gestione un bean con el nombre de esta vista
// Cuando el parámetro 'format' tiene el valor "xlsx",se añade en la cabecera de la respuesta de la petición http 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' para renderizar a formato XLSX a través de esta clase de tipo Vista.

/* NOTA: Cuando tenemos varias clases de tipo Vista para un mismo método handler del controlador anotadas con @Component con el mismo nombre de la vista,tenemos que hacer diferentes esos nombres de vistas
Por ejemplo,en esta aplicación tenemos el método handler 'ver' del controlador 'FacturaController', escuchando la petición http de tipo get para la url '/factura/ver + factura_id', que redirige a la vista 'factura/ver'.Además, tenemos 2 clases de tipo Vista('FacturaPdfView' y 'FacturaXlsxView') que usan esa misma vista 'factura/ver' en la anotación @Component
Entonces, tenemos que poner en esas anotaciones @Component diferentes nombres para hacer referencia a esa misma vista,y para ello, ponemos la extensión del media-type correspondiente al final del nombre de la vista,es decir,en 'FacturaPdfView' ponemos  '@Component("factura/ver.pdf")',y en 'FacturaXlsxView' ponemos '@Component("factura/ver.xlsx")'
Para que esto funcione,también tenemos que habiliar la propiedad 'media-types' para cada tipo en el archivo de configuración de Spring Boot'application.properties'
*/

// A diferencia de las vistas con xml,este tipo de clases de vistas ya estan preparadas para trabajar con listas,colecciones,etc...y no es necesario crear una clase envoltorio o wrapper

@Component("factura/ver.xlsx") // Indicamos que esta clase se trata de una clase Componente de Spring pàra que lo almacene en su contenedor como un bean y lo pueda gestionar
public class FacturaXlsxView extends AbstractXlsxView{

	// Sobrescribimos este método de la clase abstracta 'AbstractXlsxView' para construir el documento XLSX a renderizar
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// En lugar de inyectar los beans de tipo 'MessageSource' y 'LocaleResolver' para traducir los textos de esta clase usando los archivos de idiomas 'messages_', vamos a usar la instancia que nos devuelve el método 'getMessageSourceAccessor()' de la clase que heredamos 'AbstractXlsxView'
		// Este método ya hace por debajo 'Locale locale = localeResolver.resolveLocale(request);' para obtener la información del locale,por lo que no haría falta incluirlo si usamos esta alternativa
		MessageSourceAccessor message = getMessageSourceAccessor();
		
		// Para establecer el nombre de nuestro archivo xlsx a 'factura_view.xlsx',tenemos que modificar la cabecera de la respuesta
		response.setHeader("Content-Disposition","attachment; filename=\"factura_view.xlsx\"");
		
		// Obtenemos el objeto 'factura' del modelo 'model' que fue añadido por el método handler 'ver' de nuestro controlador 'FacturaController'
		// El método 'get()' devuelve un objeto de tipo Object y,por ese motivo,tenemos que hacer un casting a nuestro tipo específico 'Factura'
		Factura factura = (Factura)model.get("factura");
		
		// Esta instancia de tipo Sheet representa nuestro docuemnto excel,es decir,a partir de este objeto vamos a construir nuestro documento excel
		// Para usar este tipo de objeto para el formato 'xlsx',hay que importar el paquete 'org.apache.poi.ss.usermodel.Sheet'
		// Establecemos el nombre de nuestro documento 'xlsx' a "Factura Spring".Para ello,le pasamos dicho texto al método 'createSheet()' 
		Sheet sheet = workbook.createSheet("Factura Spring");
		
		// Creamos una fila para nuestro documento excel a partir de la instancia 'sheet'.Para ello,le pasamos al método 'createRow()' la posición de la fila en el docmuento.Le pasamos un 0 porque se trata de la primera fila
		Row row = sheet.createRow(0);
		// Para la fila anterior, en 'row',creamos una celda para la primera columna.Como es la primera columna,le pasamos un 0 al método 'createCell()'
		Cell cell = row.createCell(0);
		// Damos el valor 'Datos del cliente' a la celda anterior
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.ver.datos.cliente' de los archivos de idiomas 'messages_' según la información del locale
		cell.setCellValue(message.getMessage("text.factura.ver.datos.cliente"));
		// Creamos otra fila para nuestro documento excel, a partir de la instancia 'sheet', y la situamos en la segunda posición de nuestro docmuento.Para ello,le pasamos un 1 al método 'createRow()'
		row = sheet.createRow(1);
		// Para la fila anterior, en 'row',creamos una celda para la primera columna.Como es la primera columna,le pasamos un 0 al método 'createCell()'
		cell = row.createCell(0);
		// Rellenamos la celda anterior con el nombre del cliente y su apellido del cliente asociado a la factura obtenida del modelo de la vista 'model'
		cell.setCellValue(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
		// Creamos otra fila para nuestro documento excel, a partir de la instancia 'sheet', y la situamos en la tercera posición de nuestro docmuento.Para ello,le pasamos un 2 al método 'createRow()'
		row = sheet.createRow(2);
		// Para la fila anterior, en 'row',creamos una celda para la primera columna.Como es la primera columna,le pasamos un 0 al método 'createCell()'
		cell = row.createCell(0);
		// Rellenamos la celda anterior con el email del cliente asociado a la factura obtenida del modelo de la vista 'model'
		cell.setCellValue(factura.getCliente().getEmail());
		
		// Dejamos la tercera fila vacía para separar los datos del cliente de los datos de la factura
		// Otra forma de crear filas y celdas de manera más directa es ir encadenando la invocación de los métodos en una misma línea
		// A partir de la instancia 'sheet',creamos otra fila posicionada en la quinta posición(le pasamos un 4 al método 'createRow()') del documento y,a continuación,creamos una celda en la primera columna(le pasamos un 0 al método 'createCell()') de dicha fila con el texto "Datos de la factura"  
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.ver.datos.factura' de los archivos de idiomas 'messages_' según la información del locale
		sheet.createRow(4).createCell(0).setCellValue(message.getMessage("text.factura.ver.datos.factura"));
		// A partir de la instancia 'sheet',creamos otra fila posicionada en la sexta posición(le pasamos un 5 al método 'createRow()') del documento y,a continuación,creamos una celda en la primera columna(le pasamos un 0 al método 'createCell()') de dicha fila con el texto "Folio: " + factura_id  
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.cliente.factura.folio' de los archivos de idiomas 'messages_' según la información del locale
		sheet.createRow(5).createCell(0).setCellValue(message.getMessage("text.cliente.factura.folio") + ": " + factura.getId());
		// A partir de la instancia 'sheet',creamos otra fila posicionada en la septima posición(le pasamos un 6 al método 'createRow()') del documento y,a continuación,creamos una celda en la primera columna(le pasamos un 0 al método 'createCell()') de dicha fila con el texto "Descripción: " + factura_description
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.cliente.factura.descripcion' de los archivos de idiomas 'messages_' según la información del locale
		sheet.createRow(6).createCell(0).setCellValue(message.getMessage("text.cliente.factura.descripcion") + ": " + factura.getDescripcion());
		// A partir de la instancia 'sheet',creamos otra fila posicionada en la octaba posición(le pasamos un 7 al método 'createRow()') del documento y,a continuación,creamos una celda en la primera columna(le pasamos un 0 al método 'createCell()') de dicha fila con el texto "Fecha: " + factura_createAt
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.cliente.factura.fecha' de los archivos de idiomas 'messages_' según la información del locale
		sheet.createRow(7).createCell(0).setCellValue(message.getMessage("text.cliente.factura.fecha") + ": " + factura.getCreateAt());
		
		// Dejamos la novena fila vacía del documento xlsx entre los datos de la factura y los datos de línea de productos
		// Creamos un estilo visual para las celdas de la fila de la cabecera para las líneas de producto a partir de la instancia 'workbook'
		CellStyle theaderStyle = workbook.createCellStyle();
		// Establecemos el estilo visual de los bordes de la celda con el tipo 'BorderStyle.MEDIUM'
		theaderStyle.setBorderBottom(BorderStyle.MEDIUM);
		theaderStyle.setBorderTop(BorderStyle.MEDIUM);
		theaderStyle.setBorderRight(BorderStyle.MEDIUM);
		theaderStyle.setBorderLeft(BorderStyle.MEDIUM);
		// Establecemos el color de fondo 'IndexedColors.GOLD.index' y el patrón de relleno para ese color a 'FillPatternType.SOLID_FOREGROUND'
		theaderStyle.setFillForegroundColor(IndexedColors.GOLD.index);
		theaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		// Creamos un estilo visual para las celdas de la filas de las líneas de producto a partir de la instancia 'workbook'
		CellStyle tbodyStyle = workbook.createCellStyle();
		// Establecemos el estilo visual de los bordes de la celda con el tipo 'BorderStyle.THIN'
		tbodyStyle.setBorderBottom(BorderStyle.THIN);
		tbodyStyle.setBorderTop(BorderStyle.THIN);
		tbodyStyle.setBorderRight(BorderStyle.THIN);
		tbodyStyle.setBorderLeft(BorderStyle.THIN);
		
		// A partir de la instancia 'sheet',creamos otra fila posicionada en la decima posición(le pasamos un 9 al método 'createRow()') del documento para añadir una cabecera para las líneas de producto de la factura con los textos "Producto","Precio","Cantidad" y "Total"
		Row header = sheet.createRow(9);
		// A partir de la fila anterior,creamos una celda situada en la primera columna(le pasamos un 0 al método 'createCell()') de dicha fila y le damos el texto "Producto"
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.form.item.nombre' de los archivos de idiomas 'messages_' según la información del locale
		header.createCell(0).setCellValue(message.getMessage("text.factura.form.item.nombre"));
		// A partir de la fila anterior,creamos una celda situada en la segunda columna(le pasamos un 1 al método 'createCell()') de dicha fila y le damos el texto "Precio"
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.form.item.precio' de los archivos de idiomas 'messages_' según la información del locale
		header.createCell(1).setCellValue(message.getMessage("text.factura.form.item.precio"));
		// A partir de la fila anterior,creamos una celda situada en la tercera columna(le pasamos un 2 al método 'createCell()') de dicha fila y le damos el texto "Cantidad"
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.form.item.cantidad' de los archivos de idiomas 'messages_' según la información del locale
		header.createCell(2).setCellValue(message.getMessage("text.factura.form.item.cantidad"));
		// A partir de la fila anterior,creamos una celda situada en la cuarta columna(le pasamos un 3 al método 'createCell()') de dicha fila y le damos el texto "Total"
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.form.item.total' de los archivos de idiomas 'messages_' según la información del locale
		header.createCell(3).setCellValue(message.getMessage("text.factura.form.item.total"));
		// A continuación iteramos cada línea de producto contenida en la factura para añadirlas al documento xlsx
		// Contador de filas que la inicializamos a la undecima porque es la siguiente disponible en nuestro documento xlsx
		
		// Obtenemos las celdas de la fila de la cabecera para las líneas de productos y establecemos el estilo visual 'theaderStyle' a cada una de ellas
		header.getCell(0).setCellStyle(theaderStyle);
		header.getCell(1).setCellStyle(theaderStyle);
		header.getCell(2).setCellStyle(theaderStyle);
		header.getCell(3).setCellStyle(theaderStyle);
		
		int numrow = 10;
		for(ItemFactura item:factura.getItems()){
			// Para cada línea de producto,creamos una fila en la posición del docmuento dada por el contador 'numrow' a partir de la instancia 'sheet'
			row = sheet.createRow(numrow++);
			// Para la fila anterior,creamos 4 celdas situadas en la primera,segunda,tercera y cuarta columna de dicha fila y le añadimos el nombre del producto,su precio,su cantidad y el importe total de la línea respectivamnete.También,a cada celda de cada fila,le damos el estilo visual 'tbodyStyle'
			cell = row.createCell(0);
			cell.setCellValue(item.getProducto().getNombre());
			cell.setCellStyle(tbodyStyle);
			cell = row.createCell(1);
			cell.setCellValue(item.getProducto().getPrecio());
			cell.setCellStyle(tbodyStyle);
			cell = row.createCell(2);
			cell.setCellValue(item.getCantidad());
			cell.setCellStyle(tbodyStyle);
			cell = row.createCell(3);
			cell.setCellValue(item.calcularImporte());
			cell.setCellStyle(tbodyStyle);
		}
		
		// Creamos una última fila a partir de la instancia 'sheet' situada en la posición del documento indicada por el contado 'numrow'(a continuación de las líneas de productos)
		row = sheet.createRow(numrow);
		// Para la fila anterior,creamos dos celdas en las posiciones tercera y cuarta columna de dicha fila y le añadimos el texto "Gran Total:" y el valor del importe total de la factura respectivamente.También damos el estilo visual 'tbodyStyle' a ambas celdas
		cell = row.createCell(2);
		// Con la instancia 'message' de tipo 'MessageSourceAccessor' mostramos el texto asociado al identificador 'text.factura.form.total' de los archivos de idiomas 'messages_' según la información del locale
		cell.setCellValue(message.getMessage("text.factura.form.total") + ":");
		cell.setCellStyle(tbodyStyle);
		cell = row.createCell(3);
		cell.setCellValue(factura.getTotal());
		cell.setCellStyle(tbodyStyle);
		
	}

}
