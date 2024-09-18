package com.mlorenzo.springboot.app.auth.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlorenzo.springboot.app.auth.SimpleGrantedAuthorityMixin;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component // Anotamos esta clase como un componente de Spring para que lo gestione y lo almacene en su contenedor como un bean y así lo podamos inyectar y usar en otra parte del proyecto
public class JWTServiceImpl implements JWTService{

	// NOTA: El atributo 'static' indica que es una propiedad de la clase,y no da cada objeto.Por lo tanto,como también es pública,desde otra clase se podría acceder usando JWTServiceImpl.SECRET.
	// NOTA: El atributo 'final' indica que el valor inicial de la propiedad no puede modificarse
	public static final String SECRET = Base64Utils.encodeToString("Alguna.Clave.Secreta.123456".getBytes()); // Cofificamos la clave secrea 'Alguna.Clave.Secreta.123456' usando Base64
	public static final long EXPIRATION_DATE = 14000000L;
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	
	// Método que crea un token JWT a partir de la información contenida en el parámetro de entrada "auth" de tipo "Authentication"
	// Este parámetro de entrada contiene información necesaria, como el username y los roles del usuario, para poder generar el token JWT
	@Override
	public String create(Authentication auth) throws IOException{

		// Obtenemos el username del usuario autenticado con éxito.Otra manera de obtener el username es ejecutando 'auth.getName()'
		String username = ((User)auth.getPrincipal()).getUsername();
		
		// Obtenemos los roles del usuario autenticado con éxito
		Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
		
		// Creamos los 'claims' donde vamos a poner los roles del usuario
		Claims claims = Jwts.claims();
		// Insertamos los roles del usuario en formato Json a través del objeto 'ObjectMapper'
		claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
		
		// Creamos un token JWT
		String token = Jwts.builder()
				.setClaims(claims) // Ponmemos en el token los 'claims' con los roles del usuario
				.setSubject(username) // Indicamos el nombre del usuario como sujeto del token
				.signWith(SignatureAlgorithm.HS512, SECRET.getBytes()) // Se firma el token usando la encriptación 'HS512'(hay muchas) con nuestra clave secreta indicada en la propiedad de esta clase "SECRET"("Alguna.Clave.Secreta.123456")
				.setIssuedAt(new Date()) // Indicamos la fecha de creación de este token
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DATE)) // Indicamos la fecha de expiración del token contenida en la propiedad de esta clase "EXPIRATION_DATE"(4h desde la fecha de creación en milisegundos - 14000000)
				.compact(); // Compacta y crea el token JWT teniendo en cuentas las configuraciones anteriores
		
		// Devolvemos el token JWT generado
		return token;
	}

	// Método que valida el token JWT que se le pasa como parámetro de entrada
	@Override
	public boolean validate(String token) {
		
		// Analizamos el token JWT de las peticiones http recibidas invocando al método 'getClaims'.
		// Si no se ha ido por el 'catch', damos el token JWT como válido
		try{
			getClaims(token);
			return true;
		}
		// Aquí se pueden producir varios tipos de excepciones donde todas ellas tienen como excepción padre a JwtException o IllegalArgumentException
		// Si se produce alguna excepción durante el análisis del token JWT,lo damos como token JWT inválido
		catch(JwtException | IllegalArgumentException e){
			return false;
		}
		
	}

	// Método que obtiene los "claims" del token JWT que se pasa como parámetro de entrada
	@Override
	public Claims getClaims(String token) {
		// Analizamos el token JWT que nos llega en la cabecera de la petición http y obtenemos los claims
		Claims claims = Jwts.parser() 
				.setSigningKey(SECRET.getBytes()) // Lo hacemos a partir de la clave secreta que hemos usado para generarlo cuando se lo hemos pasado al usuario autenticado con éxito.Dicha clave secreta se encuentra en la propiedad de esta clase "SECRET"("Alguna.Clave.Secreta.123456")
				.parseClaimsJws(resolve(token)).getBody(); // Aquí le tenemos que pasar únicamente el código del token JWT sin el texto "Bearer ".Para ello, mediante el método "resolve()" de esta clase, eliminamos del token JWT dicho texto
		
		// Devolvemos los claims del token JWT
		return claims;
	}

	// Método que obtiene el username del token JWT que se pasa como parámetro de entrada
	@Override
	public String getUsername(String token) {
		// Usando el método "getClaims()" de esta clase obtenemos los claims del token JWT y devolvemos el sujeto de este token que va a ser el username
		return getClaims(token).getSubject();
	}

	// Método que obtiene los roles del usuario del token JWT que se pasa como parámetro de entrada
	@Override
	public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException {
		// Mediante el método "getClaims()" de esta clase, obtenemos los claims del token JWT como un objeto Json y seguidamente obtenemos los roles del usuario accediendo a la propiedad "authorities" de dicho objeto Json(Cuando creamos el token JWT,insertamos los roles en la propiedad 'authorities' del Json que representa los claims)
		Object roles = getClaims(token).get("authorities");
		
		// Necesitamos convertir los roles en formato Json a un objeto de tipo 'Collection<? extends GrantedAuthority>' que es uno de los campos que necesita el constructor 'UsernamePasswordAuthenticationToken'.Para ello usamos un objeto ObjectMapper() que realiza el mapeo de un formato a otro
		Collection<? extends GrantedAuthority> authorities = Arrays.asList(new ObjectMapper()
				.addMixIn(SimpleGrantedAuthority.class,SimpleGrantedAuthorityMixin.class) // La clase 'ObjectMapper' nos permite asignar una clase Mixin para realizar un mapeo.En este caso,se quiere aplicar la clase 'SimpleGrantedAuthoritiesMixin' sobre la clase 'SimpleGrantedAuthority' para mapear la información de cada role en formato Json a un objeto de la clase 'SimpleGrantedAuthority'
				.readValue(roles.toString().getBytes(),SimpleGrantedAuthority[].class)); // Indicamos SimpleGrantedAuthority ya que es una clase que implementa la interfaz GrantedAuthority.En un principio,se produce un error en la ejecución de la aplicación cuando se inenta mapear, por el objeto 'ObjectMapper', la información de los roles en formato Json a un objeto de la clase 'SimpleGrantedAuthority' ya que la clase 'SimpleGrantedAuthority' no posee un construcor vacío y sólo tiene uno en el que se le pasa un String correspondiente al role,que no se lo estamos pasando.Para solucionar esto,hacemos lo indicado en la línea anterior
		
		// Devolvemos los roles como objetos de tipo Collection<? extends GrantedAuthority>
		return authorities;
	}

	// Método que elimina el texto "Bearer " del token JWT que se le pasa como parámetro de entrada
	@Override
	public String resolve(String token) {
		// Si el token no es nulo y comienza con el texto indicado en la propiedad "TOKEN_PREFIX"("Bearer ") de esta clase, devolvemos únicamente el código del token JWT
		if(token != null && token.startsWith(TOKEN_PREFIX))
			return token.replace(TOKEN_PREFIX,""); 
		// En caso contrario, devolvemos null
		return null;
	}

}
