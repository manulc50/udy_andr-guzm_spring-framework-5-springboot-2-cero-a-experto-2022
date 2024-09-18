package com.mlorenzo.springboot.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
//Esta anotación nos permite usar varios archivos properties de configuración de la aplicación
@PropertySources({
	@PropertySource("classpath:textos.properties") // Con esta anotación añadimos el archivo de configuración "textos.properties" para su uso
})
public class TextosPropertiesConfig {

}
