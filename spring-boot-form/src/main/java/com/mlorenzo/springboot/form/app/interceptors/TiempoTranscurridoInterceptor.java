package com.mlorenzo.springboot.form.app.interceptors;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class TiempoTranscurridoInterceptor implements HandlerInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(TiempoTranscurridoInterceptor.class);

	// Si este método devuelve true, pasa el control a la ejecución del método handler correspondiente del controlador
	// En caso contrario, no pasa el control o no permite la ejecución del método handler correspondiente del controlador
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// Para las peticiones http de tipo Post, no queremos calcular el tiempo transcurrido de esas peticiones
		// Entonces, deolvemos directamente true para pasar el control a la ejecución de sus métodos handler correspondientes en el controlador
		if(request.getMethod().equalsIgnoreCase("post"))
			return true;
		
		if(handler instanceof HandlerMethod) {
			HandlerMethod metodo = (HandlerMethod)handler;
			logger.info("Es un método del controlador: ".concat(metodo.getMethod().getName()));
		}
		
		logger.info("TiempoTranscurridoInterceptor: preHandle() entrando...");
		logger.info("Interceptando: ".concat(handler.toString()));
		
		long tiempoInicio = System.currentTimeMillis();
		request.setAttribute("tiempoInicio", tiempoInicio);
		
		// Establecemos un tiempo de demora adicional aleatorio entre 0 y 100ms
		Random random = new Random();
		int demora = random.nextInt(100);
		Thread.sleep(demora);
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		// Para las peticiones http de tipo Post, no queremos calcular el tiempo transcurrido de esas peticiones
		// Entonces, nos salimos directamente de este método
		if(request.getMethod().equalsIgnoreCase("post"))
			return;
		
		long tiempoFinal = System.currentTimeMillis();
		long tiempoInicio = (Long)request.getAttribute("tiempoInicio");
		long tiempoTranscurrido = tiempoFinal - tiempoInicio;
		
		// Comprobación para las peticiones http de obtención de recursos estáticos, es decir, estas peticiones http no tienen asociadas un objeto de tipo ModelAndView 
		// Si la petición http está asociada a un método handler del controlador, entonces si existe un objeto de tipo ModelAndView
		if(handler instanceof HandlerMethod && modelAndView != null)
			modelAndView.addObject("tiempoTranscurrido", tiempoTranscurrido);
		
		logger.info(String.format("Tiempo transcurrido: %d milisegundos", tiempoTranscurrido));
		logger.info("TiempoTranscurridoInterceptor: postHandle() saliendo...");
	}
	
	

}
