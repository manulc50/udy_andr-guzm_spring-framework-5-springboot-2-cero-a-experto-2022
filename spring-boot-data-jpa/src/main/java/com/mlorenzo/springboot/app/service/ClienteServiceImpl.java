package com.mlorenzo.springboot.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mlorenzo.springboot.app.dao.IClienteDao;
import com.mlorenzo.springboot.app.models.entity.Cliente;

//Indicamos que esta clase se trata de una clase Servicio de Spring para que lo almacene en su contenedor como un bean y lo gestione y así poder inyectarlo y usarlo en otra parte de este proyecto
@Service("clienteService") // Establecemos el texto "clienteService" como nombre del bean 
public class ClienteServiceImpl implements IClienteService{

	// Recuperamos del contenedor de Spring el bean correspondiente que contiene la implementación de esta interfaz.En este caso,el bean que la implementa es de tipo ClienteDaoImpl
	@Autowired
	private IClienteDao clienteDao; // Este bean se correponde con la capa Dao para realizar operaciones CRUD sobre la entidad Cliente en la base de datos
	
	// La anotación @Transactional garantiza transaccionalidad a todo el metodo,es decir,todas las acciones que se hagan en un metodo sobre las tablas de la base de datos van a quedar reflejadas si todas han ido bien.Pero si alguna acción falla,ninguna de las del metodo van a tener repercusión en la base de datos.O todas las acciones,o ninguna.
	
	// Método que devuelve todos los clientes existentes en la base de datos a través de la capa Dao
	@Transactional(readOnly=true) // Solo de modo lectura,ya que no vamos a manipular ninguna tabla de la base de datos,solo vamos a realizar una consulta
	@Override
	public List<Cliente> findAll() {
		// Accedemos a nuestra capa Dao "clienteDao" para obtener todos los clientes existentes en la base de datos
		return clienteDao.findAll();
	}

	// Método que guarda un cliente en la base de datos a través de la capa Dao a partir del objeto de tipo Cliente que se le pasa como argumento de entrada
	@Transactional // Sin readOnly=true porque vamos a manipular la base de datos al querer persistir o editar un objeto Cliente en la tabla correspondiente
	@Override
	public void save(Cliente cliente) {
		// Accedemos a nuestra capa Dao "clienteDao" para guardar un cliente en la base de datos a partir de los datos contenidos en el objeto "cliente"
		// Si el cliente no existe, lo persiste y lo devuelve junto con el id generado
		// Si el cliente ya existe, lo edita y lo devuelve con los datos actualizados
		clienteDao.save(cliente);
		
	}

	// Método que devuelve un cliente de la base de datos a través de la capa Dao a partir del id que se le pasa como parámetro de entrada
	@Transactional(readOnly=true) // Solo de modo lectura,ya que no vamos a manipular ninguna tabla de la base de datos,solo vamos a realizar una consulta
	@Override
	public Cliente findOne(Long id) {
		// Accedemos a nuestra capa Dao "clienteDao" para obtener un cliente de la base de datos a partir del id que se le pasa
		return clienteDao.findOne(id);
	}

	// Método que elimina un cliente de la base de datos a través de la capa Dao a partir del id que se le pasa como parámetro de entrada
	@Transactional // Sin readOnly=true porque vamos a manipular la base de datos al querer eliminar un registro de la tabla correspondiente de clientes
	@Override
	public void delete(Long id) {
		// Accedemos a nuestra capa Dao "clienteDao" para eliminar de la base de datos un cliente a partir de su id
		clienteDao.delete(id);
		
	}

}
