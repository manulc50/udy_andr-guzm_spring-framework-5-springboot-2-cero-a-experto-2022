package com.mlorenzo.springboot.backend.apirest.models.services;

import java.util.List;

import com.mlorenzo.springboot.backend.apirest.models.entity.Cliente;

public interface IClienteService {

	// Método que devuelve todos los clientes existentes en la base de datos
	public List<Cliente> findAll();
	
	// Método que devuelve un cliente de la base de datos a partir del id que se le pasa como parámetro de entrada
	public Cliente findById(Long id);
	
	// Método que guarda un cliente en la base de datos a partir del objeto de tipo Cliente que se le pasa como argumento de entrada
	public Cliente save(Cliente cliente);
	
	// Método que elimina un cliente de la base de datos a partir del id que se le pasa como parámetro de entrada
	public void delete(Long id);

}

