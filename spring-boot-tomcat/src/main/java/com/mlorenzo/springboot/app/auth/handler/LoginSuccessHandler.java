package com.mlorenzo.springboot.app.auth.handler;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.SessionFlashMapManager;

// Esta clase va a funcionar como un lanzador para mostrar un mensaje de confirmación al usuario en caso de autenticación correcta.Se creará un bean de esta clase para usarlo en la clase de configuración de Spring Security 'SpringSecurityConfig'

@Component // Indicamos que esta clase se trata de una clase Componente de Spring pàra que lo almacene en su contenedor como un bean y lo pueda gestionar
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	
	// Recuperamos del contenedor o memoria de Spring el bean que implmenta la interfaz 'MessageSource'.La implementación de esta interfaz la gestiona Spring al ser una interfaz propia de Spring
	@Autowired
	private MessageSource messageSource; // Este bean es necesario para poder traducir los textos de esta clase a partir de los archivos de idiomas 'message_'
	
	// Recuperamos del contenedor o memoria de Spring el bean que implmenta la interfaz 'LocaleResolver'.Esta interfaz es implementada por la clase 'SessionLocaleResolver'
	@Autowired
    private LocaleResolver localeResolver; // Este bean se corresponde con el locale de la sesión http que determina el idioma de la aplicación

	// Sobrescribimos este método de la clase 'SimpleUrlAuthenticationSuccessHandler' que heredamos para mostrar un mensaje de confirmación al usuario en caso de autenticación correcta
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		// Creamos este manejador para insertarle un FlashMap con un mensaje de confirmación para el usuario autenticado
		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
		// Creamos una instancia de  FlashMap.Su funcionamiento es identico a un HashMap
		FlashMap flashMap = new FlashMap();
		// Obtenemos el locale a partir del bean "localeResolver" necesario para la traducción de textos al idioma correspondiente
		Locale locale = localeResolver.resolveLocale(request);
		// Comprueba si el usuario se ha autenticado y hacemos lo siguiente:
		if(authentication != null){
			// Crea un mensaje de éxito para el usuario autenticado a partir de su nombre que se obtiene del parámetro de entrada "authentication"
			// Obtenemos,a partir del bean 'messageSource', el texto asociado al identificador 'text.login.success' que se encuentra en el archivo de idioma 'message_' correspondiente al locale indicado en el parámetro de entrada 'locale'
			String mensaje = String.format(messageSource.getMessage("text.login.success", null, locale), authentication.getName());
			// Insertamos en el map el atributo "success" con el mensaje de éxito creado en el paso anterior
			flashMap.put("success",mensaje);
			// Guardamos el map 'flashMap' en el manejador 'flashMapManager'
			flashMapManager.saveOutputFlashMap(flashMap,request,response);
			// Escribimos en log a modo de información el mensaje de éxito creado anteriormente
			// El logger está declarado en las clase que hereda 'SimpleUrlAuthenticationSuccessHandler'
			logger.info(mensaje);
		}
		super.onAuthenticationSuccess(request,response,authentication);
	}

}
