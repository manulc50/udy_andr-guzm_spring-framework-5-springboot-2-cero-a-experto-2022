package com.mlorenzo.springboot.app.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.mlorenzo.springboot.app.auth.service.JWTService;
import com.mlorenzo.springboot.app.auth.service.JWTServiceImpl;

// Esta clase se corresponde con un fitro de seguridad basada en JWT(Jason Wb Token) por donde van a pasar todas las peticiones http de autorizacion(no de autenticación, que son tratadas por el filtro "JWTAuthenticationFilter") que realice el usuario autenticado con éxito
// Se va a verificar que el usuario que realica dichas peticiones contiene en la cabecera el token JWT(generado en el filtro 'JWTAuthenticationFilter') correcto.Si es correcto y tiene los permisos adecuados, se le da acceso al recurso solicitado de la aplicación.En caso contrario, no se le da acceso

public class JWTAuthorizationFilter extends BasicAuthenticationFilter{

	private JWTService jwtService; // Propiedad que se corresponde con el servicio con nuestra implementación de JWT(Jason Web Token)
	
	// Constructor
	public JWTAuthorizationFilter(AuthenticationManager authenticationManager,JWTService jwtService) {
		super(authenticationManager);
		this.jwtService = jwtService;
	}

	// Sobrescribimos este método de la clase, que heredamos "BasicAuthenticationFilter", para implementar nuestro tratamiento de las peticiones http de autorización(no de autenticación, que son tratadas por el filtro "JWTAuthenticationFilter")
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// Obtenemos de la cabecera de la petición http el campo indicado en la propiedad "HEADER_STRING"("Authorization")
		// Este campo debe contener el texto 'Bearer ' + token JWT pasado al usuario autenticado con éxito
		String header = request.getHeader(JWTServiceImpl.HEADER_STRING);
		
		// Comprobamos que la petición http no es de autenticación(este tipo de peticiones las trata el filtro "JWTAuthenticationFilter") analizando su cabecera
		// Es decir, este filtro solo trata las peticiones http de autorización(aquellas cuya cabecera tiene el campo "Authorization" comenzando con el texto "Bearer ") una vez que el usuario ya se ha autenticado
		if(!requiresAuthentication(header)){
			// Si la petcición http es de autenticación(su cabecera no contiene el campo "Authorization" comenzando con "Bearer ") y no de autorización(su cabecera sí contiene el campo "Authorization" comenzando con "Bearer "), se ejecuta el resto de filtros definidos y nos salimos de este filtro
			chain.doFilter(request,response);
			return; 
		}
		
		UsernamePasswordAuthenticationToken authentication = null;
		// Una vez que hemos comprobado que la petición http es de autorización, pasamos a validar el token JWT contenido en su cabecera mediante nuestro servicio "jwtService"
		if(jwtService.validate(header))
			// Si el token JWT es válido, creamos una instancia de tipo "UsernamePasswordAuthenticationToken", a partir del username y sus roles obtenidos mediante nuestro servicio "jwtService", necesaria para autenticarnos en el contexto de seguridad de Spring
			// Es decir, para cada acceso a los recursos de la aplicación, una vez validado el token JWT, hay que autenticarse de nuevo con el mismo token(no es el token JWT, es el token de Spring Security) con el que nos autenticamos en el proceso de login(método "attemptAuthentication()" del filtro "JWTAuthenticationFilter")
			authentication = new UsernamePasswordAuthenticationToken(jwtService.getUsername(header), null,jwtService.getRoles(header));
		
		// SecurityContextHolder es el manejador del contexto de seguridad de Spring
		// Accedemos a él y le pasamos nuestro objeto UsernamePasswordAuthenticationToken para que realice la autenticación para la petición http recibida
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// Continuamos con la ejecución del resto de filtros registrados en el contexto de seguridad
		chain.doFilter(request,response);
	}
	
	// Método que comprueba si la cabecera de la petición http es correcta, es decir, no es nula  y comienza con el texto indicado en la propiedad "TOKEN_PREFIX"("Bearer ")
	protected boolean requiresAuthentication(String header){
		// Si la cabecera de la petición no viene definida o no empieza por el texto contenido en la propiedad "TOKEN_PREFIX"("Bearer "), devolvemos false para indicar que no es correcta dicha cabecera
		if(header == null || !header.startsWith(JWTServiceImpl.TOKEN_PREFIX)){
			return false; // En caso sontrario, devolvemos false
		}
		// En caso contrario, la cabecera es correcta y devolvemos true
		return true;
	}
	

}
