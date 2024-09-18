package com.mlorenzo.springboot.app.models.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {
	
	// Método que localiza y devuelve una imagen almacenada en el directorio o carpeta de subida a partir del nombre que se le pasa como parámetro de entrada
	public Resource cargar(String nombreFoto) throws MalformedURLException;
	
	// Método que almacena una imagen en el directorio o carpeta de subida a partir de la información contenida en el objeto "archivo" de tipo "MultipartFile"
	public String copiar(MultipartFile archivo) throws IOException;
	
	// Método que elimina una imagen almacenada en el directorio o carpeta de subida y devuelve true, si se ha eliminado con éxito, o false, en caso contrario
	public boolean eliminar(String nombreFoto);
	
	// Método que devuelve la ruta absoluta de una imagen,cuyo nombre se le pasa como parámetro de entrada,almacenada en el directorio o carpeta de subida
	public Path getPath(String nombreFoto);
	
	// Método que elimina de manera recursiva todas las imágenes almacenadas y también elimina el directorio o carpeta donde se encontraban
	public void deleteAll();
	
	// Método que crea una carpeta o directorio donde se van a guardar las imágenes
	public void init() throws IOException;

}
