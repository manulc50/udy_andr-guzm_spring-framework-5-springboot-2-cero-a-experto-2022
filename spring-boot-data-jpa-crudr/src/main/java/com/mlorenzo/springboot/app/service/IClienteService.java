package com.mlorenzo.springboot.app.service;

import java.util.List;

import com.mlorenzo.springboot.app.models.entity.Cliente;

public interface IClienteService {
	
	// Método que devuelve todos los clientes existentes en la base de datos a través de la capa Dao
	public List<Cliente> findAll();
	
	// Método que guarda un cliente en la base de datos a través de la capa Dao a partir del objeto de tipo Cliente que se le pasa como argumento de entrada
	public void save(Cliente cliente);
	
	// Método que devuelve un cliente de la base de datos a través de la capa Dao a partir del id que se le pasa como parámetro de entrada
	public Cliente findOne(Long id);
	
	// Método que elimina un cliente de la base de datos a través de la capa Dao a partir del id que se le pasa como parámetro de entrada
	public void delete(Long id);
	
}
