package com.mlorenzo.springboot.form.app.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mlorenzo.springboot.form.app.models.Pais;

@Service
public class PaisServiceImpl implements PaisService {
	
	private List<Pais> paises;
	
	public PaisServiceImpl() {
		paises = Arrays.asList(
				new Pais(1, "ES", "España"),
				new Pais(2, "MX", "México"),
				new Pais(3, "CL", "Chile"),
				new Pais(4, "ARG", "Argentina"),
				new Pais(5, "PE", "Perú"),
				new Pais(6, "CO", "Colombia"),
				new Pais(7, "VE", "Venezuela"));
	}

	@Override
	public List<Pais> listar() {
		return paises;
	}

	@Override
	public Pais obtenerPorId(Integer id) {
		return paises.stream()
				.filter(pais -> pais.getId() == id).findFirst()
				.orElse(null);
	}

}
