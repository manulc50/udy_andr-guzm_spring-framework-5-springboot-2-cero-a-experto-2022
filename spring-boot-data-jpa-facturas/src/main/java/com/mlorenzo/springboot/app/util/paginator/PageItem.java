package com.mlorenzo.springboot.app.util.paginator;

// Esta clase representa una casilla del paginador con el número de página
public class PageItem {

	private int numero; // Se trata del número de página
	private boolean actual; // Indica si es el número de página seleccionado actualmente
	
	public PageItem(int numero, boolean actual) {
		this.numero = numero;
		this.actual = actual;
	}
	
	public int getNumero() {
		return numero;
	}
	
	public boolean isActual() {
		return actual;
	}
		
}
