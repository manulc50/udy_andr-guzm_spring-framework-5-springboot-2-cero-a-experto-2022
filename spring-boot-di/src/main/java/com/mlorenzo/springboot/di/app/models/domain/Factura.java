package com.mlorenzo.springboot.di.app.models.domain;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
// Por defecto, como esta clase se trata de un componente de Spring por la anotación anterior, el bean que se almacene en su contenedor o memoria va a ser de tipo Singleton, es decir, el bean va a durar lo que dure la aplicación hasta que se haga undeploy del servidor embebido o interno Tomcat porque solo hay un bean para todas las peticiones http que se realicen mientras la aplicación se esté ejecutando en el servidor
@RequestScope // Con esta anotación, cambiamos el contexto por defecto del bean de Singleton a Request y ahora el bean va a durar lo que dure una petición http, es decir, por cada petición http, se va a crear un bean y se va a destruir cuando finalice su petición http correspondiente 
// @SessionScope // Con esta anotación, cambiamos el contexto por defecto del bean de Singleton a Session y ahora el bean va a durar lo que dure una sesión, es decir, va a durar hasta que se cierre el navegador o cuando se produce un Timeout o cuando se invalida la sesión. Los objetos o bean que usen este contexto o Scope deben implementar la interfaz Serializable
// @ApplicationScope // Con esta anotación, cambiamos el contexto por defecto del bean de Singleton a Application. Es un contexto muy similar a Singleton pero se diferencian en que el bean con contexto o scope Applicaation es un Singleton que se almacena en el contexto del servlet(puede haber varios servlets en la aplicación), sin embargo, el Singleton se almacena en el contexto de la aplicación de Spring(solo hay uno)
public class Factura {
	
	@Value("${factura.descripcion}")
	private String descripcion;
	
	@Autowired
	private Cliente cliente;
	
	@Autowired
	@Qualifier("itemsFacturaOficina")
	private List<ItemFactura> items;
	
	// Esta anotación hace que este método se ejecute justo después de crearse el objeto y después de inyectarse las dependencias anotadas con @Autowired y @Value de Spring
	@PostConstruct
	public void inicializar() {
		cliente.setNombre(cliente.getNombre() + " " + "Manuel");
		descripcion = descripcion + " del cliente: " + cliente.getNombre();
	}
	
	// Esta anotación hace que este método se ejecute justo antes de destruirse el objeto
	// Un objeto de esta clase, como está dentro del contexto de Spring porque esta clase está anotada con @Component, por defecto es un objeto de tipo Singleton y va a estar vivo hasta lo que dure la aplicación, es decir, hasta que se baje(undeploy) la aplicación del servidor embebido o interno Tomcat
	// Nota: Esta anotación no se aplica o no funciona para un contexto o Scope de tipo Sesión(clase anotada con #SessionScope)
	@PreDestroy
	public void destruir() {
		System.out.println("Factura destruida: ".concat(descripcion));
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
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
	
}
