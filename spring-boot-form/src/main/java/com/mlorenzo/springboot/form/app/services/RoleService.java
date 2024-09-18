package com.mlorenzo.springboot.form.app.services;

import java.util.List;

import com.mlorenzo.springboot.form.app.models.Role;

public interface RoleService {
	public List<Role> listar();
	public Role obtenerPorId(Integer id);
}
