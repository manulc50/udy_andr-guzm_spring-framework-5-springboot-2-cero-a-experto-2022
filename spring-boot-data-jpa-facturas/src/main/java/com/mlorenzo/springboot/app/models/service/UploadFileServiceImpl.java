package com.mlorenzo.springboot.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service // Indicamos que esta clase se trata de una clase Servicio de Spring para que lo almacene en su contenedor como un bean y lo gestione y así poder inyectarlo y usarlo en otra parte de este proyecto
public class UploadFileServiceImpl implements IUploadFileService{
	
	// Nota: Las constantes en Java son siempre 'final'(indica que no se puede alterar su contenido inicial) y 'static'(indica que pertenece a la clase y no a los objetos)
	
	// Constante DIRECTORIO_UPLOAD
	private final static String DIRECTORIO_UPLOAD = "uploads"; // Es 'uploads' debido a que es una carpeta que se encuentra dentro de nuestro proyecto.Si la ruta donde se van a subir las fotos es externa al proyecto,es necesario inidcar la ruta absoluta,como por ejemplo "C://Temp//uploads"
	
	// Habilitamos el uso del log en esta clase
	private final Logger log = LoggerFactory.getLogger(UploadFileServiceImpl.class);	

	// Método que localiza y devuelve una imagen almacenada en el directorio o carpeta de subida a partir del nombre que se le pasa como parámetro de entrada
	@Override
	public Resource cargar(String nombreFoto) throws MalformedURLException {
		// Obtenemos la ruta absoluta o completa de donde se va a obtener la foto invocando al método getPath() a partir de 'nombreFoto'
		Path rutaArchivo = getPath(nombreFoto);
		// Mostamos en el log, a modo de información, la ruta de la foto
		log.info(rutaArchivo.toString());
		
		// Recurso que vamos a retornar en el objeto ResponseEntity	
		// Creamos una objeto de tipo UrlResource a partir de la ruta de la foto convertida a Uri
		// Puede lanzar la excepción 'MalformedURLException'.No la capturamos aquí,sino que la lanzamos fuera de este método al siguiente nivel
		Resource recurso = new UrlResource(rutaArchivo.toUri());
		
		// Si el recurso no existe,o si existe y no se puede leer, lanzamos una excepción RuntimeException con un mensaje de error personalizado
		if(!recurso.exists() || !recurso.isReadable()){
			throw new RuntimeException("Error: no se puede cargar la imagen: " + rutaArchivo.toString());
		}
		
		return recurso;
	}

	// Método que almacena una imagen en el directorio o carpeta de subida a partir de la información contenida en el objeto "archivo" de tipo "MultipartFile"
	@Override
	public String copiar(MultipartFile archivo) throws IOException {
		// Generamos un identificador único en formato string con 'UUID.randomUUID().toString()' y lo concatenamos al nombre original del archivo con el separador '_'.Esto lo hacemos para garantizar que el nombre original del archivo sea único y no se pueda repetir.
		// Algunos navegadores como Edge,cuando seleccionamos una imagen para subirla al servidor,obtiene como nombre de dicha imagen la ruta completa,como por ejemplo 'C:\\usuario\\imagenes\\imagen3.jpg' en lugar de 'imagen3.jpg', y esto da problemas, ya que introduce caracteres ilegales como ':' y '\\'(caracter escapado de '\').Por este motivo,lo reeplazamos por ''(nada)
		// Por último,si el nombre original del archivo tiene espacios en blanco,los eliminamos con el método 'replace'
		String nombreFoto = UUID.randomUUID().toString() + "_" +  archivo.getOriginalFilename().replace(":","").replace("\\","").replace(" ","");
		
		// Obtenemos la ruta absoluta o completa donde se va a subir la foto invocando al método getPath() a partir de 'nombreFoto'
		Path rutaArchivo = getPath(nombreFoto);
		
		// Mostamos en el log, a modo de información, la ruta de la foto
		log.info(rutaArchivo.toString());
		
		// Intenta copiamos el archivo en la ruta indicada en 'rutaArchivo'
		// Puede lanzar la excepción 'IOException'.No la capturamos aquí,sino que la lanzamos fuera de este método al siguiente nivel
		Files.copy(archivo.getInputStream(),rutaArchivo);
		
		return nombreFoto;
	}

	// Método que elimina una imagen almacenada en el directorio o carpeta de subida y devuelve true, si se ha eliminado con éxito, o false, en caso contrario
	@Override
	public boolean eliminar(String nombreFoto) {
		boolean result = false;
		
		// Si dicho nombre viene informado,significa que ya se subió una foto anteiormente para este cliente.Entonces,
		if(nombreFoto != null && nombreFoto.length() > 0){
			// Obtenemos la ruta absoluta o completa donde se localiza la foto invocando al método getPath() a partir de 'nombrefoto'
			Path rutaArchivo = getPath(nombreFoto);
			// Creamos una instancia de tipo File a partir de la ruta obtenida en el paso anterior
			File archivo = rutaArchivo.toFile();
			// Comprobamos si el archivo,cuya instancia hemos obtenido en el paso anterior,existe y se puede leer,y en caso afirmativo,lo eliminamos para posteriormente volver a asignarle una nueva imagen al cliente
			if(archivo.exists() && archivo.canRead()){
				archivo.delete();
				result = true;
			}
		}
		
		return result;
	}

	// Método que devuelve la ruta absoluta de una imagen,cuyo nombre se le pasa como parámetro de entrada,almacenada en el directorio o carpeta de subida
	@Override
	public Path getPath(String nombreFoto) {
		// Creamos una instancia de tipo Path para establecer la ruta a la carpeta donde se encuentra la imagen con nombre 'nombreFoto',que es el nombre que se pasa como parámetro a este método y que se recibe de la entrada
		// Usamos la ruta relativa en 'DIRECTORIO_UPLOAD' y le concatenamos el nombre de la foto contenido en 'nombreFoto' mediante el método 'resolve()'
		// Finalmente,el método 'toAbsolutePath()' nos va a dar la ruta absoluta o completa donde se localiza la foto
		return Paths.get(DIRECTORIO_UPLOAD).resolve(nombreFoto).toAbsolutePath();
	}
	
	// Método que elimina de manera recursiva todas las imágenes almacenadas y también elimina el directorio o carpeta donde se encontraban
	@Override
	public void deleteAll() {
		// Obtiene la ruta del directorio a partir de la constante "DIRECTORIO_UPLOAD" de esta clase y elimina de manera recusiva las imágenes almacenadas y después elimina el propio directorio
		FileSystemUtils.deleteRecursively(Paths.get(DIRECTORIO_UPLOAD).toFile());
		
	}

	// Método que crea una carpeta o directorio donde se van a guardar las imágenes
	@Override
	public void init() throws IOException {
		// Obtiene la ruta del directorio a partir de la constante "DIRECTORIO_UPLOAD" de esta clase y crea el directorio
		Files.createDirectory(Paths.get(DIRECTORIO_UPLOAD));
	}

}
