package com.mlorenzo.springboot.app.models.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

//Como buena práctica,implementamos la interfaz Serializable que es recomendable siempre y cuando una instancia de una clase entidad se quiere usar de manera remota o se quiere pasar o serializar a bytes

//Nota: Por convención, el nombre de una clase Java comienza en mayúscula y es singular.Sin embargo,en una tabla de la base de datos el nombre de una tabla comienza en minúscula y es en plural

@Entity // Indicamos que esta clase se trata de una clase de entidad o persistencia.Es aquella que se mapea con una tabla de la base de datos
// Esta clase va a ser mapeada con la tabla 'authorities' de la base de datos
@Table(name="authorities",uniqueConstraints={@UniqueConstraint(columnNames={"user_id","authority"})}) // A nivel de tablas,establecemos una restriccion para tener un indice unico entre los campos 'user_id' y 'authority'
public class Role implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3485743960794639535L;
	
	// Las propiedades de la clase que no tengan la anotacion @Column van a tener el mismo nombre en relacion con los campos de la tabla de la base de datos
	
	@Id // Indica que esta propiedad va a tratarse de la clave primaria
	@GeneratedValue(strategy=GenerationType.IDENTITY) // Indica el modo de generación de la clave primaria(Con 'GenerationType.IDENTITY' se genera de manera autoincremental)
	private Long id;
	
	private String authority;
	
	// NOTA: Como no hemos definido ningún constructor personalizado(donde se le pasen parámetros),Spring Data JPA va a usar el constructor por defecto que es el vacío

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	

}
