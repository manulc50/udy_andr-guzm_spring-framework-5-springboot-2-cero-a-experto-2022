package com.mlorenzo.springboot.app.auth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlorenzo.springboot.app.auth.service.JWTService;
import com.mlorenzo.springboot.app.auth.service.JWTServiceImpl;
import com.mlorenzo.springboot.app.models.entity.Usuario;

// Esta clase es el filtro de seguridad basada en JWT(Jason Web Token) que va a generar un token JWT y va a dárselo al usuario que se autentique con éxito en la aplicación mediante el tratamiento de peticiones http de autenticación(no de autorización, que son tratadas por el filtro "JWTAuthorizationFilter")
// Tal y como está configurado, para realizar la autenticación, se debe realizar una petición http Post a la url '.../api/login' enviando un objeto Json con las propiedades 'username' y 'password' o un form-data con los campos 'username' y 'password'

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private AuthenticationManager authenticationManager; // Esta propiedad es la que se encarga de realizar la autenticación utilizando, por debajo, nuestro servicio "JpaUserDetailsService"(usa este servicio porque así lo hemos indicado en la clase de configuración "SpringSecurityConfig")
	private JWTService jwtService; // Propiedad que se corresponde con el servicio con nuestra implementación de JWT(Jason Web Token)
	
	// Constructor
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager,JWTService jwtService) {
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
		// Establecemos la ruta('/api/login') y el método('POST') de la petición http que va a escuchar este filtro.Para otras rutas o métodos http,este filtro no va a hacer nada.
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login","POST")); // Si no se establece esta ruta y este tipo de petición,por defecto, la ruta es '.../login' y el tipo de petición 'Post'
	}
	
	// Primer paso - Sobrescribimos este método de 'UsernamePasswordAuthenticationFilter', que se encarga de la autenticación del usuario, para darle el comportamtiento deseado
	// Tal y como está configurado, la autenticación se realiza enviando una petición http Post a la url '.../api/login' con un objeto Json con las propiedades 'username' y 'password' o con un form-data con los campos 'username' y 'password'
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		// Obtenemos el usarname y el password de la petición http cuando se pasan usando 'form-data'.Los métodos 'obtainUsername' y 'obtainPassword' se encuentran implementados en la clase que heredamos 'UsernamePasswordAuthenticationFilter' 
		String username = obtainUsername(request); // Otra manera es poner directamente 'request.getparameter("username")'. El método 'obtainUsername(request)' ejecuta esta senctencia por debajo
		String password = obtainPassword(request); // Otra manera es poner directamente 'request.getparameter("password")'. El método 'obtainPassword(request)' ejecuta esta senctencia por debajo
		
		// Si el username y su password existen,escribimos en el log sus valores.El log se encuentra definido en la clase que heredamos 'UsernamePasswordAuthenticationFilter'
		if(username != null && password != null){
			logger.info("Username desde el request parameter(form-data): " + username);
			logger.info("Password desde el request parameter(form-data): " + password);
		}
		// En este caso,cuando el username y/o el password son nulos,significa que no han sido enviados usando 'form-data',sino usando 'raw' o en bruto
		else{
			
			Usuario user = null;
			// Para recuperar el username y el password en este caso,usamos el método 'getInputStream()' de la petición http y nos devolverá información en formato Json que la tenemos que transformar en un objeto Usuario.Para ello usamos el objeto 'ObjectMapper'
			try {
				user = new ObjectMapper().readValue(request.getInputStream(),Usuario.class);
				username = user.getUsername();
				password = user.getPassword();
				
				logger.info("Username desde el request InputStream(raw): " + username);
				logger.info("Password desde el request InputStream(raw): " + password);
				
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Eliminamos, por si acaso, los espacios que puediera haber en el usernaname
		username = username.trim();
		
		// Creamos el token de autenticacion(No es el token JWT) a partir del username y su password.
		// Insisto,no se trata del token JWT, sino de otro token que maneja internamente Spring Security
		// A partir de este token, vamos a obtener los datos necesarios, como el username, para generar el token JWT
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,password);
		
		// A través del Authentication Manager devolvemos la autenticación teniendo en cuenta el token generado anteriormente
		return authenticationManager.authenticate(authToken);
	}

	// Segundo paso - Sobrescribimos este método de 'UsernamePasswordAuthenticationFilter', que se ejecuta cuando el usuario se ha autenticado con éxito, para darle el comportamtiento deseado.En este método vamos a generar el token JWT
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException { // El objeto 'authResult' contiene los datos del usuario que se ha autenticado con éxito
		
		// Creamos y obtenemos el token JWT a través de nuestro servicio jwtService
		String token = jwtService.create(authResult);
		
		// Ponemos el token JWT a continuación del texto contenido en la propiedad "HEADER_STRING"("Bearer ") en el campo indicado en la propiedad "HEADER_STRING"("Authorization") de la cabecera de la respuesta de esta petición http
		response.addHeader(JWTServiceImpl.HEADER_STRING,JWTServiceImpl.TOKEN_PREFIX + token);
		
		// En un HashMap ponemos toda la información que queremos pasarle al usuario autenticado con éxito junto con el token JWT generado
		Map<String,Object> body = new HashMap<String,Object>();
		// Registramos en el map el token JWT generado
		body.put("token",token);
		// Registramos en el map el nombre del usuario
		body.put("user", (User)authResult.getPrincipal());
		// Registramos en el map nuestro mensaje de éxito personalizado
		body.put("mensaje",String.format("Hola %s,has iniciado sesión con éxito!",authResult.getName())); // Otra forma de obtener el username es; '((User)auth.getPrincipal()).getUsername();'
		
		// En la respuesta de la petición http ponemos la información del HashMap anterior en formato Json.Para ello,usamos un objeto ObjectMapper
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		// Indicamos el resultado de la petición http con el estado 200(OK)
		response.setStatus(200);
		// Indicamos que la respuesta va a ser en formato Json
		response.setContentType("application/json");

	}

	// Segundo paso - Sobrescribimos este método de 'UsernamePasswordAuthenticationFilter', que se ejecuta cuando el usuario no se ha autenticado con éxito, para darle el comportamtiento deseado.
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		
		// En un HashMap ponemos toda la información que queremos pasarle al usuario que no se ha autenticado con éxito
		Map<String,Object> body = new HashMap<String,Object>();
		// Registramos en el map nuestro mensaje de error personalizado
		body.put("mensaje","Error de autenticación: username y/o password incorrectos!");
		// Registramos en el map el mensaje interno de Spring que se genera cuando un usuario se autentica incorrectamente
		body.put("error",failed.getMessage());
	
		// En la respuesta de la petición http ponemos la información del HashMap anterior en formato Json.Para ello,usamos un objeto ObjectMapper
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		// Indicamos el resultado de la petición http con el estado 401(Usuario no autorizado)
		response.setStatus(401);
		// Indicamos que la respuesta va a ser en formato Json
		response.setContentType("application/json");
	}
	
	
	
	
	
	

}
