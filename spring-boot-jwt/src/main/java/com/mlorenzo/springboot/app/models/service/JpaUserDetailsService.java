package com.mlorenzo.springboot.app.models.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mlorenzo.springboot.app.models.dao.IUsuarioDao;
import com.mlorenzo.springboot.app.models.entity.Role;
import com.mlorenzo.springboot.app.models.entity.Usuario;

// UserDetailsService es una interfaz de Spring que nos permite implementar el proceso de login o autenticación de usuarios

//Indicamos que esta clase se trata de una clase Servicio de Spring para que lo almacene en su contenedor como un bean y lo gestione y así poder inyectarlo y usarlo en otra parte de este proyecto
@Service("jpaUserDetailsService") // Establecemos el texto "jpaUserDetailsService" como nombre del bean 
public class JpaUserDetailsService implements UserDetailsService{

	// Recuperamos del contenedor de Spring el bean correspondiente que contiene la implementación de nuestra interfaz IUsuarioDao.En este caso,el bean que la implementa es de Spring,ya que estamos usando su interfaz 'CrudRepository'
	@Autowired
	private IUsuarioDao usuarioDao; // Este bean se corresponde con la capa Dao para hacer operaciones CRUD de la entidad Usuario sobre la base de datos
	
	// Habilitamos el log para esta clase
	private Logger log = LoggerFactory.getLogger(this.getClass());

	// La anotación @Transactional garantiza transaccionalidad a todo el metodo,es decir,todas las acciones que se hagan en un metodo sobre las tablas de la base de datos van a quedar reflejadas si todas han ido bien.Pero si alguna acción falla,ninguna de las del metodo van a tener repercusión en la base de datos.O todas las acciones,o ninguna.
	// Tiene que ser la anotación @Transactional de Spring
	
	// Implementamos este método de la interfaz UserDetailsService de Spring para obtener el username del usuario y sus roles desde la base de datos a través de nuestra capa Dao usuarioDao,y así,poder crear un objeto User de Spring con todos esos datos recuperados
	@Override
	@Transactional(readOnly=true) // Solo de modo lectura,ya que no vamos a manipular ninguna tabla de la base de datos,solo vamos a realizar una consulta
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Obtenmos el usuario con nombre 'username' de la base de datos a través de la capa Dao 'usuarioDao'
		Usuario usuario = usuarioDao.findByUsername(username);
		
		// Si no se ha podido obtener el usuario con nombre 'username' de la base de datos, hacemos lo siguiente:
		if(usuario == null){
			// Escribimos en log de error un mensaje de error
			log.error("Error login: No existe el usuario con '" + username + "'");
			// Y lanzamos una excepción de tipo UsernameNotFoundException con ese mensaje de error
			throw new UsernameNotFoundException("El usuario con username '" + username + "' no existe en el sistema");
		}
		
		// Obtenemos los roles del usuario para pasárselos al objeto User.Para ello,tenemos que convertir la lista obtenida de roles de tipo Role a roles de tipo GrantedAuthority,pero,en realidad,GrantedAuthority es sólo una interfaz que la implementa SimpleGrantedAuthority.Así que,en definitiva, tenemos que converir los roles de tipo Role a roles de tipo SimpleGrantedAuthority
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		// Para cada uno de los roles del usuario recuperado de la base de datos, hacemos lo siguiente:
		for(Role role:usuario.getRoles()){ 
			// Informamos al log con los roles del usuario autenticado
			log.info("Role: " + role.getAuthority());
			// Creamos una instancia de tipo 'SimpleGrantedAuthority'('SimpleGrantedAuthority implementa la interfaz 'GrantedAuthority') para Spring Security y le insertamos el role
			authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
		}
		
		// Si por algun motivo el usuario obtenido de la base de datos no tiene roles asignados,informamos al log en modo error y emitimos una excepcion 'UsernameNotFoundException' con un mensaje de error
		if(authorities.isEmpty()){
			// Escribimos en log de error un mensaje de error
			log.error("Error login: El usuario con '" + username + "' no tiene roles asignados ");
			// Creamos y lanzamos una excepcion 'UsernameNotFoundException' con un mensaje de error
			throw new UsernameNotFoundException("El usuario con username '" + username + "' no tiene roles asignados");
		}		
		
		// Devolvemos una instancia de User con los datos del usuario 'username' obtenidos de la base de datos y sus roles.El objeto User es una implementación de la interfaz UserDetails
		return new User(usuario.getUsername(),usuario.getPassword(),usuario.getEnabled(),true,true,true,authorities);
	}

}
