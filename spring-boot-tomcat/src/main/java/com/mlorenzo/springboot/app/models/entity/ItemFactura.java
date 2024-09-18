package com.mlorenzo.springboot.app.models.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


//Como buena práctica,implementamos la interfaz Serializable que es recomendable siempre y cuando una instancia de una clase entidad se quiere usar de manera remota o se quiere pasar o serializar a bytes

//Nota: Por convención, el nombre de una clase Java comienza en mayúscula y es singular.Sin embargo,en una tabla de la base de datos el nombre de una tabla comienza en minúscula y es en plural

@Entity // Indicamos que esta clase se trata de una clase de entidad o persistencia.Es aquella que se mapea con una tabla de la base de datos
@Table(name = "facturas_items") // Esta clase se va a mapear con la tabla 'facturas_items' en la base de datos
public class ItemFactura implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Las propiedades de la clase que no tengan la anotacion @Column van a tener el mismo nombre en relacion con los campos de la tabla de la base de datos
	
	@Id // Indica que esta propiedad va a tratarse de la clave primaria
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Indica el modo de generación de la clave primaria(Con 'GenerationType.IDENTITY' se genera de manera autoincremental)
	private Long id;
	private Integer cantidad;
	
	// En una relación uno a muchos la clase entidad dueña de la relación es la que tiene la anotación @ManyToOne porque en esta clase entidad es donde se va a crear la clave foránea y por defecto se genera una automática sin necesidad de usar la anotación @JoinColumn usando el nombre del atributo del otro lado de la relación(@OneToMany) seguido de un guion bajo más el nombre de la propiedad que hace de clave primaria en el otro lado de la relación(@OneToMany)
	// En el caso de esta relación entre Producto y ItemFactura de tipo uno a muchos unidireccional,debido a que la clase Producto no tiene atributo de ItemFactura porque no nos interesa,pero esta clase ItemFactura sí tiene un atributo Producto para saber los productos asociados a un item de factura.Como esta clase es dueña de la relación por ser la del lado @ManyToOne con un atributo Producto,no hace falta porner la anotación @JoinColumn porque la clave foránea se va a crear automáticamente con el nombre por defecto del nombre del atributo del otro lado de la relación,"producto",seguido de un guion bajo más el nombre de la propiedad que hace de clave primaria en la entidad Prodcuto("producto_id")
	// Entonces,si deseamos personalizar el nombre de la clave foránea para darle otro valor,podemos usar opcionalmente la anotación @JoinColumn(name="") y en el atributo 'name' establecer el nombre que queramos
	// Lo mas optimo es usar carga perezosa con FetchType.LAZY,es decir,en el momento que se haga un getProducto ir a la base de datos a realizar la consulta.Pero para evitar un problema con las anotaciones @JsonManagedReference-@JsonBackReference a la hora de renderizar la informacion de cada factura en una vista json,tenemos que usar carga EAGER,que lo que hace es realizar la consulta en la base de datos y obtener la informacion de cada producto justo cuando se hace un getItemFactura(y no un getProducto como seria con LAZY)
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="producto_id") // Este @JoinColumn es opcional
	private Producto producto;
	
	// NOTA: Como no hemos definido ningún constructor personalizado(donde se le pasen parámetros),Spring Data JPA va a usar el constructor por defecto que es el vacío
	
	// No hay atributo para la clase Factura ya que desde cada item no nos interesa ver la información de la factura relacionada,pero desde cada factura si nos interesa ver la información de sus items.Por eso la relacion es unidereccional del lado de la entidad Factura

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	// Método que calcula el importe de este item
	public Double calcularImporte() {
		return cantidad.doubleValue() * producto.getPrecio().doubleValue();
	}

}
