package com.mlorenzo.springboot.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.mlorenzo.springboot.app.models.entity.Cliente;

// A la hora de crear un repositorio o Dao con Spring Data JPA,Spring Data JPA nos proporciona 3 tipos de interfaces que podemos extender para implementar este repositorio o Dao.Éstas son: CrudRepository(nos proporciona los métodos básicos para hacer el CRUD),PagingAndSortingRepository(a su vez extiende de CrudRepository y nos proporciona los mismos métodos que CrudRepository más funcionalidades para hacer paginación y ordenación  y JPARepository(a su vez extiende de PagingAndSortingRepository y nos proporicona todos los métodos indicados anteriormente más otras funcionalidades adicionales)
// Cuando usamos las interfaces de Spring Data JPA para implementar nuestro repositorio,no hace falta usar la anotación @Repository.Si usamos nuestra propia interfaz con nuestra propia implementación,sí tenemos que usar dicha anotación.
// "PagingAndSortingRepository" es una interfaz propia de Spring que nos proporciona métodos para realizar operaciones CRUD en la base de datos con funcionalidades de paginación y ordenación.Esta interfaz extiende a su vez de "CrudRepository",que es la que realmente nos proporicona las operaciones básicas para hacer CRUD
// Tenemos que indicar la clase entidad sobra la que vamos a realizar las operaciones CRUD en la base de datos y el tipo de la propiedad de dicha clase que representa la clave primaria
public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long> {
	
	// Las consultas personalizadas en Spring Data JPA se pueden realizar de dos manera;una que,respetando la nomenclatura y estructura indicada por Spring Data JPA,se implementa automáticamente por debajo sin tener que escribirla nosotros
	// Y la otra manera es no respetar la nomenclatura y estructura indicada por Spring Data JPA y escribir o implementar nosotros mismos la consulta usando la anotación @Query
	
	// Creamos una consulta manual(no respeta la nomenclatura indicada por Spring Data JPA) que localiza y obtiene de la base de datos un cliente junto con sus facturas, si tiene, a partir del id que se le pasa como parámetro
	// Esta query personalizada optimiza reduciendo el número de consultas a la base de datos entre las tablas Cliente y Factura mediante el uso de 'join fetch' a través de sus relaciones con sus ids
	// Con 'join fetch' si por ejemplo un cliente no tiene facturas,la consulta no va a devolver nada debido a que no se cumple la condicion,pero en nuestra aplicacion queremos mostrar la informacion del cliente aunque no tenga facturas asociada.Por ello,es necesario realizar la consulta con 'left join fetch' para que en caso de que no haya facturas,siempre devuelda datos la consulta con la informacion del lado del Cliente(izquierdo)
	// Se usa el lenguaje JPQL. Este tipo de consultas se realizan usando los nombres de las clases entidades y no usando los nombres correspondientes de las tablas en la base de datos
	@Query("select c from Cliente c left join fetch c.facturas where c.id=?1") // "?1" hace referencia al primer argumento que se le pasa al método,es decir,a "Long id".Si tuvieramos más filtros en la consulta con "?2","?3",...,harían referencia al segundo argumento,tercer argumento,...,y así sucesivamente
	public Cliente fetchByIdWithFacturas(Long id);

}
