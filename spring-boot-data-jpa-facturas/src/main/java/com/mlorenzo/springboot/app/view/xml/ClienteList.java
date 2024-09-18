package com.mlorenzo.springboot.app.view.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.mlorenzo.springboot.app.models.entity.Cliente;

/*
 Esta clase hace de wrapper o envoltorio de la clase Cliente.Es necesario para realizar el proceso de marshalling o serializacion de objetos de tipo Cliente a xml,
 ya que por defecto la clase 'Jaxb2Marshaller'(encarga de este proceso) no sabe tratar con todo lo que tenga que ver con listas de objetos
*/
@XmlRootElement(name="clientes") // Anotacion para indicar que esta clase va a ser el inicio o raiz de nuestro documento xml <clientes>...</clientes>
public class ClienteList {
	
	@XmlElement(name="cliente") // Indicamos que cada cliente de esta lista va a ser un elemento dentro del documento xml con nombre 'cliente' <cliente>...</cliente>
	private List<Cliente> clientes;

	public ClienteList() {

	}

	public ClienteList(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}
	
	// No es necesario el metodo setClientes
}
