import { Component, OnInit } from '@angular/core';

/* Este componente ha sido creado usando el comando 'ng g c directiva' mediante Angular CLI.Con este comando, también se registra el componente automáticamente en el módulo principal de la aplicación 'app.module.ts' */

@Component({
  selector: 'app-directiva',
  templateUrl: './directiva.component.html'
})
export class DirectivaComponent{

  listaCursos: string[] = ["TypeScript","Java SE","C#","PHP"]; // Array de Strings
  habilitar: boolean = true; // Propiedad de tipo boolean para habilitar o no el listado de cursos mediante un botón disponible en la vista HTML.Por defecto se muestra.

  constructor() { }

  // Método que cambia el valor de la propiedad "habilitar" de esta clase de true a false y viceversa
  setHabilitar(): void{
    this.habilitar = !this.habilitar;
  }

}
