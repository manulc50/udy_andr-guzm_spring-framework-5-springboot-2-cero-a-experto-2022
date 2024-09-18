import { BrowserModule } from '@angular/platform-browser';
import { NgModule,LOCALE_ID } from '@angular/core';
import { RouterModule,Routes } from '@angular/router'; // Para usar rutas y navegabilidad en nuestra aplicación
import { HttpClientModule } from '@angular/common/http'; // Para poder realizar peticiones http(Get,Post,Put,Delete) a un servidor para,por ejemplo,obtener datos
import { FormsModule } from '@angular/forms'; // Importamos el módulo de Angular para usar formularios
import { registerLocaleData } from '@angular/common'; // Para registar locales y así poder cambiar el idioma de la aplicación(Por defecto Angular usa el locale "en-US" - Inglés de América)

// Componentes
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { DirectivaComponent } from './directiva/directiva.component';
import { ClientesComponent } from './clientes/clientes.component';
import { FormComponent } from './clientes/form.component'

// Servicios
import { ClienteService } from './clientes/cliente.service';

// Locales
import localeES from '@angular/common/locales/es';

// Registramos de manera global el locale para español
registerLocaleData(localeES,'es');

// Constante con un array de nuestras rutas
const ROUTES: Routes=[
  {path: '', redirectTo: '/clientes', pathMatch: 'full'}, // La ruta principal,es decir, la que no tiene nada o ruta raíz,se redirige a '/clientes'
  {path: 'directivas', component: DirectivaComponent}, // El componente 'DirectivaComponent' esta asociado a la ruta 'directivas'
  {path: 'clientes', component: ClientesComponent}, // El componente 'ClientesComponent' esta asociado a la ruta 'clientes'
  {path: 'clientes/form', component: FormComponent}, // El componente 'FormComponent' esta asociado a la ruta 'clientes/form'
  {path: 'clientes/form/:id', component: FormComponent} // El componente 'FormComponent' esta asociado a la ruta 'clientes/form/:id'.':id' es una variable que representa el id del cliente
];

@NgModule({
  // En este array se registran los componentes de la aplicación
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    DirectivaComponent,
    ClientesComponent,
    FormComponent
  ],
  // En este array se registran los módulos de la aplicacion
  imports: [
    BrowserModule,
    FormsModule, // Registramos el módulo de Angular para usar formularios
    HttpClientModule, // Registramos el módulo para poder realizar peticiones http
    RouterModule.forRoot(ROUTES) // Registramos nuestras rutas definidas en la constante 'ROUTES'
  ],
  // En este array se registran los servicios de la aplicacion
  // NOTA: Si nuetros servicio tienen el atributo "providedIn: 'root'" establecido para el decorador @Injectable,no hace falta registrarlos en el array 'providers'.Es un tema que salió en las últimas versiones de Angular
  providers: [
    { provide: LOCALE_ID, useValue: 'es' }, // Para cambiar la localización por defecto de la aplicación,y por lo tanto,el idioma por defecto("en-US"(por defecto) -> Inglés américano a "es" -> español)
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
