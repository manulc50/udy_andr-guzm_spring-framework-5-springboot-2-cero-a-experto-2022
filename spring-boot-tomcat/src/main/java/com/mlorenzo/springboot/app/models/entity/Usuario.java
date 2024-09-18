package com.mlorenzo.springboot.app.models.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

//Como buena práctica,implementamos la interfaz Serializable que es recomendable siempre y cuando una instancia de una clase entidad se quiere usar de manera remota o se quiere pasar o serializar a bytes

//Nota: Por convención, el nombre de una clase Java comienza en mayúscula y es singular.Sin embargo,en una tabla de la base de datos el nombre de una tabla comienza en minúscula y es en plural

@Entity // Indicamos que esta clase se trata de una clase de entidad o persistencia.Es aquella que se mapea con una tabla de la base de datos
@Table(name="users") // Esta clase va a ser mapeada con la tabla 'users' de la base de datos
public class Usuario implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3796230173598496911L;
	
	// Las propiedades de la clase que no tengan la anotacion @Column van a tener el mismo nombre en relacion con los campos de la tabla de la base de datos
	
	@Id // Indica que esta propiedad va a tratarse de la clave primaria
	@GeneratedValue(strategy=GenerationType.IDENTITY) // Indica el modo de generación de la clave primaria(Con 'GenerationType.IDENTITY' se genera de manera autoincremental)
	private Long id;
	
	// Restricción a nivel de tabla(se aplica cuando se crea la tabla en la base de datos) para que el campo username de la tabla sea único(no puede haber registros con usernames repetidos). Y también establecemos a nivel de campo de tabla un tamaño máximo de 30 caracteres
	@Column(length=30,unique=true)
	private String username;
	
	// Restricción a nivel de tabla(se aplica cuando se crea la tabla en la base de datos) para que el campo password de la tabla tenga un tamaño máximo de 60 caracteres, ya que vamos a almacenar las contraseñas encriptadas, que suelen ser de tamaños grandes
	@Column(length=60)
	private String password;
	private Boolean enabled;
	
	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL) // La relacion entre esta clase y la clase Role es uno a muchos desde este lado(un usuario puede tener muchos roles).Usamos una carga de datos perezosa(FetchType.LAZY) y efecto cascada para todas las operaciones que se hagan sobre cada usuario,tambien se hagan sobre cada role relacionado
	@JoinColumn(name="user_id") // Indicamos la clave extranjera o foranea que establece la relacion con la clase Role.'user_id' es el nombre del campo en la tabla 'authorities' mapeada con la clase 'Role' que establece la relacion con los usuarios de la tabla 'users'
	private List<Role> roles;
	
	// NOTA: Como no hemos definido ningún constructor personalizado(donde se le pasen parámetros),Spring Data JPA va a usar el constructor por defecto que es el vacío

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	

}
