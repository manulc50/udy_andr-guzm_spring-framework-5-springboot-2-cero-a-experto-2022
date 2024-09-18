package com.mlorenzo.springboot.app.util.paginator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

/* Esta clase se encarga de calcular el paginador de manera dinámica,es decir, en lugar de poner todas las páginas del paginador,que puede ser muy grando en caso de haber muchos registros en la base de datos,
   acotamos dichas páginas dinámicamente calculando un rango de páginas según el número de página que se haya seleccionado por última vez */

// Hacemos una clase generica en función del parámetro T ya que el paginador podría ser para una lista de la entidad Cliente,o de Factura,o de Producto,etc...
public class PageRender<T> {
	
	private String url;
	
	// Esta propiedad tiene la información del paginador,como por ejemplo,el número total de páginas,el número de elementos por página,el número de página seleccionado actualmente,etc...
	private Page<T> page;
	
	private int totalPaginas;
	
	private int numElementosPorPagina;
	
	private int paginaActual;
	
	// Esta lista representa el paginador que se va a crear de manera dinámica cada vez que cambien los indices 'desde' y 'hasta'
	// Una instancia de PageItem representa una casilla del paginador con un número de página
	private List<PageItem> paginas;
	
	public PageRender(String url, Page<T> page) {
		this.url = url;
		this.page = page;
		this.paginas = new ArrayList<PageItem>();
		
		// La instancia page de tipo Page es la que contiene toda la informción del paginador,como por ejemplo,el número total de páginas,el número de elementos por página,el número de página seleccionado actualmente,etc...
		numElementosPorPagina = page.getSize();
		totalPaginas = page.getTotalPages();
		// El indice del número de página empieza en 0 pero queremos mostrar en la vista dicho indice como si empezara en 1
		paginaActual = page.getNumber() + 1;
		
		// Indices del rango del paginador
		int desde, hasta;
		// Este caso no tiene  una gran cantidad de páginas y podemos mostrar todas ellas directamente en la vista.Por eso,establecemos el indice 'desde' en 1 y 'hasta' en la última página
		if(totalPaginas <= numElementosPorPagina) {
			desde = 1;
			hasta = totalPaginas;
		// En caso contrario,se pueden dar varias opciones en función del número de página actual(la más reciente en seleccionarse),es decir,vamos a calcular los indices 'desde' y 'hasta' en función de dónde se localice la página actual seleccionada
		} else {
			// Caso para el rango inicial del paginador(la página actual está en el rango inicial del paginador)
			if(paginaActual <= numElementosPorPagina/2) {
				desde = 1;
				hasta = numElementosPorPagina;
			// Caso para el rango final del paginador(la página actual está en el rango final del paginador)
			} else if(paginaActual >= totalPaginas - numElementosPorPagina/2 ) {
				desde = totalPaginas - numElementosPorPagina + 1;
				hasta = numElementosPorPagina;
			// Caso para el rango medio del paginador(la página actual está en el rango medio del paginador)
			} else {
				desde = paginaActual -numElementosPorPagina/2;
				hasta = numElementosPorPagina;
			}
		}
		
		// Por cada nuevo rango del paginador dado por los indices 'desde' y 'hasta',construimos el paginador
		for(int i=0; i < hasta; i++) {
			// Vamos creando casillas del paginador desde el indice 'desde' hasta el indice 'hasta', y aquella que coincida con el número de página seleccionado actualmente,se establece a true
			paginas.add(new PageItem(desde + i, paginaActual == desde+i));
		}

	}

	public String getUrl() {
		return url;
	}

	public int getTotalPaginas() {
		return totalPaginas;
	}

	public int getPaginaActual() {
		return paginaActual;
	}

	public List<PageItem> getPaginas() {
		return paginas;
	}
	
	// Método para saber si es la primera página
	public boolean isFirst() {
		return page.isFirst();
	}
	
	// Método para saber si es la última página
	public boolean isLast() {
		return page.isLast();
	}
	
	// Método para saber si tiene siguiente página
	public boolean isHasNext() {
		return page.hasNext();
	}
	
	// Método para saber si tiene página anterior
	public boolean isHasPrevious() {
		return page.hasPrevious();
	}

}
