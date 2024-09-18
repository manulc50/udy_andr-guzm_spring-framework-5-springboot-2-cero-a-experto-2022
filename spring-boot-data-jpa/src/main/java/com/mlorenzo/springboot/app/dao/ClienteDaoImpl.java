package com.mlorenzo.springboot.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.mlorenzo.springboot.app.models.entity.Cliente;

//Cuando usamos las interfaces de Spring Data JPA para implementar nuestro repositorio,no hace falta usar la anotación @Repository, pero si usamos nuestra propia interfaz con nuestra propia implementación, como en este caso, sí tenemos que usar dicha anotación

//Indicamos que esta clase se trata de una clase Repositorio(de persistencia o de acceso a datos) de Spring para que lo almacene en su contenedor como un bean y lo gestione y así poder inyectarlo y usarlo en otra parte de este proyecto
@Repository("clienteDaoJPA") // Establecemos el texto "clienteDaoJPA" como nombre del bean 
public class ClienteDaoImpl implements IClienteDao{
	
	// Con esta anotación se inyecta de forma automática el Entity Manager o unidad de persistencia en función de la configuración de la base de datos que hemos indicado en el archivo de configuración de este proyecto "application.properties"
	// Por defecto, si en dicho archivo de configuración no indicamos la configuración de la base de datos, Spring utiliza H2 como base de datos, que es una base de datos en memoria
	@PersistenceContext
	private EntityManager em; // Este bean se encarga de manejar las clases de tipo Entidad o Entity(aquellas anotadas con la anotación @Entity) y de reaalizar las operaciones CRUD a la base de datos a través de estas clases entidad usando el lenguaje JPQL(Lenguaje que se aplica a las clases entidad, y no a las tablas como ocurre en SQL)
	
	// NOTA: Como tenemos una capa de servicio o fachada, las anotaciones @Transactional, para que Spring se haga cargo de las transacciones de la base de datos, pasan a esa capa

	// Método que devuelve todos los clientes existentes en la base de datos
	@SuppressWarnings("unchecked") // Esta anotación quita o elimina el aviso o warning que se indica en el código de este método(no afecta a la correcta funcionalidad del método)
	@Override
	public List<Cliente> findAll() {
		// Usamos el Entity Manager para crear y realizar una consulta en JPQL que devuelva todos los clientes existentes en la base de datos
		return em.createQuery("from Cliente").getResultList();
	}

	// Método que guarda un cliente en la base de datos a partir del objeto de tipo Cliente que se le pasa como argumento de entrada
	@Override
	public void save(Cliente cliente) {
		// Si el cliente que se recibe tiene un id definido, se trata de una edicion porque significa que el cliente ya ha sido creado anteriormente
		if(cliente.getId() != null && cliente.getId().longValue() > 0)
			// Usamos el Entity Manager para editar o actualizar un nuevo cliente existente en la base de datos a partir de los nuevos datos que se le pasa
			em.merge(cliente);
		// En caso contrario, significa que se trata de una nueva creación porque el id aún no existe
		else
			// Usamos el Entity Manager para persistir o guardar un nuevo cliente en la base de datos a partir de los datos del cliente que se le pasa
			em.persist(cliente);
		
	}

	// Método que devuelve un cliente de la base de datos a partir del id que se le pasa como parámetro de entrada
	@Override
	public Cliente findOne(Long id) {
		// Usamos el Entity Manager para localizar y devolver el cliente de la base de datos a partir del id que se le pasa
		// Al método "find()" hay que indicarle el tipo del objeto que va a localizar y devolver de la base de datos. En este caso, el objeto a localizar y devolver es de tipo Cliente
		return em.find(Cliente.class,id);
	}

	// Método que elimina un cliente de la base de datos a partir del id que se le pasa como parámetro de entrada
	@Override
	public void delete(Long id) {
		// Obtenemos el cliente mediante el método "findOne(id)" de esta clase pasándole el id
		Cliente cliente = findOne(id);
		// Usamos el Entity Manager para eliminar el cliente localizado de la base de datos
		em.remove(cliente);
		
	}

}
