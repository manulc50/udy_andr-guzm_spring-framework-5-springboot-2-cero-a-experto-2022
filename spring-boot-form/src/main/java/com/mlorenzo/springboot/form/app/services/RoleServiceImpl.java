package com.mlorenzo.springboot.form.app.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mlorenzo.springboot.form.app.models.Role;

@Service
public class RoleServiceImpl implements RoleService {
	private List<Role> roles;
	
	public RoleServiceImpl() {
		roles = List.of(
				new Role(1, "Administrador", "ROLE_ADMIN"),
				new Role(2, "Usuarior", "ROLE_USER"),
				new Role(3, "Moderador", "ROLE_MODERATOR"));
	}
	
	@Override
	public List<Role> listar() {
		return roles;
	}

	@Override
	public Role obtenerPorId(Integer id) {
		return roles.stream()
				.filter(role -> role.getId() == id).findFirst()
				.orElse(null);
	}

}
