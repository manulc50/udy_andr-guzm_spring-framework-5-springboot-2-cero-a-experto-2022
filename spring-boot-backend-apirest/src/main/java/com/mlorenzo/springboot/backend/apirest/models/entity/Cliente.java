package com.mlorenzo.springboot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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
	
	// Con el atributo 'message' podemos personalizar el mensaje de error para validar los campos que queramos y así cambiar el mensaje que hay por defecto
	
	@NotEmpty(message="no puede estar vacío") // Validación(no es una restricción a nivel de campo de tabla) de la propiedad nombre para que no sea vacía
	@Size(min=4,max=12,message="el tamaño tiene que estar entre 4 y 12 caracteres") // Validación(no es una restricción a nivel de campo de tabla) de la propiedad nombre para que tenga como mínimo 4 caracteres y como máximo 12
	@Column(nullable=false) // Restricción a nivel de tabla(se aplica cuando se crea la tabla en la base de datos) para que el campo nombre de la tabla no pueda ser nulo.Si no se indica,por defecto lo es
	private String nombre;
	
	@NotEmpty(message="no puede estar vacío") // Validación(no es una restricción a nivel de campo de tabla) de la propiedad apellido para que no sea vacía
	private String apellido;
	
	@NotEmpty(message="no puede estar vacío") // Validación(no es una restricción a nivel de campo de tabla) de la propiedad email para que no sea vacía
	@Email(message="no es una dirección de correo bien formada") // Validación(no es una restricción a nivel de campo de tabla) de la propiedad email para tenga el formato correcto
	@Column(nullable=false,unique=true) // Restricción a nivel de tabla(se aplica cuando se crea la tabla en la base de datos) para que el campo email de la tabla no pueda ser nulo y,además, tiene que ser único(no puede haber registros en la tabla con emails repetidos)
	private String email;
	
	@Column(name="create_at") // Mapea con el campo 'create_at' en la base de datos
	@Temporal(TemporalType.DATE) // Con esta anotación indicamos que la fecha se va a guardar en la base de datos en formato Date, es decir, solamente se almacena la fecha(TemporalType.TIME almacena la hora, minutos y segundos, pero no la fecha; TemporalType.TIMESTAMP almacena la fecha y la hora)
	private Date createAt;
	
	// Esta etiqueta hace que el metodo se ejecute automaticamente justo antes de invocar al metodo persist,es decir,antes de hacerse el persist en la tabla correspondiente de la base de datos
	@PrePersist
	private void prePersist(){
		createAt = new Date();
	}

	// El contructor vacío es el que va a usar Spring Data JPA
	public Cliente() {
	}
	
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

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	@Override
	public String toString() {
		return nombre + " " + apellido;
	}
	
	

}
