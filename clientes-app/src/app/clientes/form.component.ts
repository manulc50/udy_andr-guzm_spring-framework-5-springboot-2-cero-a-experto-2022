import { Component, OnInit } from '@angular/core';
import { Cliente } from './cliente';
import { ClienteService } from './cliente.service';
import { Router, ActivatedRoute } from '@angular/router';
import swal from 'sweetalert2'; // Para poder lanzar ventanas de alerta o aviso más vistosas y robustas con sweetalert2

/* Este componente ha sido generado mediante el comando 'ng g clientes/form --flat' usando Angular-CLI.El comando '--flat' sirve para no crear el directorio('form' en 'clientes') indicado en el comando.
   AL usar este comando,también se registra automáticamente en el módulo principal de la aplicación 'app.module.ts' */

/* Nota: Se implementa el patrón 'Observable' -> En nuestro servicio 'cliente.service' se define el sujeto observable y aquí,en el componente, definimos el observador que se suscribe al observable.
  De esta manera,cuando la información del observable cambia,el observador que se ha suscrito recibe la notificación de ese cambio y ejecuta la tarea implmentada en el método subscribe().
  Con este patrón, se trabaja de forma reactiva y asíncrona.*/

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html'
})
export class FormComponent implements OnInit {
  // Las propiedades que no tengan restricciones de visibilidad,como por ejemplo 'private',son por defecto públicos y sí tienen visibilidad en la vista asociada a este componente
  // Las propiedades que tengan la restricción 'private' solo tienen visibilidad en esta clase y no son visibles en la vista(html) asociada a este componente
  private cliente: Cliente = new Cliente(); // Esta propiedad,que es una instancia de 'Cliente',va a servir para almacenar la información introducida en el formulario que se encuentra en la vista asociada a este componente
  private titulo: string = "Crear cliente"; // Por defecto, se supone que vamos a crear un nuevo cliente.Cuando se detecte que vamos a editar un cliente existente, se cambiará el valor de esta propiedad
  errores: string[]; // Esta propiedad almacena en un array los errores que nos devuelve el componente de servicio 'cliente.service' cuando se usan los métodos 'crear' y 'update'

  constructor(private clienteService: ClienteService,private router: Router,private activatedRoute: ActivatedRoute) { // Inyectamos en este componente el servicio 'ClienteService' y lo podemos usar mediante la propiedad 'clienteService'.También inyectamos el módulo 'Router' de Angular, para especificar la navegabilidad con una ruta mediante la propiedad 'router', y el módulo 'ActivatedRoute' para poder recuperar el id del cliente desde la url o ruta

  }

  // Este método se ejecuta después del constructor justo cuando se inicia este componente
  ngOnInit() {
    // Cuando se inicial el componente,queremos cargar todos los datos del cliente mediante la invocación al método 'cargarCliente()'
    this.cargarCliente();
  }

  // Creamos un nuevo cliente
  create(): void{
    // Creamos un nuevo cliente usando el método 'crear' del servicio 'clienteService'.
    this.clienteService.crear(this.cliente).subscribe(
      /* Se trata de una función anónima que se podría poner de la siguiente manera:
        function(respuesta){
          // Implementación
        } */
       // En este caso,desde el componente de nuestro servicio 'cliente.service',nuestro método 'crear()' devuelve directamente un Observable<Cliente>(Hemos procesado esta información con el operador 'map')
      cliente => {
        // Navegamos hasta la ruta '/clientes'
        this.router.navigate(['/clientes']);
        // Mostramos una ventana de éxito con sweetalert2 usando la propiedad "nombre" del objeto "cliente"
        // Recibe el título,el texto de la ventana y el tipo de ventana que deseamos mostrar.
        swal('Nuevo cliente',`El cliente ${cliente.nombre} ha sido creado con éxito!`,'success');
      },
      // En caso de que el componente de sevicio 'cliente.service' nos devuelva errores
      err => {
        // Mostramos en la consola de error este mensaje junto con el código de estado de error
        console.error("Código del error desde el backend: " + err.status);
        // Mostramos en la consola de error la información de los errores.Para ello, accedemos a 'err.error.errors'(esta estructura es así debido a que así la hemos considerado en nuestro backend(API Rest de clientes))
        console.error(err.error.errors);
        // Asociamos los errores a nuestra propiedad 'errores'.Para ello, accedemos a 'err.error.errors'(esta estructura es así debido a que así la hemos considerado en nuestro backend(API Rest de clientes)).Para concretar más en el tipo de datos,como 'err.error.errors' nos da un tipo 'Any',mediante la instrucción 'as' lo convertimos a un array de string 'string[]',que es el tipo de dato de la propiedad 'errores'
        this.errores = err.error.errors as string[];
      }
    );
  }

