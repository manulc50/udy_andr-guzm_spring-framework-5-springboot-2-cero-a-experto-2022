import { Component } from "@angular/core";

/* Este archivo ha sido generado mediante el comando 'ng generate class footer.componente' del Angular CLI */

// Para que esta clase de Angular sea un componente,tiene que se decorada con esta anotación
@Component({
    selector: "app-footer",  // Etiqueta que donde se use se va a renderizar lo que haya en 'template' o 'templateUrl'
    templateUrl: "./footer.component.html", // La vista HTML puede ir integrada aquí mediante la propiedad 'template' o puede ir en otro documento externo mediante la propiedad 'templateUrl'.Cuando la vista HTML es pequeño(de 3 a 5 líneas) se puede integrar directamente aquí,sin embargo,si es tiene más líneas,es mejor usar un documento HTML externo.
    styleUrls: ["./footer.component.css"] // A un componente se le puede aplicar varias hojas de estilo.Por eso 'styleUrls' puede recibir un array
})

// Es importante que tenga la palabra reservada 'export' para poderlo importar en otros lugares de la aplicación para usarlo
export class FooterComponent {
    autor: any={nombre: "Andrés", apellido: "Guzmán"} // El tipo 'any' es genérico
}
