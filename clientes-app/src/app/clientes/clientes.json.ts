import { Cliente } from './cliente';

/* Esta clase simula un archivo json con los datos de los clientes */

// Definimos y exportamos una constante llamada 'CLIENTES' que contiene un array de objetos en formato json con los datos de los clientes
 export const CLIENTES: Cliente[] = [
    {id: 1,nombre: 'Andrés',apellido: 'Guzmán',createAt: 'profesor@bolsadeideas.com',email: '2017-12-11'},
    {id: 2,nombre: 'Mr. John',apellido: 'Doe',createAt: 'john.doe@gmail.com',email: '2017-12-11'},
    {id: 3,nombre: 'Linus',apellido: 'Torvalds',createAt: 'linus.torvalds@gmail.com',email: '2017-11-12'},
    {id: 4,nombre: 'Rasmus',apellido: 'Lerdorf',createAt: 'rasmus.lerdorf@gmail.com',email: '2017-11-13'}
  ];