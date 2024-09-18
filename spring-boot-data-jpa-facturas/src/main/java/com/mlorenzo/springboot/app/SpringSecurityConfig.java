package com.mlorenzo.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

// Al heredar de WebSecurityConfigurerAdapter de Spring Security,esta clase funciona como un filtro o capa que esta por encima de nuestra aplicación.Si la autenticación del usuario es correcta,pasara a ejecutarse la lógica de nuestra aplicación invocándose los métodos del siguiente nivel más bajo que es la de los controladores

// Esta anotación habilita las anotaciones de seguridad @Secured y @PreAuthorize para ser usadas a nivel de método handler en los controladores
@EnableGlobalMethodSecurity(securedEnabled=true,prePostEnabled=true)
@Configuration // Indicamos que esta clase se trata de una clase de configuración de Spring para que lo almacene en su contenedor como un bean y lo gestione
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{
	
	// Recuperamos del contenedor de Spring el bean correspondiente que contiene la implementación de la interfaz AuthenticationSuccessHandler.En este caso,el bean que la implementa es de tipo LoginSuccessHandler.
	@Autowired
	private AuthenticationSuccessHandler loginSuccessHandler; // Este bean se corresponde con nuestro lanzador para autenticaciones con éxito que muestra un mensaje de confirmación al usuario
	
	/* Bean para realizar autenticación por jdbc.
	@Autowired
	private DataSource dataSource; // Inyectamos el bean con toda la información necesaria para realizar la conexión con la base de datos
	*/
	
	// Recuperamos del contenedor de Spring el bean correspondiente que contiene la implementación de la interfaz PasswordEncoder.En este caso,el bean que la implementa es de tipo BCryptPasswordEncoder.
	@Autowired
	private PasswordEncoder passwordEncoder; // Este bean se corresponde con el codificacdor de contraseñas
	
	// Recuperamos del contenedor de Spring el bean correspondiente que contiene la implementación de esta interfaz.En este caso,el bean que la implementa es de tipo JpaUserDetailsService.
	@Autowired
	private UserDetailsService userDetailsService; // Este bean implementa el proceso de login o autenticación de usuarios
	
	// Se sobrescribe este método de la clase de Spring WebSecurityConfigurerAdapter para configurar los permisos de accesos a las distintas rutas o recursos de la aplicación a los usuarios
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Es importante comenzar a configurar las rutas desde lo más especifico a lo más genérico
		http.authorizeRequests().antMatchers("/","/css/**","/js/**","/images/**","/listar**","/api/clientes/**","/locale").permitAll() // Autoriza a todo el mundo(acceso publico) los accesos a las rutas "/","/css/**","/js/**","/images/**","/listar**("/listar" y "/listar-rest"),"/api/clientes/**" y "/locale".Con la expersion regular /** se incluye todo lo que haya a continuación 
		// Las autorizaciones comentadas abajo se pueden sustituir por anotaciones @Secure y @PreAuthorize en los controladores
		/*.antMatchers("/ver/**").hasAnyRole("USER") // Sólo los usuarion con role 'USER' pueden acceder a la ruta '/ver/**'
		.antMatchers("/uploads/**").hasAnyRole("USER") // Sólo los usuarion con role 'USER' pueden acceder a la ruta '/uploads/**'
		.antMatchers("/form/**").hasAnyRole("ADMIN") // Sólo los usuarios con role "ADMIN" pueden acceder a la ruta '/form/**'(formulario para crear y editar usuarios)
		.antMatchers("/eliminar/**").hasAnyRole("ADMIN") // sólo los usuarios con role "ADMIN" pueden acceder a la ruta '/eliminar/**'(para eliminar usuarios)
		.antMatchers("/factura/**").hasAnyRole("ADMIN")*/ // Sólo los usuarios con role "ADMIN" pueden acceder a la ruta '/factura/**'(para crear facturas)
		.anyRequest().authenticated() // Cualquier otro acceso requiere autenticación. El método 'anyRequest()' siempre se pone al final de los permisos para rutas especificas,ya que es para tratar el caso genérico para el resto de rutas
		.and() // Con este método volvemos al objeto inicial HttpSecurity
		.formLogin() // Indicamos que la autenticación de usuarios se va a realizar a través de un formulario de login
			.successHandler(loginSuccessHandler) // Cuando el usuario ha iniciado correctamente la sesión,lanzamos el handler 'loginSuccessHandler' que va a mostrar un mensaje de confirmación de login al usuario
			.loginPage("/login").permitAll() // Indicamos que todo el mundo tiene acceso al formulario de login.Por defecto,si no se especifica ninguna vista nuestra personalizada de login,se usa la que te proporciona Spring. Con 'loginPage("/login")' habilitamos nuestra vista personalizada de login que se encuentra en la ruta '/login'
		.and() // Con este método volvemos al objeto inicial HttpSecurity
		.logout().permitAll() // Indicamos que todo el mundo tiene acceso para hacer logout
		.and() // Con este método volvemos al objeto inicial HttpSecurity
		.exceptionHandling().accessDeniedPage("/error_403")  // Cuando se produce una excepción de acceso denegado al usuario,le indicamos que se ejecute el controlador parametrizable registrado para la ruta '/error_403'
		.and() // Con este método volvemos al objeto inicial HttpSecurity
		.csrf().csrfTokenRepository(new HttpSessionCsrfTokenRepository()); // Para solucionar la excepción "Cannot create a session after the response has been committed" que a veces salía al hacer login o logout
	}

	// Se sobrescribe este método de la clase de Spring WebSecurityConfigurerAdapter para registar nuestra implementación de la interfaz UserDetailsService en el manejador de autenticaciones de Spring,es decir,a través de nuestra implemetación,indicamos la manera de obtener los usuarios y sus roles para la autenticación
	@Autowired // Obtenemos de la memoria de Spring el bean 'build' de tipo 'AuthenticationManagerBuilder' que se corresponde con el Authentication Manager o manejador de la autenticación
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
		build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

}
