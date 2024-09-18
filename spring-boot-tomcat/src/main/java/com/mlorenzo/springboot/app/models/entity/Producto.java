package com.mlorenzo.springboot.app.models.entity;

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

//Como buena práctica,implementamos la interfaz Serializable que es recomendable siempre y cuando una instancia de una clase entidad se quiere usar de manera remota o se quiere pasar o serializar a bytes

//Nota: Por convención, el nombre de una clase Java comienza en mayúscula y es singular.Sin embargo,en una tabla de la base de datos el nombre de una tabla comienza en minúscula y es en plural

@Entity // Indicamos que esta clase se trata de una clase de entidad o persistencia.Es aquella que se mapea con una tabla de la base de datos
@Table(name = "productos") // Esta clase se va a mapear con la tabla 'productos' en la base de datos
public class Producto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Las propiedades de la clase que no tengan la anotacion @Column van a tener el mismo nombre en relacion con los campos de la tabla de la base de datos
	
	@Id // Indica que esta propiedad va a tratarse de la clave primaria
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Indica el modo de generación de la clave primaria(Con 'GenerationType.IDENTITY' se genera de manera autoincremental)
	private Long id;
	private String nombre;
	private Double precio;
	
	@Temporal(TemporalType.DATE) // Con esta anotación indicamos que la fecha se va a guardar en la base de datos en formato Date, es decir, solamente se almacena la fecha(TemporalType.TIME almacena la hora, minutos y segundos, pero no la fecha; TemporalType.TIMESTAMP almacena la fecha y la hora)
	@Column(name="create_at") // Indicamos que la propiedad 'createAt' se va a mapear en un campo de la tabla con nombre 'create_at'
	private Date createAt;
	
	// NOTA: Como no hemos definido ningún constructor personalizado(donde se le pasen parámetros),Spring Data JPA va a usar el constructor por defecto que es el vacío
	
	// No hay atributo de ItemFactura ya que no nos interesa tener esa información en este lado de la relacion.Por eso es unidireccional del lado de ItemFactura

	@PrePersist // Esta etiqueta hace que el método se ejecute automáticamente justo antes de invocar al método persist,es decir, justo antes de persistir una factura en la tabla de la base de datos
	public void prePersist(){
		createAt = new Date(); // Creamos una instancia con la fecha actual del sistema
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
