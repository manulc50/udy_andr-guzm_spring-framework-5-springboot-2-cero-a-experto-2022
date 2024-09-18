package com.mlorenzo.springboot.error.app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mlorenzo.springboot.error.app.models.domain.Usuario;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	private List<Usuario> usuarios;
	
	public UsuarioServiceImpl() {
		this.usuarios = new ArrayList<Usuario>();
		this.usuarios.add(new Usuario(1,"Ándres","Guzmán"));
		this.usuarios.add(new Usuario(2,"Pepa","García"));
		this.usuarios.add(new Usuario(3,"Lalo","Mena"));
		this.usuarios.add(new Usuario(4,"Luci","Fernández"));
		this.usuarios.add(new Usuario(5,"Pato","González"));
		this.usuarios.add(new Usuario(6,"Paco","Rodríguez"));
		this.usuarios.add(new Usuario(7,"Juan","Gómez"));
	}
	
	@Override
	public List<Usuario> listar() {
		return this.usuarios;
	}

	@Override
	public Usuario obtenerPorId(Integer id) {
		Usuario resultado = null;
		
		for(Usuario u: this.usuarios){
			if(u.getId().equals(id)) {
				resultado = u;
				break;
			}
			
		}
		
		return resultado;
	}

	@Override
	public Optional<Usuario> obtenerPorIdOptional(Integer id) {
		Usuario usuario = this.obtenerPorId(id);
		// Devolvemos un objeto Optional a partir del objeto "usuario" con la posibilidad de que este objeto pueda ser nulo
		return Optional.ofNullable(usuario); // El método "ofNullable" de la clase Optional crea un objeto Optional a partir de otro objeto con la posibilidad de que pueda ser nulo. Sin embargo, el método "of" de la misma clase hace lo mismo pero no es posible que el objeto de ese Optional pueda ser nulo
	}

}
