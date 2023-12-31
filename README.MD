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

## Usage
Follow these steps to compile, launch the client and the servers:
- **MANDATORY**: Make sure the working directory is "proj2", inside the project folder
- Run "***mvn clean compile package***" to build
- Open a new terminal and go to FServer/target
- Run "***java -cp FServer-0.0.1-jar-with-dependencies.jar com.server.FAuth password password false***" to run the the auth server
- Run "***java -cp FServer-0.0.1-jar-with-dependencies.jar com.server.FAccess password password false***" to run the the access server
- Run "***java -cp FServer-0.0.1-jar-with-dependencies.jar com.server.FStorage password password false***" to run the the storage server
- Run "***java -cp FServer-0.0.1-jar-with-dependencies.jar com.server.FAuth password password false***" to run the the main server
- Open a new terminal and o to FClient/target
- Run "***java -jar FClient-0.0.1-jar-with-dependencies.jar password password false***" to run the client shell program

<br />
<br />

### Requirements
![image](https://github.com/f-parrinha/SRSC_Projeto2/assets/113953185/fd26bb2b-8c09-4be2-a772-7843f003d296)

### Phase 1 Arquitecture
![image](https://github.com/f-parrinha/SRSC_Projeto2/assets/113953185/4ce47c8a-4bdf-4a74-8880-8dc804195668)
Note: We are using Spring instead of JAVA RMI

### Phase 2 Arquitecture
![image](https://github.com/f-parrinha/SRSC_Projeto2/assets/113953185/1a87824a-8d25-4701-8020-3edc28ba49ce)
Note: We are using Spring instead of JAVA RMI

### Config File Layout
![image](https://github.com/f-parrinha/SRSC_Projeto2/assets/113953185/18b044b4-5384-4fb6-93d6-7743357b0e62)

### Client/Server Interactions
![image](https://github.com/f-parrinha/SRSC_Projeto2/assets/113953185/11b176ab-8367-4e75-9b84-bf9af1817c88)

### Authentication Protocol
![image](https://github.com/f-parrinha/SRSC_Projeto2/assets/113953185/b3d48229-90cd-44a4-b9b6-9e1782e0bb42)
