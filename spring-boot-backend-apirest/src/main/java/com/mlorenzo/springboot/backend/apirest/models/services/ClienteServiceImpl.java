package com.mlorenzo.springboot.backend.apirest.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mlorenzo.springboot.backend.apirest.models.dao.IClienteDao;
import com.mlorenzo.springboot.backend.apirest.models.entity.Cliente;


@Service // Indicamos que esta clase se trata de una clase Servicio de Spring para que lo almacene en su contenedor como un bean y lo gestione y así poder inyectarlo y usarlo en otra parte de este proyecto
public class ClienteServiceImpl implements IClienteService{

	// Recuperamos del contenedor de Spring el bean correspondiente que contiene la implementación de esta interfaz.En este caso,la implentación de esta interfaz la lleva internamente Spring,es decir, Spring contiene un bean con la implementación de su interfaz CrudRepository
	@Autowired
	private IClienteDao clienteDao; // Este bean se correponde con la capa Dao para realizar operaciones CRUD sobre la entidad Cliente en la base de datos
	
	// La anotación @Transactional garantiza transaccionalidad a todo el metodo,es decir,todas las acciones que se hagan en un metodo sobre las tablas de la base de datos van a quedar reflejadas si todas han ido bien.Pero si alguna acción falla,ninguna de las del metodo van a tener repercusión en la base de datos.O todas las acciones,o ninguna.
	
	// Método que devuelve todos los clientes existentes en la base de datos
	@Override
	@Transactional(readOnly = true) // Solo de modo lectura,ya que no vamos a manipular ninguna tabla de la base de datos,solo vamos a realizar una consulta
	public List<Cliente> findAll() {
		// Accedemos a nuestra capa Dao "clienteDao" para obtener todos los clientes existentes en la base de datos
		return (List<Cliente>) clienteDao.findAll();
	}

	// Método que devuelve un cliente de la base de datos a partir del id que se le pasa como parámetro de entrada
	@Override
	@Transactional(readOnly = true) // Solo de modo lectura,ya que no vamos a manipular ninguna tabla de la base de datos,solo vamos a realizar una consulta
	public Cliente findById(Long id) {
		// Accedemos a nuestra capa Dao "clienteDao" para obtener un cliente de la base de datos a partir del id que se le pasa
		// Si no se localiza un cliente con ese id, se devuelve null
		return clienteDao.findById(id).orElse(null);
	}

	// Método que guarda un cliente en la base de datos a partir del objeto de tipo Cliente que se le pasa como argumento de entrada
	@Override
	@Transactional // Sin readOnly=true porque vamos a manipular la base de datos al querer persistir o editar un objeto Cliente en la tabla correspondiente
	public Cliente save(Cliente cliente) {
		// Accedemos a nuestra capa Dao "clienteDao" para guardar un cliente en la base de datos a partir de los datos contenidos en el objeto "cliente"
		// Si el cliente no existe, lo persiste y lo devuelve junto con el id generado
		// Si el cliente ya existe, lo edita y lo devuelve con los datos actualizados
		return clienteDao.save(cliente);
	}

	// Método que elimina un cliente de la base de datos a partir del id que se le pasa como parámetro de entrada
	@Override
	@Transactional // Sin readOnly=true porque vamos a manipular la base de datos al querer eliminar un registro de la tabla correspondiente de clientes
	public void delete(Long id) {
		// Accedemos a nuestra capa Dao "clienteDao" para eliminar de la base de datos un cliente a partir de su id
		clienteDao.deleteById(id);
		
	}
	
	
}
