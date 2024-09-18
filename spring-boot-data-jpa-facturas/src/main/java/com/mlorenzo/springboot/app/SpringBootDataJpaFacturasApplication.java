package com.mlorenzo.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mlorenzo.springboot.app.models.service.IUploadFileService;

// Implementamos la interfaz CommandLineRunner para poder ejecutar las tareas indicadas en el método run() antes de la aplicación Spring Boot desde el método main
@SpringBootApplication
public class SpringBootDataJpaFacturasApplication implements CommandLineRunner{
	
	// Recuperamos del contenedor de Spring el bean correspondiente que contiene la implementación de la interfaz PasswordEncoder.En este caso,el bean que la implementa es de tipo BCryptPasswordEncoder.
	@Autowired
	private PasswordEncoder passwordEncoder; // Este bean se corresponde con el codificacdor de contraseñas

	// Recuperamos del contenedor de Spring el bean correspondiente que contiene la implementación de esta interfaz.En este caso,el bean que la implementa es de tipo UploadFileServiceImpl
	@Autowired
	private IUploadFileService uploadFileService; // Este bean se corresponde con la capa de servico para la subida y descarga de archivos de tipo imagen
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootDataJpaFacturasApplication.class, args);
	}

	// Este método, que se sobrescribe, es para ejecutar una serie de tareas antes de que se ejecute la aplicación Spring Boot desde el método main
	// Una tarea consiste en eliminar la carpeta, indicada en la constante DIRECTORIO_UPLOAD("uploads"), junto con sus archvios y volverla a crear
	// La otra tarea consiste en codificar la contraseña "12345" 2 veces con el algoritmo BCryt y mostrar el resultado por pantalla en cada iteración 
	@Override
	public void run(String... args) throws Exception {
		// Elimina todos los archivos que existan en la ruta indicada por la constante DIRECTORIO_UPLOAD("uploads") y también la carpeta.
		uploadFileService.deleteAll();
		// Crea la carpeta indicada en la ruta indicada en la constante DIRECTORIO_UPLOAD("uploads")
		uploadFileService.init();
		
		// Clave a codificar usando "BCrypt"
		String password = "12345";
		// Codificamos 2 veces la contraseña 'password'
		for(int i=0;i<2;i++){
			// Codificamos la contraseña 'password' en cada iteración con "BCryp"t usando el codificador implementado con la clase "BCryptPasswordEncoder"
			String bCryptPassword = passwordEncoder.encode(password);
			// Mostramos por pantalla el resultado de la contraseña codificada 'passwordBCrypt' en cada iteración
			System.out.println(bCryptPassword);
			
		}
		
		
	}
	
	
}
