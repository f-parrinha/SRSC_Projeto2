# Segurança de Redes e Sistemas de Computadores - Project 2

## FServer

Create a File Server service.

 - Has the capacity to store the different files (int final version it communicates with a cloud service)
 - Authorizes
 - Controls the different accesses
 - Has a secure communication channel

Uses microservices for its execution, having four modules:
  - Main dispatcher with a RESTful API to dispatch the requests to the different components
  - One component for the remaining modules
