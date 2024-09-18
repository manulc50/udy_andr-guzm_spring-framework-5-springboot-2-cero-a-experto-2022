package com.mlorenzo.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mlorenzo.springboot.app.auth.filter.JWTAuthenticationFilter;
import com.mlorenzo.springboot.app.auth.filter.JWTAuthorizationFilter;
import com.mlorenzo.springboot.app.auth.service.JWTService;

// Al heredar de WebSecurityConfigurerAdapter de Spring Security,esta clase funciona como un filtro o capa que esta por encima de nuestra aplicación.Si la autenticación del usuario es correcta,pasara a ejecutarse la lógica de nuestra aplicación invocándose los métodos del siguiente nivel más bajo que es la de los controladores

// Nota: Spring Security con Jason Web Token(JWT) utiliza sesiones sin estado(stateless).Sin embargo,Spring Security 'normal'(sin JWT) utiliza sesiones con estado

@EnableGlobalMethodSecurity(securedEnabled = true) // Esta anotación habilita el uso de la anotación de seguridad @Secured para ser usada a nivel de método handler en los controladores y configurar los accesos a los endpoints o rutas de nuestra API Rest
@Configuration // Indicamos que esta clase se trata de una clase de configuración de Spring para que lo almacene en su contenedor como un bean y lo gestione
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{
	
	/* Bean para realizar autenticacion por jdbc.
	@Autowired
	private DataSource dataSource; // Inyectamos el bean con toda la informacion necesaria para realizar la conexion con la base de datos
	*/
	
	// Recuperamos del contenedor de Spring el bean correspondiente que contiene la implementación de esta interfaz.En este caso,el bean que la implementa es de tipo JpaUserDetailsService.
	@Autowired
	private UserDetailsService userDetailsService; // Este bean implementa el proceso de login o autenticación de usuarios
	
	// Recuperamos del contenedor o memoria de Spring el bean que implmenta nuestra interfaz 'JWTService'.Esta interfaz es implementada por nuestra clase 'JWTServiceImpl'
	@Autowired
	private JWTService jwtService; // Este bean se corresponde con nuestro servicio que implementa y gestiona todo lo relacionado con JWT(Jason Web Token)
	
	// Método que devuelve una instancia de tipo 'BCryptPasswordEncoder', que implementa la interfaz 'PasswordEncoder', para codificar passwords o contraseñas usando el algoritmo "BCrypt" a la hora de guardarlas en la base de datos
	@Bean // Anotación para que Spring almacene en su memoria o contenedor la instancia creada de tipo 'BCryptPasswordEncoder' como un bean y así poder inyectarlo en otra parte del código para usarlo
	public PasswordEncoder passwordEncoder(){
		// Crea una instancia de tipo "BCryptPasswordEncoder" para codificar passwords o contraseñas usando el algoritomo "BCrypt"
		return new BCryptPasswordEncoder();
	}
	
	// Se sobrescribe este método de la clase de Spring WebSecurityConfigurerAdapter para configurar los permisos de accesos a las distintas rutas o recursos de la aplicación a los usuarios
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Es importante comenzar a configurar las rutas desde lo más especifico a lo más genérico
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/clientes","/api/clientes/page/**","/api/uploads/img/**").permitAll() // Se permite el acceso a las rutas '/api/clientes','/api/clientes/page/**' y '/api/uploads/img/**'('.../**' significa cualquier otra cosa que haya a continuación)a cualquier usuario para peticiones http de tipo Get.Si no se especifica un 'HttpMethod', por defecto se permite el acceso a la ruta para cualquier tipo de petición http(Get,Post,Put,...) válida para dicha ruta
		// Comentamos estas reglas ya que las vamos a configurar a nivel del controlador con la anotación @Secured.Es importante dejar aquí los permisos de acceso público para todos los usuarios y los que requieren la verificación de roles de usuarios configurarlos con @Secured
		//.antMatchers(HttpMethod.GET,"/api/clientes/{id}").hasAnyRole("USER","ADMIN") // Para peticiones http de tipo get a la ruta '/api/clientes/{id}', se permite el acceso a usuarios autenticados con roles "ROLE_USER" y "ROLE_ADMIN".Al método 'hasAnyRole()'(que es para especificar más de un role) no hace falta especificarle el prefijo 'ROLE_' ya que por debajo Spring concatena dicho prefijo con el role indicado en dicho método
		//.antMatchers(HttpMethod.POST,"/api/clientes/upload").hasAnyRole("USER","ADMIN") // Para peticiones http de tipo post a la ruta '/api/clientes/upload', se permite el acceso a usuarios autenticados con roles "ROLE_USER" y "ROLE_ADMIN".Al método 'hasAnyRole()'(que es para especificar más de un role) no hace falta especificarle el prefijo 'ROLE_' ya que por debajo Spring concatena dicho prefijo con el role indicado en dicho método
		//.antMatchers(HttpMethod.POST,"/api/clientes").hasRole("ADMIN") // Para peticiones http de tipo post a la ruta '/api/clientes', se permite el acceso a usuarios autenticados con role "ROLE_ADMIN".Al método 'hasRole()'(que es para indicar un único role) no hace falta especificarle el prefijo 'ROLE_' ya que por debajo Spring concatena dicho prefijo con el role indicado en dicho método
		//.antMatchers("/api/clientes/**").hasRole("ADMIN") // Para cualquier petición http(menos la de tipo get que ya se contempla en la segunda regla) para la ruta '/api/clientes/**'('/api/clientes/' + cualquier otra cosa),se permite el acceso a usuarios autenticado con role "ROLE_ADMIN".Al método 'hasRole()'(que es para indicar un único role) no hace falta especificarle el prefijo 'ROLE_' ya que por debajo Spring concatena dicho prefijo con el role indicado en dicho método.Aquí hacen match '/api/clientes/{id}' para Delete y '/api/clientes/{id} para Put
		.anyRequest().authenticated() // Cualquier otro acceso requiere autenticación. El método 'anyRequest()' siempre se pone al final de los permisos para rutas especificas,ya que es para tratar el caso genérico para el resto de rutas
		.and() // Con este método volvemos al objeto inicial HttpSecurity
		.addFilter(new JWTAuthenticationFilter(authenticationManager(),jwtService)) // Registramos nuestro filtro para seguridad JWT(parte de autenticación) pasándole el manejador de la autenticación y nuestro servicio con nuestra implementación de JWT(Jason Web Token).El método 'authenticationManager()' nos devuelve el manejador de la autenticación(se encarga de realizar la autenticación utilizando, por debajo, nuestro servicio "JpaUserDetailsService") y se encuentra implementado en la clase que heredamos 'WebSecurityConfigurerAdapter'
		.addFilter(new JWTAuthorizationFilter(authenticationManager(),jwtService)) // Registramos nuestro filtro para seguridad JWT(parte de autorización una vez que el usuario se ha autenticado) pasándole el manejador de la autenticación y nuestro servicio con nuestra implementación de JWT(Jason Web Token).El método 'authenticationManager()' nos devuelve el manejador de la autenticación(se encarga de realizar la autenticación utilizando, por debajo, nuestro servicio "JpaUserDetailsService") y se encuentra implementado en la clase que heredamos 'WebSecurityConfigurerAdapter'
		.csrf().disable() // Deshabilitamos la seguridad csrf usada en las vistas con formularios(Por defecto Spring Security la utiliza de manera automática cuando detecta formularios en las vistas) ya que se trata de una aplicación Api Rest(backend y frontendo por separado) y se va a usar seguridad basada en Jason Web Token(JWT).La seguridad csrf únicamente tiene sentido en aplicaciones MVC, que son aplicaciones con estado donde la parte backend y la frontend se encuentran en el mismo proyecto y dicho backend debe guardar el estado de la sesión cuando los usuarios hacen login
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Establecemos sesión sin estado ya que se trata de una aplicación Api Rest(backend y frontend por separado) y es un requisito para implementar seguridad con JWT.Spring Security utiliza por defecto sesión con estados debido a que va asociada a formularios login para mantener la sesión cuando un usuario se loguea.En resumen, en aplicaciones MVC(backend y frontend en la mismo proyecto) la sesión es con estado y se utiliza la seguridad csrf en las vistas con formularios,sin embargo,en aplicaciones Api Rest(backend y frontend separados en proyectos diferentes) la sesión es sin estado y se suele usar la seguirdad JWT
	}

	// Se sobrescribe este método de la clase de Spring WebSecurityConfigurerAdapter para registar nuestra implementación de la interfaz UserDetailsService en el manejador de autenticaciones de Spring,es decir,a través de nuestra implemetación,indicamos la manera de obtener los usuarios y sus roles para la autenticación
	@Autowired // Obtenemos de la memoria de Spring el bean 'build' de tipo AuthenticationManagerBuilder que se corresponde con el Authentication Manager o manejador de la autenticación
	public void configurerGlobal(AuthenticationManagerBuilder build) throws Exception{
		
		/* Código para realizar autenticación del usuario en memoria
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		// 'UserBuilder' nos permite crear usuarios en memoria
		UserBuilder users = User.builder().passwordEncoder(encoder::encode);
		// Indicamos que vamos a usar un método de autenticación en memoria
		build.inMemoryAuthentication()
		.withUser(users.username("admin").password("12345").roles("ADMIN","USER")) // Crea en memoria el usuario 'admin' con password '12345' y role 'ADMIN' y 'USER'
		.withUser(users.username("andres").password("12345").roles("USER")); // Crea en memoria el usuario 'andres' con password '12345' y role 'USER'
		*/
		
		/* Código para realizar autenticación del usuario usando jdbc
		// Usamos autenticación por jdbc
		build.jdbcAuthentication()
		.dataSource(dataSource) // Le pasamos el datasource con la información necesaria para realizar la conexión a la base de datos
		.passwordEncoder(passwordEncoder)  // Le pasamos el bean con el tipo de codificación de la password
		.usersByUsernameQuery("select username,password,enabled from users where username=?") // Indicamos la consulta SQL para recuperar la información del usuario que coincida con el nombre de usuario escrito en el formulario de login.Esta consulta es a nivel de tablas,no a nivel de clases de Java.
		.authoritiesByUsernameQuery("select u.username,a.authority from authorities a inner join users u on (a.user_id=u.id )where u.username=?"); // Indicamos la consulta SQL para recuperar los roles del usuario cuyo nombre coincida con el introducido en el formulario de login.Consulta a nivel de tablas.
		*/
		
		// Código para realizar autenticación del usuario usando Jpa
		// Registramos nuetra implementación de la interfaz UserDetailsService(bean de tipo JpaUserDetailsService) en el manejador de autenticaciones de Spring indicandole también el tipo de codificación de contraseñas a usar(BCryptPasswordEncoder)
		build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

}
