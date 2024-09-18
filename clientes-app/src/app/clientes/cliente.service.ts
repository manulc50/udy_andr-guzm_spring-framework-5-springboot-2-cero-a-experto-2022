import { Injectable } from '@angular/core';
import { Cliente } from './cliente'
import { CLIENTES } from './clientes.json' // importamos nuestra constante 'CLIENTES' con todos los datos de los clientes
import { of,Observable,throwError } from 'rxjs';
import { HttpClient,HttpHeaders } from '@angular/common/http'; // importamos el módulo HttpCliente para poder realizar peticiones http a un servidor y el módulo HttpHeaders para poder enviar una cabecera en la petición http
import { map,catchError,tap } from 'rxjs/operators';
import swal from 'sweetalert2'; // Para poder lanzar ventanas de alerta o aviso más vistosas y robustas con sweetalert2
import { Router } from '@angular/router'; // Módulo para establecer la navegabilidad entre componentes de manera programática
// import {formatDate,DatePipe} from '@angular/common' // Para dar formato a las fechas

/* Este servicio ha sido generado mediante el comando 'ng g service cliente' usando Angular-CLI.
   El patrón de diseño 'Observable' indica que hay uno o varios sujetos observables(Por ejemplo,' Observable<Cliente[]>' - Cliente sería un sujeto de Observable)
   que son escuchados o observados por los observadores(se suscriben a los observables) y que cuando sufren algún tipo de cambio(los observables),los observadores
   ejecutan una determinada tarea.
   Con este patrón, se trabaja de forma reactiva y asíncrona.*/

