package com.mlorenzo.springboot.horariointerceptor.app.interceptors;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component("horario")
public class HorarioInterceptor implements HandlerInterceptor {
	
	// Nota: Para poder inyectar valores de propiedades definidas en el archivo de configuración "application.properties" mediante la anotación @Value, es necesario anotar esta clase con @Componente para que sea un componente de Spring y la almacene como un bean en su contendor
	
	@Value("${config.horario.apertura}")
	private Integer apertura;
	
	@Value("${config.horario.cierre}")
	private Integer cierre;

	// Este método se ejecuta antes del método handler del controlador
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// Creamos una instancia de Calendar de Java para obtener la hora del día actual
		Calendar calendar = Calendar.getInstance(); // Esta instancia es un Singleton, es decir, una instancia para toda la aplicación, y se obtiene a través del método "getInstance"
		int hora = calendar.get(Calendar.HOUR_OF_DAY);
		
		if(hora >= apertura && hora < cierre) {
			// StringBuilder es una clase de Java para crear y manejar Strings, o cadenas de texto, que sean mutables, es decir, que podamos ir cambiando cada instancia de StringBuilder, añadiendo o quitanto cadenas de texto, sin tener que crear nuevos objetos para ello como sí ocurre con los Strings normales
			StringBuilder mensaje = new StringBuilder("Bienvenidos al horario de atención al cliente");
			mensaje.append(", atendemos desde las ");
			mensaje.append(apertura);
			mensaje.append("hrs. ");
			mensaje.append("hasta las ");
			mensaje.append(cierre);
			mensaje.append("hrs. ");
			mensaje.append("Gracias por su visita");
			
			// Creamos el atributo "mensaje" en la petición http a partir del mensaje contenido en el StringBuilder anterior
			// Para ello, obtenemos el String de ese StringBuilder mediante el método "toString"
			request.setAttribute("mensaje",mensaje.toString());
			
			return true;
		}
		
		// Enviamos una redirección a la ruta "/cerrado" para que se ejecute el método handler del controlador asociado a esa ruta
		response.sendRedirect(request.getContextPath().concat("/cerrado"));
		return false;
	}

	// Este método se ejecuta después del método handler del controlador pero antes de ir a la vista correspondiente
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		// Obtenemos el valor del atributo "mensaje" de la petición http
		// Para ello, como el método "getAttribute" devuelve un objeto genérico de tipo Object, tenemos que hacer un casting al tipo "String" porque el dato de ese atributo se corresponde con una cadena de texto
		String mensaje = (String)request.getAttribute("mensaje");
		
		if(modelAndView != null && handler instanceof HandlerMethod)
			// Creamos el atributo "horario" con el mensaje recuperado en el paso anterior y se lo pasamos a la vista(al html)
			modelAndView.addObject("horario",mensaje);
	}

}
