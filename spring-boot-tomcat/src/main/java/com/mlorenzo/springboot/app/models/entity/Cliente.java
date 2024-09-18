package com.mlorenzo.springboot.app.models.entity;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

//Como buena práctica,implementamos la interfaz Serializable que es recomendable siempre y cuando una instancia de una clase entidad se quiere usar de manera remota o se quiere pasar o serializar a bytes

//Nota: Por convención, el nombre de una clase Java comienza en mayúscula y es singular.Sin embargo,en una tabla de la base de datos el nombre de una tabla comienza en minúscula y es en plural

@Entity // Indicamos que esta clase se trata de una clase de entidad o persistencia.Es aquella que se mapea con una tabla de la base de datos
@Table(name="clientes") // Esta clase va a ser mapeada con la tabla 'clientes' de la base de datos
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
	
	private String foto;
	
	@NotNull // Validación(no es una restricción a nivel de campo de tabla) de la propiedad fecha para que no sea nula.La anotación @NotEmpty no funcionaría aquí porque es para propiedades String 
	@Column(name="create_at") // Mapea con el campo 'create_at' en la base de datos
	@Temporal(TemporalType.DATE) // Con esta anotación indicamos que la fecha se va a guardar en la base de datos en formato Date, es decir, solamente se almacena la fecha(TemporalType.TIME almacena la hora, minutos y segundos, pero no la fecha; TemporalType.TIMESTAMP almacena la fecha y la hora)
	@DateTimeFormat(pattern="yyyy-MM-dd") // Por defecto,SpringBoot usa el formato "dd/MM/yyyy",pero de esta manera podemos especificar el que queramos
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss") // Anotacion para dar formato al timestamp cuando esta informacion se va a renderizar a un documento json
	private Date createAt;
	
	// FetchType.LAZY es una carga perezosa,es decir,carga las facturas justo cuando lo necesita,es decir, en el momento que se ejecuta el metodo getFacturas, y no antes.Es la opcion mas recomendada.La opcion FetchType.EAGER va a llamar a todas las facturas de cada cliente y es peor porque sobrecarga la base de datos
	// cascade=CascadeType.ALL indica que todas las operaciones(insertar,borrar,...) que se hagan sobre cada cliente,también se van a relizar en cadena sobre las facturas.Si tenemos 1 cliente con 3 facuras y se quiere salvar ese cliente ,se van a insertar tambien sus 3 facturas automaticamente.Si en la bd existe 1 cliente con 4 facturas y se elimina ese cliente,automaticamente tambien se van a eliminar sus 4 facturas.
	// mappedBy="cliente" se especifica el nombre de la propiedad de la otra entidad relacionada(Factura.cliente).Establece una relacion bidireccional entre ambas entidades,Cliente y Factura.
	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="cliente") // One hace referencia a la clase donde estamos,es decir,Cliente,y Many a la propiedad 'facturas'.Un cliente puede tener muchas facturas
	//@JsonIgnore // Esta anotacion ignora esta propiedad a la hora de renderizar la informacion de cada cliente en una vista json.Es necesario para evitar el bucle infinito establecido entre las entidades Cliente y Factura a la hora de renderizar la vista
	@JsonManagedReference // Parte que si queremos mostrar en el documento json.La idea es renderizar en la vista json la información de cada cliente con sus facturas evitando el bucle infinito por haber una relacion bidireccional entre esta clase y Factura
	List<Factura> facturas; // Un cliente puede tener muchas facturas
	
	/* No hace falta porque al final la fecha se introduce en el formulario y no hace falta autogenerarla
	@PrePersist // Esta etiqueta hace que el metodo se ejecute automaticamente justo antes de invocar al metodo persist
	public void prePersist(){
		fecha = new Date();
	}*/

	// NOTA: Como no hemos definido ningún constructor personalizado(donde se le pase parámetros),Spring Data JPA va a usar este constructor que es el por defecto,el vacío
	public Cliente() {
		facturas = new ArrayList<Factura>(); // Inicializamos la lista de facturas a un ArrayList
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

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	
	public List<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}
	
	// Metodo que añade una nueva factura al cliente
	public void addFactura(Factura factura){
		facturas.add(factura);
	}

	@Override
	public String toString() {
		return nombre + " " + apellido;
	}
	
	

}