// Como este decorador o anotación tiene asociado el atributo "providedIn: 'root'", no hace falta registrarlo en el array 'providers' del módulo principal de la aplicación 'app.module.ts' 
// Este decorador de Angular clasifica esta clase como una clase de servicio y se puede inyectar en componentes mediante inyección de dependencias
@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  // Las propiedades que tengan la restricción 'private' solo tienen visibilidad en esta clase
  private urlEndPoint: string = "http://localhost:8090/api/clientes"; // url o ruta donde se encuentra nuestro servidor con la api rest de clientes 
  private httpHeaders: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'}) // creamos una cabecera a partir del módulo de Angular 'HttpHeaders' especificando el tipo de contenido que viaja en la petición http

  constructor(private http: HttpClient, // inyectamos como una dependencia el módulo HttpClient para poder usarlo a través de la propiedad 'http' de esta clase y así poder realizar peticiones http
              private router: Router) { // inyectamos como una dependencia el módulo Router para poder establecer la navegabilidad entre componentes de manera programática a partir de la propiedad de esta clase 'router'
  }

  // Metodo que devuelve los datos de los clientes en un flujo Observable
  getClientes(): Observable<Cliente[]>{
    // return of(CLIENTES); // Con el operador 'of' creamos nuestro flujo observable a partir de los datos de clientes almacenados en la constante 'CLIENTES'
    // Primera forma de realizar la petición http con casting a 'Cliente[]':
    // return this.http.get<Cliente[]>(this.urlEndPoint); // realizamos una petición http de tipo Get a nuestra api Rest de clientes para obtener todos sus datos.El metodo 'get' devuelve un Obervable(Esto es así porque el método handler  correspondiente a esta petición de nuestra api devuelve todos los datos de los clientes) de Any(cualquier tipo de dato).Por eso hay que realizar un casting a 'Cliente[]'.
    // Segunda forma de realizar la petición http usando el operador 'map' para pasar los datos de 'Observable<Any>' a 'Observable<Cliente[]>':
    
    // Realizamos una petición http de tipo Get a la ruta indicada en la propiedad "urlEndPoint"(url asociada al método handler de obtener todos los clientes de nuestro backend) de esta clase para obtener los datos de todos los cliente existentes
    // El metodo 'get' devuelve un Obervable con los datos devueltos por el backend
    // El método 'pipe' nos permite usar operadores para objetos Observables como 'map','tap' y 'catchError'
    return this.http.get(this.urlEndPoint).pipe(
      // NOTA: Es importante el orden de ejecución de los operadores como 'tap','map'
      // El operador 'tap' nos permite trabajar con los datos para realizar algún tipo de tarea sin devolver nada y sin modificar dichos datos
      // El operador 'map' nos permite transformar o mapear el tipo de datos de los datos a otro tipo devolviendo los datos finales
      // En este punto,al operador 'tap' le entra en 'resp' un Observable de tipo 'Any' y le sale un Observable de tipo 'Any',lo mismo ya que no manipulamos los datos,sólo los mostramos en la consola de log
      tap(resp =>{
        console.log("En el tap antes del map:");
        // En la variable "clientes" almacenamos un array de tipo "Cliente" con los datos de los clientes obtenidos del objeto "resp", que es de tipo "Any"
        let clientes = resp as Cliente[]; // Es lo mismo que 'let clientes = resp.content as Cliente[];'
        // Como ya tenemos una varible 'clientes' en formato 'Cliente[]',ya podemos iterarlo para mostrar el nombre de cada cliente del array en la consola de log
        clientes.forEach(cliente => console.log(cliente.nombre));
      }),
      // En este punto,al operador 'map' le entra en 'response' un Observable de tipo 'Any' y le sale un Observale de tipo 'Cliente[]' con los nombres de los clientes almacenados en mayúscula
      map((response: any) => {
        // En la variable "clientes" almacenamos un array de tipo "Cliente" con los datos de los clientes obtenidos del objeto "response", que es de tipo "Any"
        let clientes = response as Cliente[];
        // Con el operador 'map' transformamos el Observable<Cliente[]> "clientes" a otro Observable<Cliente[]> con los nombres de los clientes en mayúscula y sus fechas con formato
        return clientes.map(cliente => {
           cliente.nombre = cliente.nombre.toLocaleUpperCase();
           /* Primera forma usando la función "formatDate()"
           // Le pasamos la fecha a transformar,el formato deseado y el locale
           cliente.createAt = formatDate(cliente.createAt,"dd-MM-yyyy","en-US");
           */
           /* Segunda forma usando DatePipe(Lo suyo es usarlo directamente en las vistas pero también se puede usar de manera programática)
           // Se le pasa el locale al constructor
           let datePipe = new DatePipe("es");
           // Le pasamos la fecha a transformar y el formato deseado("Dia_semana Numero_dia, mes año")
           cliente.createAt = datePipe.transform(cliente.createAt,"EEEE dd, MMMM yyyy");
           */
           return cliente;
          });
      }),
      // En este punto,al operador 'tap' le entra un Observable de 'Cliente[]' y le sale lo mismo ya que no manipulamos los datos,sólo los mostramos en la consola de log
      tap(clientes =>{
        console.log("En el tap después del map:");
        // Como ya tenemos el objeto 'clientes' directamente en formato 'Cliente[]',lo iteramos para mostrar el nombre de cada cliente en la consola de log
        clientes.forEach(cliente => console.log(cliente.nombre));
      })
    );
  }

  // Metodo que devuelve los datos del cliente que ha sido creado en un flujo Observable
  // Devolvemos un Observable de tipo "Cliente", y no de tipo "Any", ya que los componentes que usan este método solo necesita los datos del cliente creado
  crear(cliente: Cliente): Observable<Cliente>{
    // Realizamos una petición http de tipo Post a la ruta indicada en la propiedad "urlEndPoint"(url asociada al método handler de crear de nuestro backend) de esta clase con los datos del cliente a crear y la cabecera necesaria para crear un nuevo cliente
    // El metodo 'post' devuelve un Obervable con los datos devueltos por el backend
    // El método 'pipe' nos permite usar operadores para objetos Observables como 'map','tap' y 'catchError'
    return this.http.post<Cliente>(this.urlEndPoint,cliente,{'headers': this.httpHeaders}).pipe(
      // Con el operador 'map' transformamos la respuesta de la petición,que es un Observable de tipo 'Any'(cualquiera), a un Observable de tipo 'Cliente'.Para ello, en este caso(así hemos devuelto esta estructura de la respuesta desde nuestro backend(Api Rest de clientes)), tenemos que acceder a la propiedad 'cliente' del objeto Observable de tipo 'Any' 'resp'
      map((resp :any) => resp.cliente as Cliente),
      // Con el operador 'catchError' podemos capturar los errores que nos devuelva la petición a la API Rest del Backend.La información de los errores capturados se almacenan en el objeto 'e'
      catchError(e => {
        // Verificamos si el estado del error es 400(BAD_REQUEST),y en caso afirmativo,lanzamos el error 'e' como un Observable
        // El estado de error 400(BAD_REQUEST) indica que nuestro backend ha devuelto un error de validación de los datos de creación de un cliente, que viajan en la petición http
        if(e.status == 400)
          // Con el método 'throwError()' lanzamos el error 'e' como un Observable
          return throwError(e);
        // El mensaje de error en el objeto 'e' se encuentra en la propiedad 'mensaje' porque así lo hemos implmentado en nuestra Api Rest de clientes
        // Mostramos por consolael mensaje de error
        console.log(e.error.mensaje);
        // Mostramos una ventana de error con sweetalert2 usando la propiedad "mensaje" de la propiedad "error" del objeto "e"
        // Recibe el título,el texto de la ventana y el tipo de ventana que deseamos mostrar.
        // En la propiedad "error" de la propiedad "error" del objeto "e" se encuentra el detalle del error
        swal(e.error.mensaje,e.error.error,'error');
        // Con el método 'throwError()' lanzamos el error 'e' como un Observable
        return throwError(e);
      })
    );
  }

  // Metodo que devuelve los datos del cliente que estamos solicitando a través de su id
  getCliente(id: number): Observable<Cliente>{
    // Realizamos una petición http de tipo Get a la ruta indicada en la propiedad "urlEndPoint" + "/" + el valor de "id"(url asociada al método handler de obtener un cliente de nuestro backend) de esta clase para obtener los datos de un cliente a partir de su id
    // El metodo 'get' devuelve un Obervable con los datos devueltos por el backend
    // El método 'pipe' nos permite usar operadores para objetos Observables como 'map','tap' y 'catchError'
    return this.http.get(`${this.urlEndPoint}/${id}`).pipe( 
      // Con el operador 'catchError' podemos capturar los errores que nos devuelva la petición a la API Rest del Backend.La información de los errores capturados se almacenan en el objeto 'e'
      catchError(e => {
        // El mensaje de error en el objeto 'e' se encuentra en la propiedad 'mensaje' porque así lo hemos implmentado en nuestra Api Rest de clientes
        // Mostramos por consolael mensaje de error
        console.log(e.error.mensaje);
        // Mostramos una ventana de error con sweetalert2 usando la propiedad "mensaje" de la propiedad "error" del objeto "e"
        // Recibe el título,el texto de la ventana y el tipo de ventana que deseamos mostrar.
        swal('Error al editar',e.error.mensaje,'error');
        // Navegamos hasta la ruta '/clientes'
        this.router.navigate(['/clientes']);
        // Con el método 'throwError()' lanzamos el error 'e' como un Observable
        return throwError(e);
      }),
      // Con el operador 'map' pasamos de 'Observable<Any>',que es la respuesta de la petición('response'), a 'Observable<Cliente>'
      map(response => response as Cliente)
    );
  }

  // Método que actualiza la información de un cliente.Devolvemos un Observable de un cliente con los datos actualizados
  // Devolvemos un Observable de tipo "Any", y no de tipo "Cliente", porque nuestro backend nos envía, además de los datos del cliente editado, más información que es usada para mostrarla en las vistas de los componentes que hacen uso de este servicio
  update(cliente: Cliente): Observable<any>{
    // Realizamos una petición http de tipo Put a la ruta indicada en la propiedad "urlEndPoint"(url asociada al método handler de editar de nuestro backend) de esta clase con los datos del cliente a modificar y la cabecera necesaria para actualizar un nuevo cliente
    // El metodo 'put' devuelve un Obervable con los datos devueltos por el backend
    // El método 'pipe' nos permite usar operadores para objetos Observables como 'map','tap' y 'catchError'
     return this.http.put<any>(`${this.urlEndPoint}/${cliente.id}`,cliente,{'headers': this.httpHeaders}).pipe(
      // Con el operador 'catchError' podemos capturar los errores que nos devuelva la petición a la API Rest del Backend.La información de los errores capturados se almacenan en el objeto 'e'
      catchError(e => {
        // El mensaje de error en el objeto 'e' se encuentra en la propiedad 'mensaje' porque así lo hemos implmentado en nuestra Api Rest de clientes
        // Mostramos por consolael mensaje de error
        console.log(e.error.mensaje);
        // Mostramos una ventana de error con sweetalert2 usando la propiedad "mensaje" de la propiedad "error" del objeto "e"
        // Recibe el título,el texto de la ventana y el tipo de ventana que deseamos mostrar.
        // En la propiedad "error" de la propiedad "error" del objeto "e" se encuentra el detalle del error
        swal(e.error.mensaje,e.error.error,'error');
        // Con el método 'throwError()' lanzamos el error 'e' como un Observable
        return throwError(e);
      })
    );
  }

  // Método que elimina un cliente de la aplicación mediante su id
  // Devolvemos un Observable de tipo Any(cualquiera),ya que queremos pasar al componente que usa esté método toda la respuesta recibida desde el backend(ahora mismo solo contiene un mensaje de éxito)
  delete(id: number): Observable<any>{
    // Realizamos una petición http de tipo Delete a la ruta indicada en la propiedad "urlEndPoint" + "/" + el valor de "id"(url asociada al método handler de eliminar de nuestro backend) de esta clase para eliminar un cliente a partir de su id
    // El metodo 'delete' devuelve un Obervable con los datos devueltos por el backend
    // El método 'pipe' nos permite usar operadores para objetos Observables como 'map','tap' y 'catchError'
    return this.http.delete<any>(`${this.urlEndPoint}/${id}`).pipe(
      // Con el operador 'catchError' podemos capturar los errores que nos devuelva la petición a la API Rest del Backend.La información de los errores capturados se almacenan en el objeto 'e'
      catchError(e => {
        // El mensaje de error en el objeto 'e' se encuentra en la propiedad 'mensaje' porque así lo hemos implmentado en nuestra Api Rest de clientes
        // Mostramos por consolael mensaje de error
        console.log(e.error.mensaje);
        // Mostramos una ventana de error con sweetalert2 usando la propiedad "mensaje" de la propiedad "error" del objeto "e"
        // Recibe el título,el texto de la ventana y el tipo de ventana que deseamos mostrar.
        // En la propiedad "error" de la propiedad "error" del objeto "e" se encuentra el detalle del error
        swal(e.error.mensaje,e.error.error,'error');
        // Con el método 'throwError()' lanzamos el error 'e' como un Observable
        return throwError(e);
      })
    );
  }
}
