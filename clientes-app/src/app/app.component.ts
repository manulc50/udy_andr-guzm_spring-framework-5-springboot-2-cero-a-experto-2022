import { Component } from '@angular/core';

// El componente con nombre 'app-root' es el componente principal de la aplicación
@Component({
  selector: 'app-root', // Es el nombre que va a tener el componente como elemento HTML para hacerle referencia
  templateUrl: './app.component.html', // Todo componente tiene asociado una vista o documento HTML
  styleUrls: ['./app.component.css'] // Todo componente puede tener opcionalmente asociado un archivo de estilos CSS
})

// En esta clase se definen propiedades,variables y metodos que se van a usar en la vista HTML asociada a este componente.Sería como un controlador en Java.Es necesario que la clase se exporte con 'export' para que sea visible desde la vista
export class AppComponent {
  title = 'Bienvenido a Angular';
  curso: string = 'Angular con Spring 5'; // El tipo de la propiedad('string') de la case es opcional
  profesor: string = 'Andrés Guzmán';
}
