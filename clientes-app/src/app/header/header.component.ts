import { Component } from "@angular/core";

/* Este componente se ha creado manualmente sn usar Angular-CLI.Es por ello que es necesario registrar este componente en el modulo principal de la aplicación 'app.module.ts' */

// Para que esta clase de Angular sea un componente,tiene que se decorada con esta anotación
@Component({
    selector: "app-header", // Etiqueta que donde se use se va a renderizar lo que haya en 'template' o 'templateUrl'
    templateUrl: 'header.component.html' // La vista HTML puede ir integrada aquí mediante la propiedad 'template' o puede ir en otro documento externo mediante la propiedad 'templateUrl'.Cuando la vista HTML es pequeño(de 3 a 5 líneas) se puede integrar directamente aquí,sin embargo,si es tiene más líneas,es mejor usar un documento HTML externo.
}) 

// Es importante que tenga la palabra reservada 'export' para poderlo importar en otros lugares de la aplicación para usarlo
export class HeaderComponent{
    title: string = 'App Angular'
}