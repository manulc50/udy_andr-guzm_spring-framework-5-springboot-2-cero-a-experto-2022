package com.mlorenzo.springboot.webflux.app.models.documents;


import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Debido a que estamos usando una base de dato NoSQL(no relacional),aquí ya no manejamos entities,sino documents,aúnque la forma de trabajar con ellos es igual que en las entities

// Una clase Java con la anotación @Document va a ser un documento que va a ser mapeado a una colección de la base de datos no relacional MongoDB en formato Json(Es el formato que interpreta MongoDB)
@Document(collection="productos") // Al igual que en las entities en bd relacionales,podemos dar nombre a la colección donde se van a mapear estos documentos con el parámetro collection
public class Producto {
	
	@Id // Con esta anotación indicamos que esta propiedad de esta clase va a ser la clave primaria
	private String id; // A diferencia de las bd relaciones donde los ids son númericos,en las no relaciones como MongoDB,los ids son alfanuméricos
	
	private String nombre;
	
	private Double precio;
	
	private Date createAt;
	
	// Además de nuestro constructor personalizado para las 2 propiedades nombre y precio,definimos un contructor vacío para que pueda ser manejado por Spring Data Mongo
	public Producto(){
		
	}
	
	public Producto(String nombre, Double precio) {
		this.nombre = nombre;
		this.precio = precio;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public Double getPrecio() {
		return precio;
	}
	
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	
	public Date getCreateAt() {
		return createAt;
	}
	
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	

}
