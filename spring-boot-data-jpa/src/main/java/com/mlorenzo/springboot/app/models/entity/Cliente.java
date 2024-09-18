package com.mlorenzo.springboot.app.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

//Como buena práctica,implementamos la interfaz Serializable que es recomendable siempre y cuando una instancia de una clase entidad se quiere usar de manera remota o se quiere pasar o serializar a bytes

//Nota: Por convención, el nombre de una clase Java comienza en mayúscula y es singular.Sin embargo,en una tabla de la base de datos el nombre de una tabla comienza en minúscula y es en plural

//Si se desea que el nombre de la tabla de la base de datos coincida con el nombre de la clase,no hace falta poner la anotación @Table

@Entity // Indicamos que esta clase se trata de una clase de entidad o persistencia.Es aquella que se mapea con una tabla de la base de datos
@Table(name="clientes") // Esta clase representa una entidad que va a ser mapeada con la tabla 'clientes 'de la base de datos
public class Cliente implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8661384449094555735L;

	// Las propiedades de la clase que no tengan la anotacion @Column van a tener el mismo nombre en relacion con los campos de la tabla de la base de datos

	@Id // Indica que esta propiedad va a tratarse de la clave primaria
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Indica el modo de generación de la clave primaria(Con 'GenerationType.IDENTITY' se genera de manera autoincremental)
	private Long id;
	
	@NotEmpty // Validación(no es una restricción a nivel de campo de tabla) de la propiedad nombre para que no sea vacía
	private String nombre;
	
	@NotEmpty // Validación(no es una restricción a nivel de campo de tabla) de la propiedad apellido para que no sea vacía
	private String apellido;
	
	@NotEmpty // Validación(no es una restricción a nivel de campo de tabla) de la propiedad email para que no sea vacía
	@Email // Validación(no es una restricción a nivel de campo de tabla) de la propiedad email para tenga el formato correcto
	private String email;
	
	@NotNull // Validación(no es una restricción a nivel de campo de tabla) de la propiedad fecha para que no sea nula.La anotación @NotEmpty no funcionaría aquí porque es para propiedades String 
	@Column(name="create_at") // Mapea con el campo 'create_at' en la base de datos
	@Temporal(TemporalType.DATE) // Con esta anotación indicamos que la fecha se va a guardar en la base de datos en formato Date, es decir, solamente se almacena la fecha(TemporalType.TIME almacena la hora, minutos y segundos, pero no la fecha; TemporalType.TIMESTAMP almacena la fecha y la hora)
	@DateTimeFormat(pattern="yyyy-MM-dd") // Por defecto,SpringBoot usa el formato "dd/MM/yyyy",pero de esta manera podemos especificar el que queramos
	private Date fecha;
	
	/* No hace falta porque al final la fecha se introduce en el formulario y no hace falta autogenerarla
	@PrePersist // Esta etiqueta hace que el metodo se ejecute automaticamente justo antes de invocar al metodo persist
	public void prePersist(){
		fecha = new Date();
	}*/
	
	// NOTA: Como no hemos definido ningún constructor personalizado,Spring Data JPA va a usar el constructor por defecto que es el vacío

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

}
