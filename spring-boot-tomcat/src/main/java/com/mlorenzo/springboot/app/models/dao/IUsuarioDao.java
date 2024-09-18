package com.mlorenzo.springboot.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mlorenzo.springboot.app.models.entity.Usuario;

// A la hora de crear un repositorio o Dao con Spring Data JPA,Spring Data JPA nos proporciona 3 tipos de interfaces que podemos extender para implementar este repositorio o Dao.Éstas son: CrudRepository(nos proporciona los métodos básicos para hacer el CRUD),PagingAndSortingRepository(a su vez extiende de CrudRepository y nos proporciona los mismos métodos que CrudRepository más funcionalidades para hacer paginación y ordenación  y JPARepository(a su vez extiende de PagingAndSortingRepository y nos proporicona todos los métodos indicados anteriormente más otras funcionalidades adicionales)
// Cuando usamos las interfaces de Spring Data JPA para implementar nuestro repositorio,no hace falta usar la anotación @Repository.Si usamos nuestra propia interfaz con nuestra propia implementación,sí tenemos que usar dicha anotación.
// "CrudRepository" es una interfaz propia de Spring que nos proporciona métodos o funcionalidades básicas para realizar operaciones CRUD en la base de datos
// Tenemos que indicar la clase entidad sobra la que vamos a realizar las operaciones CRUD en la base de datos y el tipo de la propiedad de dicha clase que representa la clave primaria
public interface IUsuarioDao extends CrudRepository<Usuario,Long>{
	
	// Las consultas personalizadas en Spring Data JPA se pueden realizar de dos manera;una que,respetando la nomenclatura y estructura indicada por Spring Data JPA,se implementa automáticamente por debajo sin tener que escribirla nosotros
	// Y la otra manera es no respetar la nomenclatura y estructura indicada por Spring Data JPA y escribir o implementar nosotros mismos la consulta usando la anotación @Query
	
	// Creamos una consulta personalizada de manera automática(respetando la nomenclatura y estructura indicada por Spring Data JPA) que hace una búsqueda de un usuario a partir de su 'username'
	public Usuario findByUsername(String username);
	
	// Un ejemplo de una consulta personalizada manual(no respeta la nomenclatura indicada por Spring Data JPA) que también hace una búsqueda de un usuario a partir de su 'username'
	// Se usa el lenguaje JPQL. Este tipo de consultas se realizan usando los nombres de las clases entidades y no usando los nombres correspondientes de las tablas en la base de datos
	@Query("select u from Usuario u where u.username=?1") // "?1" hace referencia al primer argumento que se le pasa al método,es decir,a "String username".Si tuvieramos más filtros en la consulta con "?2","?3",...,harían referencia al segundo argumento,tercer argumento,...,y así sucesivamente
	public Usuario findByUsername2(String username);
}
