package com.mlorenzo.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mlorenzo.springboot.app.models.entity.Cliente;
import com.mlorenzo.springboot.app.models.entity.Factura;
import com.mlorenzo.springboot.app.models.entity.Producto;

public interface IClienteService {

	// Método que devuelve todos los clientes existentes en la base de datos sin usar paginación
	public List<Cliente> findAll();
	
	// Método que devuelve todos los clientes existentes en la base de datos usando paginación a partir del objeto de tipo Pageable que se le pasa como argumento de entrada
	public Page<Cliente> findAll(Pageable pageable);

	// Método que guarda un cliente en la base de datos a partir del objeto de tipo Cliente que se le pasa como argumento de entrada
	public void save(Cliente cliente);
	
	// Método que devuelve un cliente de la base de datos a partir del id que se le pasa como parámetro de entrada
	public Cliente findOne(Long id);
	
	// Método que devuelve de forma óptima un cliente de la base de datos junto con sus facturas, si tiene, a partir del id que se le pasa como argumento de entrada
	public Cliente fetchByIdWithFacturas(Long id);
	
	// Método que elimina un cliente de la base de datos a partir del id que se le pasa como parámetro de entrada
	public void delete(Long id);
	
	// Método que devuelve los productos de la base de datos cuyos nombres coinciden con el término que se le pasa como parámetro de entrada
	public List<Producto> findByNombre(String term);
	
	// Método que guarda una factura en la base de datos a partir del objeto de tipo Factura que se le pasa como parámetro de entrada
	public Factura saveFactura(Factura factura);
	
	// Método que devuelve un producto de la base de datos a partir del id que se le pasa como argumento de entrada
	public Producto findProductoById(Long id);
	
	// Método que devuelve una factura de la base de datos a partir del id que se le pasa como argumento de entrada
	public Factura findFacturaById(Long id);
	
	// Método que elimina una factura de la base de datos a partir del id que se le pasa como argumento de entrada al método
	public void deleteFactura(Long id);
	
	// Método que devuelve de manera óptima una factura de la base datos junto con sus líneas de productos y el cliente al que pertenece a partir del id que se le pasa como parámetro de entrada
	public Factura fetchFacturaByIdWithClienteWhithItemFacturaWithProducto(Long id);

}

