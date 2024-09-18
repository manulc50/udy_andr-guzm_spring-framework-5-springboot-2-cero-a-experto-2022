package com.mlorenzo.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mlorenzo.springboot.app.models.entity.Cliente;

public interface IClienteService {

	// Método que devuelve todos los clientes existentes en la base de datos sin usar paginación
	public List<Cliente> findAll();
	
	// Método que devuelve todos los clientes existentes en la base de datos usando paginación a partir del objeto de tipo Pageable que se le pasa como argumento de entrada
	public Page<Cliente> findAll(Pageable pageable);

	// Método que guarda un cliente en la base de datos a partir del objeto de tipo Cliente que se le pasa como argumento de entrada
	public Cliente save(Cliente cliente);
	
	// Método que devuelve un cliente de la base de datos a partir del id que se le pasa como parámetro de entrada
	public Cliente findById(Long id);
	
	// Método que elimina un cliente de la base de datos a partir del id que se le pasa como parámetro de entrada
	public void delete(Long id);
	
}

