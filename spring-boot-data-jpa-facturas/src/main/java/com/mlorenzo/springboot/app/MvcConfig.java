package com.mlorenzo.springboot.app;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration // Indicamos que esta clase se trata de una clase de configuración de Spring para que lo almacene en su contenedor como un bean y lo gestione
public class MvcConfig implements WebMvcConfigurer{
	
	// Método para registrar controladores parametrizables
	// Es una alternativa a crear una clase con anotación @Controller de Spring
	// Esta alternativa tiene sentido cuando se quiere implementaar un controlador que no tiene ninguna lógica de negocio y simplemente devuelve una vista
	public void addViewControllers(ViewControllerRegistry registry){
		// Regista un controlador para la ruta '/error_403' que devuelve la vista 'error_403'
		registry.addViewController("/error_403").setViewName("error_403");
	}
	
	// Método que devuelve una instancia de tipo 'BCryptPasswordEncoder', que implementa la interfaz 'PasswordEncoder', para codificar passwords o contraseñas usando el algoritmo "BCrypt" a la hora de guardarlas en la base de datos
	@Bean // Anotación para que Spring almacene en su memoria o contenedor la instancia creada de tipo 'BCryptPasswordEncoder' como un bean y así poder inyectarlo en otra parte del código para usarlo
	public PasswordEncoder passwordEncoder(){
		// Crea una instancia de tipo "BCryptPasswordEncoder" para codificar passwords o contraseñas usando el algoritomo "BCrypt"
		return new BCryptPasswordEncoder();
	}
	
	// Método que realiza el proceso de Marshalling para pasar la información de un objeto a formato xml
	@Bean // La instancia de tipo 'Jaxb2Marshaller' va a ser almacenada por Spring en su contenedor o memoria como un bean para poder inyectarlo en otra parte del código para usarlo 
	public Jaxb2Marshaller jaxb2Marshaller(){
		// Creamos una Instancia de tipo "Jaxb2Marshaller" que realiza el proceso de serialización de objetos Java a documentos xml
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		// Con la invocación a este método indicamos en un array las clases Java que queremos serializar a formato Xml
		// Tienen que ser las clases envoltorio o Wrapper creadas por nosotros
		marshaller.setClassesToBeBound(new Class[]{com.mlorenzo.springboot.app.view.xml.ClienteList.class});
		return marshaller;
	}
	
	// Este método se encarga de crear una implementación de tipo "SessionLocaleResolver" para la interfaz "LocaleResolver", que se trata de un adaptador que nos permite guardar nuestro Locale
	// "LocaleResolver" es una interfaz que puede ser implementada para una sesión http,para una cockie,etc...En nuestro caso,la vamos a implementar para una sesión http mediante la clase "SessionLocaleResolver",que nos permite guardar nuestro Locale en la sesión http
	@Bean // La instancia de tipo 'SessionLocaleResolver' va a ser almacenada por Spring en su contenedor o memoria como un bean para poder inyectarlo en otra parte del código para usarlo 
	public LocaleResolver localeResolver() {
		// Creamos una instancia de la clase "SessionLocaleResolver", que va a ser el adaptador que usemos para implementar la interfaz "LocaleResolver", para almacenar nuestro locale en la sesión http
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		// Registramos en el adaptador nuestro Locale para español de España a partir del código del lenguaje 'es' y el código del país 'ES'
		localeResolver.setDefaultLocale(new Locale("es","ES"));
		// Devolvemos nuestra implementación del adaptador "LocaleResolver"
		return localeResolver;
	}
	
	// Este método crea un interceptor de peticiones http de tipo "LocaleChangeInterceptor"
	// "LocaleChangeInterceptor" es un interceptor que se encarga de interceptar las petición http cuando en la url de dicha petición viaja el parámetro del lenguaje 'lang' para cambiar el Locale por otros códigos de lenguajes y paises
	// Intercepta las peticiones http que tienen el parámetro "lang" porque así se lo hemos indicado en la implemntación de este método
	// Un interceptor se ejecuta siempre antes que el método handler de un controlador
	@Bean // La instancia de tipo 'LocaleChangeInterceptor' va a ser almacenada por Spring en su contenedor o memoria como un bean para poder inyectarlo en otra parte del código para usarlo 
	public LocaleChangeInterceptor localeChangeInterceptor() {
		// Creamos una instancia de tipo "LocaleChangeInterceptor" que va a ser nuestro interceptor
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		// Establecemos en el interceptor el nombre del parámetro("lang") de las url de las peticiones http que tiene que escuchar para interceptarlas
		localeChangeInterceptor.setParamName("lang");
		// Devolvemos nuestra implementación del interceptor
		return localeChangeInterceptor;
	}

	// Sobrescribimos este método de la clase que heredamos WebMvcConfigurer para registrar nuestro interceptor de tipo "LocaleChangeInterceptor",devuelto por nuestro método "localeChangeInterceptor", en la configuración de Spring
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// Registramos nuestro interceptor, devuelto por nuestro método "localeChangeInterceptor", en el contexto de Spring
		registry.addInterceptor(localeChangeInterceptor());
	}

	/*
	 * Otra manera de utilizar la carpeta 'uploads', fuera de la carpeta por defecto 'static', como un recurso accesible por la aplicación, es usando un método handler en el controlador(método handler 'verFoto' del controlador 'ClienteController') que reciba el nombre del archivo al que se quiere acceder y devuelva ese mismo archivo como un recurso
	// Sobrescribimos este método para agregar un nuevo manejador de recursos que se encargue de hacer accesible públicamente la carpeta de subida de imágenes de los clientes desde las vistas cuando su localización se encuentra fuera de la carpeta por defecto de recursos, que es 'static'
	// Es importante hacer este registro ya que, si no lo hacemos, cuando una vista de la aplicación intente acceder a una imagen de la carpeta 'uploads' para mostrarla,no lo va a poder hacer debido a que dicha carpeta y todo su contenido no es un recurso público de la aplicación
	// Entonces,para que esta carpeta y su contenido sea un recurso público accesible por la aplicación,es necesario registrarla en este método como un recurso
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Obtenemos la ruta absoluta o completa donde se localiza nuestra carpeta de subidas de imágenesEn este caso,la queremos en el directorio raíz de nuestro proyecto.Por eso, al método 'get()' únicamente le pasamos el nombre de la carpeta 'uploads'
		// Es importante usar el método 'toUri()' para que agregue el esquema File con el texto 'File:' al inicio de la ruta absoluta o completa
		String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();
		// Registramos la carpeta de subida de imágenes y todo lo que haya dentro de esta('/uploads/**') indicando la ruta absoluta o completa obtenida en el paso anterior
		registry.addResourceHandler("/uploads/**").addResourceLocations(resourcePath);
	}
	
	*/
	
	

}
