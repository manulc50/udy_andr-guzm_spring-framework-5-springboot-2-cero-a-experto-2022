import { Component, OnInit } from '@angular/core';
import { Cliente } from './cliente';
import { ClienteService } from './cliente.service';
import swal from 'sweetalert2'; // Para poder lanzar ventanas de alerta o aviso más vistosas y robustas con sweetalert2
import { tap } from 'rxjs/operators';

/* Este componente ha sido creado usando el comando 'ng g c clientes' mediante Angular CLI.Con este comando, también se registra el componente automáticamente en el módulo principal de la aplicación 'app.module.ts' */

/* Código obtenido de la web 'sweetalert2.github.io' para implementar la ventana de confirmación de borrado*/
const swalWithBootstrapButtons = swal.mixin({
  confirmButtonClass: 'btn btn-success',
  cancelButtonClass: 'btn btn-danger',
  buttonsStyling: false,
})
/* ************************************ */

/* Nota:Se implementa el patrón 'Observable' -> En nuestro servicio 'Cliente.service' se define el sujeto observable y aquí,en el componente, definimos el observador que se suscribe al observable.
   De esta manera,cuando la información del observable cambia,el observador que se ha suscrito recibe la notificación de ese cambio y ejecuta una tarea(aquella definida en el método 'subscribe()')
   Con este patrón, se trabaja de forma reactiva y asíncrona.*/

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html'
})
export class ClientesComponent implements OnInit {
  // Las propiedades que no tengan restricciones de visibilidad,como por ejemplo 'private',son por defecto públicos y sí tienen visibilidad en la vista asociada a este componente
  // Las propiedades que tengan la restricción 'private' solo tienen visibilidad en esta clase y no son visibles en la vista(html) asociada a este componente

  clientes: Cliente[];

  constructor(private clienteService: ClienteService) { } // Inyectamos en este componente el servicio 'ClienteService' y lo podemos usar mediante la propiedad 'clienteService' 

  // Este método se ejecuta después del constructor justo cuando se inicia este componente
  ngOnInit() {
    // Obtenemos los datos de los clientes usando el metodo 'getClientes' del servicio 'clienteService'
    // El método 'pipe' nos permite usar operadores para objetos Observables como 'map','tap' y 'catchError'
    this.clienteService.getClientes().pipe(
      // El operador 'tap' nos permite trabajar con los datos para realizar algún tipo de tarea sin devolver nada y sin modificar dichos datos
      // En este punto,al operador 'tap' le llega en 'clientes' un Observable de tipo 'Cliente[]' debido a que así nos lo devuelve el método de nuestro servicio 'getClientes()'
      tap(clientes =>{
          console.log("En el tap de clientes.component:");
          // Iteramos el array contenido en 'clientes' y mostramos en la consola de log el nombre de cada cliente
          clientes.forEach(cliente => console.log(cliente.nombre));
        })
    )
    .subscribe(
      /* Se trata de una función anónima que se podría poner de la siguiente manera:
        function(clientes){
          Implementación
        }*/
      clientes => this.clientes = clientes
    );
  }

  // Método que elimina el cliente que se le pasa como argumento de entrada a este método mediante nuestro servicio "clienteService"
  delete(cliente: Cliente): void{
    /* Código obtenido de la web 'sweetalert2.github.io' para implementar la ventana de confirmación de borrado*/
    swalWithBootstrapButtons({
      title: 'Está seguro?',
      text: `¿Seguro que desea eliminar al cliente ${cliente.nombre} ${cliente.apellido}?`,
      type: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar!',
      cancelButtonText: 'No, cancelar!',
      reverseButtons: true
    }).then((result) => {
      // Si se ha pulsado sobre el bóton 'Sí, eliminar!' de la ventana de confirmación, se hace lo siguiente
      if (result.value) {
        // Ejecutamos el método 'delete' de nuestro servicio 'clienteService' pasándole el id_cliente y nos suscribimos al Observable que devuelve.
        this.clienteService.delete(cliente.id).subscribe(
          // En este caso,desde el componente de nuestro servicio 'clienteService',nuestro método 'delete(id)' devuelve un Obsevable de tipo 'Any' que se corresponde con un json un mensaje de éxito en la eliminación  
          respuesta => { 
            // El método anterior realiza el borrado del cliente sobre la base de datos,pero este array de clientes sigue manteniendo los datos del cliente borrado en memoria.Por eso,es necesario filtrar dicho array para quitarlo.Lo hacemos con el método 'filter' que recibe una función anónima de tipo flecha donde crea otro array con aquellos clientes distintos al borrado('cli => cli !== cliente')
            this.clientes = this.clientes.filter(cli => cli !== cliente);
            // Por último,muestra una ventana de éxito de sweetalert2
            swalWithBootstrapButtons( 'Cliente Eliminado!',respuesta.mensaje,'success');
          }
        ); 
      }
    })

  }

}