  // Obtenemos los datos de un cliente
  cargarCliente():void{
    // Accedemos a los parametros que viajan con la url o ruta mediante la propiedad 'activatedRoute' de tipo ActivatedRoute.'params' nos devuelve un Observable y, por ello,tenemos que suscribirnos
    this.activatedRoute.params.subscribe(
      params => {
        // Obtenemos el 'id' de los parámetros.Es 'id' ya que así se lo hemos indicado en las rutas definidas en el moódulo 'aap.module.ts'
        let id = params['id'];
        // Si el id existe, se trata de una edición de un cliente.Entonces, usamos nuestro servicio 'clienteService' para obtener sus datos a partir de dicho id
        // Si no existe el id, no hacemos nada porque se trata de la creación de un nuevo cliente y no necesitamos ir a nuestro backend a través de nuestro servicio 'clienteService' pata obtener datos de clientes 
        if(id){
          this.titulo = "Editar cliente" // Cambiamos el valor de la propiedad "titulo" de esta clase a "Editar cliente"
          // Llamamos al método 'getCliente' de nuestro servicio 'clienteService' pasándole el id recuperado.Como nos devuelve un Observable,nos suscribimos y asociamos la respuesta('cliente') a nuestra propiedad 'cliente'
          this.clienteService.getCliente(id).subscribe(
            cliente => this.cliente = cliente
          );
        }
      }
    )
  }

   // Actualizamos los datos de un cliente
  update():void{
    // Actualizamos los datos de un cliente usando el método 'update' del servicio 'clienteService'
    this.clienteService.update(this.cliente).subscribe(
      // En este caso,desde el componente de nuestro servicio 'cliente.service',nuestro método 'update(cliente)' devuelve un Obsevable de tipo 'Any' que se corresponde con un json con la información del cliente actualizado y un mensaje que informa del estado de dicha actualización(No hemos procesado la infomación con el operador 'map')
      respuesta => {
        // Navegamos hasta la ruta '/clientes'
        this.router.navigate(['/clientes']);
        // Mostramos una ventana de éxito con sweetalert2 usando la propiedad "mensaje" del objeto "respuesta" y la propiedad "nombre" de la propiedad "cliente" de dicho objeto
        // Recibe el título,el texto de la ventana y el tipo de ventana que deseamos mostrar.
        swal('Cliente Actualizado',`${respuesta.mensaje}: ${respuesta.cliente.nombre}`,'success');
      },
      // En caso de que el componente de sevicio 'cliente.service' nos devuelva errores
      err => {
        // Mostramos en la consola de error este mensaje junto con el código de estado de error
       console.error("Código del error desde el backend: " + err.status);
       // Mostramos en la consola de error la información de los errores.Para ello, accedemos a 'err.error.errors'(esta estructura es así debido a que así la hemos considerado en nuestro backend(API Rest de clientes))
       console.error(err.error.errors);
       // Asociamos los errores a nuestra propiedad 'errores'.Para ello, accedemos a 'err.error.errors'(esta estructura es así debido a que así la hemos considerado en nuestro backend(API Rest de clientes)).Para concretar más en el tipo de datos,como 'err.error.errors' nos da un tipo 'Any',mediante la instrucción 'as' lo convertimos a un array de string 'string[]',que es el tipo de dato de la propiedad 'errores'
       this.errores = err.error.errors as string[];
     }
    );
  }

}
