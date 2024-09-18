package com.mlorenzo.springboot.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mlorenzo.springboot.app.models.Usuario;

@Controller
@RequestMapping("/app")
public class IndexController {
	
	@Value("${texto.indexcontroller.index.titulo}")
	private String textoIndex;
	
	@Value("${texto.indexcontroller.perfil.titulo}")
	private String textoPerfil;
	
	@Value("${texto.indexcontroller.listar.titulo}")
	private String textoListar;
	
	// Nota: Los objeto de tipo "Model", "ModelMap", "Map" y "ModelAndView" nos permiten pasar atributos a vistas
	
	// El atributo "usuarios" de vista anotado con @ModelAttribute en el método "poblarUsuarios" es accesibe desde este método handler ya que dicha anotación crear atributos de vista globales
	@GetMapping({"/index", "", "/home"}) 
	public String saludar(Model model){ 
		model.addAttribute("titulo", textoIndex);
		return "index";
	}
	
	// El atributo "usuarios" de vista anotado con @ModelAttribute en el método "poblarUsuarios" es accesibe desde este método handler ya que dicha anotación crear atributos de vista globales
	@GetMapping({"/index/modelmap"}) 
	public String saludar(ModelMap model){ 
		model.addAttribute("titulo", textoIndex);
		return "index";
	}
	
	// El atributo "usuarios" de vista anotado con @ModelAttribute en el método "poblarUsuarios" es accesibe desde este método handler ya que dicha anotación crear atributos de vista globales
	@GetMapping({"/index/map"}) 
	public String saludar(Map<String, Object> map){ 
		map.put("titulo", textoIndex);
		return "index";
	}
	
	// El atributo "usuarios" de vista anotado con @ModelAttribute en el método "poblarUsuarios" es accesibe desde este método handler ya que dicha anotación crear atributos de vista globales
	@GetMapping({"/index/modelandview"}) 
	public ModelAndView saludar(ModelAndView mv){ 
		mv.addObject("titulo", textoIndex);
		mv.setViewName("index");
		return mv;
	}
	
	// El atributo "usuarios" de vista anotado con @ModelAttribute en el método "poblarUsuarios" es accesibe desde este método handler ya que dicha anotación crear atributos de vista globales
	@RequestMapping("/perfil") // Por defecto, el tipo de petición http es Get
	public String perfil(Model model) {
		Usuario usuario = new Usuario();
		usuario.setNombre("Manuel");
		usuario.setApellido("Lorenzo");
		usuario.setEmail("test@gmail.com");
		
		model.addAttribute("usuario", usuario);
		model.addAttribute("titulo", textoPerfil + ": ".concat(usuario.getNombre()));
		
		return "perfil";
	}
	
	// El atributo "usuarios" de vista anotado con @ModelAttribute en el método "poblarUsuarios" es accesibe desde este método handler ya que dicha anotación crear atributos de vista globales
	@RequestMapping("/listar") // Por defecto, el tipo de petición http es Get
	public String listar(Model model) {
		model.addAttribute("titulo", textoListar);
		return "listar";
	}
	
	// La anotación @ModelAttribute nos permite crear un atributo de vista de manera global en función de lo que devuelva un método
	// Este atributo es común a todos los métodos handler del controlador, es decir, todas las vistas devueltas por los métodos handler van a poder usar el atributo anotado con esta anotación
	// Con la anotación @ModelAttribute creamos de manera global el atributo de vista "usuarios" con la lista de usuarios devuelta por este método. Este atributo es compartido por todos los métodos handler de este controlador
	@ModelAttribute("usuarios")
	public List<Usuario> poblarUsuarios(){
		List<Usuario> usuarios = Arrays.asList(
				new Usuario("Manuel","Lorenzo","manuel.lorenzo@gmail.com"),
				new Usuario("John","Doe","john.doe@gmail.com"),
				new Usuario("Jane","Doe","jane.doe@gmail.com"),
				new Usuario("Tornado","Roe","tornado.roe@gmail.com"));
		
		return usuarios;
	}

}
