package com.mlorenzo.springboot.form.app.models;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import com.mlorenzo.springboot.form.app.validations.IdentificadorRegex;
import com.mlorenzo.springboot.form.app.validations.Requerido;

public class Usuario {
	
	// Esta propiedad no va a estar en el formulario y, por esta razón, no se valida
	private String identificador;
	
	// Expresión Regular para validar esta propiedad
	// Ejemplo válido: "23.456.789-H"
	// Nota: El rango "[0-9]" se puede poner también como "\\d"
	// Se comenta porque ahora validamos esta propiedad usando nuestro validador personalizado de la clase "IdentificadorRegexValidador" mediante nuestra anotación "@IdentificadorRegex"
	//@Pattern(regexp = "[0-9]{2}[.][\\d]{3}[.][\\d]{3}[-][A-Z]{1}")
	@IdentificadorRegex
	private String idEmpleado;
	
	// Sobrescribimos el mensaje de error por defecto por éste
	// Nota: Si existe un mensaje de error personalizado en el archivo de propiedades "messages.properties" que afecta a esta propiedad, este mensaje de error será sobrescrito por el de dicho archivo
	// Se comenta porque ahora validamos esta propiedad usando nuestro validador personalizado de la clase "UsuarioValidador"
	//@NotEmpty(message = "el nombre no puede ser vacío")
	private String nombre;
	
	// Se comenta porque ahora validamos esta propiedad usando nuestro validador personalizado de la clase "RequeridoValidador" mediante nuestra anotación "@Requerido"
	//@NotEmpty
	@Requerido
	private String apellido;
	
	@NotBlank // Realiza la misma validación que la anotación "@NotEmpty" y, además, valida que no haya espacios en blanco
	@Size(min = 3, max = 8) // Sólo para propiedades de tipo String. Para las propiedades numéricas están las anotaciones @Min y @Max
	private String username;
	
	@NotEmpty
	private String password;
	
	// Validamos esta propiedad usando nuestro validador personalizado de la clase "RequeridoValidador" mediante nuestra anotación "@Requerido"
	@Requerido
	// Sobrescribimos el mensaje de error por defecto por éste
	// Nota: Si existe un mensaje de error personalizado en el archivo de propiedades "messages.properties" que afecta a esta propiedad, este mensaje de error será sobrescrito por el de dicho archivo
	@Email(message = "correo con formato incorrecto")
	private String email;
	
	@NotNull
	@Min(5)
	@Max(5000)
	private Integer cuenta;
	
	@NotNull
	@Past // Anotación que sólo acepta fechas pasadas a la actual
	//@Future // Anotación que sólo acepta fechas posteriores a la actual
	// Nota: Si no se indica un formato, por defecto se usa "yyyy/MM/dd"
	// Se comenta porque ahora formateamos las fechas usando la anotación "@InitBinder" y registrando un "CustomDateEditor"(ver clase "FormController")
	//@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaNacimiento;
	
	// Se comenta porque ahora usamos la propiedad "pais" como un objeto de tipo Pais
	/*@NotEmpty
	private String pais;*/
	
	@NotNull
	private Pais pais;
	
	// Se comenta porque ahora usamos la propiedad "roles" como una lista de objeto de tipo Role
	// Nota: Esta anotación "@NotEmpty", además de usarse en propiedades de tipo String, también puede usarse en listas porque calcula el tamaño de las listas para verificar si están o no vacías
	/*@NotEmpty
	private List<String> roles;*/
	
	// Nota: Esta anotación "@NotEmpty", además de usarse en propiedades de tipo String, también puede usarse en listas porque calcula el tamaño de las listas para verificar si están o no vacías
	@NotEmpty
	private List<Role> roles;
	
	private Boolean habilitar;
	
	@NotEmpty
	private String genero;
	
	private String valorSecreto;
	
	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	
	public String getIdEmpleado() {
		return idEmpleado;
	}

	public void setIdEmpleado(String idEmpleado) {
		this.idEmpleado = idEmpleado;
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
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getCuenta() {
		return cuenta;
	}

	public void setCuenta(Integer cuenta) {
		this.cuenta = cuenta;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public String getValorSecreto() {
		return valorSecreto;
	}

	public void setValorSecreto(String valorSecreto) {
		this.valorSecreto = valorSecreto;
	}
}
