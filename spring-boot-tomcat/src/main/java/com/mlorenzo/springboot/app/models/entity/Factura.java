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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonBackReference;

//Como buena práctica,implementamos la interfaz Serializable que es recomendable siempre y cuando una instancia de una clase entidad se quiere usar de manera remota o se quiere pasar o serializar a bytes

//Nota: Por convención, el nombre de una clase Java comienza en mayúscula y es singular.Sin embargo,en una tabla de la base de datos el nombre de una tabla comienza en minúscula y es en plural

@Entity // Indicamos que esta clase se trata de una clase de entidad o persistencia.Es aquella que se mapea con una tabla de la base de datos
@Table(name="facturas") // Esta clase se va a mapear con la tabla 'facturas' en la base de datos
public class Factura implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	// Las propiedades de la clase que no tengan la anotacion @Column van a tener el mismo nombre en relacion con los campos de la tabla de la base de datos
	
	@Id // Indica que esta propiedad va a tratarse de la clave primaria
	@GeneratedValue(strategy=GenerationType.IDENTITY) // Indica el modo de generación de la clave primaria(Con 'GenerationType.IDENTITY' se genera de manera autoincremental)
	private Long id;
	
	@NotEmpty // Validación(no es una restricción a nivel de campo de tabla) de la propiedad descripcion para que no sea vacía
	private String descripcion;
	private String observacion;
	
	@Temporal(TemporalType.DATE) // Con esta anotación indicamos que la fecha se va a guardar en la base de datos en formato Date, es decir, solamente se almacena la fecha(TemporalType.TIME almacena la hora, minutos y segundos, pero no la fecha; TemporalType.TIMESTAMP almacena la fecha y la hora)
	@Column(name="create_at") // Indicamos que la propiedad 'createAt' se va a mapear en un campo de la tabla con nombre 'create_at'
	private Date createAt;
	
	// En una relación uno a muchos la clase entidad dueña de la relación es la que tiene la anotación @ManyToOne porque en esta clase entidad es donde se va a crear la clave foránea y por defecto se genera una automática sin necesidad de usar la anotación @JoinColumn usando el nombre del atributo del otro lado de la relación(@OneToMany) seguido de un guion bajo más el nombre de la propiedad que hace de clave primaria en el otro lado de la relación(@OneToMany)
	// En el caso de esta relación entre Cliente y Factura de tipo uno a muchos bidireccional,debido a que hay la clase Cliente tiene atributo de Factura y la clase Factura tiene atributo de Cliente.Como esta clase es dueña de la relación por ser la del lado @ManyToOne con un atributo Cliente,no hace falta porner la anotación @JoinColumn porque la clave foránea se va a crear automáticamente con el nombre por defecto del nombre del atributo del otro lado de la relación,"cliente",seguido de un guion bajo más el nombre de la propiedad que hace de clave primaria en la entidad Cliente("cliente_id")
	//   Entonces,si deseamos personalizar el nombre de la clave foránea para darle otro valor,podemos usar opcionalmente la anotación @JoinColumn(name="") y en el atributo 'name' establecer el nombre que queramos
	// FetchType.LAZY es una carga perezosa,es decir,carga el cliente justo en el momento que lo necesita,es decir,cuando se ejecuta el método getCliente, y no antes.Es la opción más recomendada.La opción FetchType.EAGER va a llamar a todos clientes que tengan facturas y es peor porque sobrecarga la base de datos
	@ManyToOne(fetch=FetchType.LAZY) // Many hace referencia a la clase donde estamos,es decir, Factura, y One hace referencia a la propiedad,es decir, cliente.Un cliente puede tener muchas facturas
	@JsonBackReference // Parte que no queremos renderizar en la vista json.Con esta anotacion rompemos el buble infinito a la hora de renderizar un documento json debido a su relacion bidireccional con Cliente 
	private Cliente cliente; // Una factura solo puede estar relacionado con un cliente

	// FetchType.LAZY es una carga perezosa,es decir,carga los items justo cuando los necesite,es decir, en el momento que se ejecuta el metodo getItems, y no antes.Es la opcion mas recomendada.La opcion FetchType.EAGER va a llamar a todas los items de cada factura y es peor porque sobrecarga la base de datos
	// cascade=CascadeType.ALL indica que todas las operaciones(insertar,borrar,...) que se hagan sobre cada factura,también se van a relizar en cadena sobre los items.Si tenemos 1 factura con 3 items y se quiere salvar esa factura ,se van a insertar tambien sus 3 items automaticamente.Si en la bd existe 1 factura con 4 items y se elimina esa factura,automaticamente tambien se van a eliminar sus 4 items.
	// En una relación uno a muchos la clase entidad dueña de la relación es la que tiene la anotación @ManyToOne porque en esta clase entidad es donde se va a crear la clave foránea y por defecto se genera una automática sin necesidad de usar la anotación @JoinColumn usando el nombre del atributo del otro lado de la relación(@OneToMany) seguido de un guion bajo más el nombre de la propiedad que hace de clave primaria en el otro lado de la relación(@OneToMany)
	// En esta relación entre Factura e ItemFactura,como es una relación uno a muchos unidireccional, debido a que sólo tenemos un atributo de ItemFactura en esta clase para saber los items asociados a una factura,pero no hay atributo de Factura en la clase ItemFactura porque no nos interesa,no se nos crea automáticamente la clave foránea y aquí sí es necesario usar la anotación @JoinColumn porque estamos en el lado de la relación @OneToMany
	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL) // One hace referencia a la clase donde estamos,es decir,Factura,y Many a la propiedad 'items'.Una factura puede tener muchos items
	@JoinColumn(name="factura_id")
	private List<ItemFactura> items;
	
	// NOTA: Como no hemos definido ningún constructor personalizado(donde se le pase parámetros),Spring Data JPA va a usar este constructor que es el por defecto,el vacío
	public Factura(){
		items = new ArrayList<ItemFactura>(); // inicializamos la lista de ItemFactura como un ArrayList
	}
	
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	// A la hora de serializar un documento xml sobre una entidad Cliente,como posee una relacion bidireccional con la entidad Factura,también se va a serializar en xml dicha entidad,pero esta entidad tambien tiene a Cliente como propiedad(por la relacion bidireccional) y asi sucesivamente.Entonces, se produce un bucle infinito entre serializaciones a xml cuyo problema se resuelve con la anotacion de abajo
	@XmlTransient //Esta anotacion evita que el atributo 'facturas' sea serializado en un documento XML permitiendo que no se entre en un bucle infinito entre entidades Cliente y Factura.
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<ItemFactura> getItems() {
		return items;
	}

	public void setItems(List<ItemFactura> items) {
		this.items = items;
	}

	// Método que añade un nuevo item a la lista de items de la factura
	public void addItemFactura(ItemFactura item){
		items.add(item);
	}
	
	// Método que calcula el importe total de la factura
	public Double getTotal(){
		Double total = 0.;
		
		for(ItemFactura item:items)
			total += item.calcularImporte();
		
		return total;
	}
	

}
