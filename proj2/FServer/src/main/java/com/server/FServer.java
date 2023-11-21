package com.server;

import com.api.LoginRequest;
import com.client.serviceClients.FAuthClient;
import com.client.serviceClients.FClient;
import com.client.shell.ShellPreconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


/**
 * Class  FServer  creates the main server (dispatcher server) for the FServer service
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 * @TODO Add the right functionalities. Right now it just prints the requests' info
 */
@SpringBootApplication
@RestController
public class FServer {
    private static final String SERVER_URL = "http://localhost:8082";
    private final FAuthClient authClient;


    public FServer() {
        this.authClient = new FAuthClient(SERVER_URL);
    }

    public static void main(String[] args) {
        SpringApplication.run(FServer.class, args);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody LoginRequest loginRequest) {

        var response = authClient.authenticateUser(loginRequest.username(), loginRequest.password());

        // Process response
        response.subscribe(
                result -> {
                    System.out.println("Result: " + result);
                },
                error -> {
                    System.err.println("Error: " + error.getMessage());
                },
                () -> { /* Leaving empty... */ }
        );
        return Mono.just(ResponseEntity.ok("Autenticacao realizada com sucesso"));
    }

    @GetMapping("/ls/{username}")
    public Mono<ResponseEntity<String>> listFiles(@PathVariable String username) {
        return Mono.just(ResponseEntity.ok("Lista de arquivos/diretórios para " + username));
    }

    @GetMapping("/ls/{username}/{path}")
    public Mono<ResponseEntity<String>> listFiles(@PathVariable String username, @PathVariable String path) {
        return Mono.just(ResponseEntity.ok("Lista de arquivos/diretórios para " + username + " no caminho " + path));
    }

    @PostMapping("/mkdir/{username}/{path}")
    public Mono<ResponseEntity<String>> createDirectory(@PathVariable String username, @PathVariable String path) {
        return Mono.just(ResponseEntity.ok("Diretório criado com sucesso para " + username + " no caminho " + path));
    }

    @PostMapping("/put/{username}/{path}/{file}")
    public Mono<ResponseEntity<String>> uploadFile(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return Mono.just(ResponseEntity.ok("Arquivo " + file + " enviado com sucesso para " + username + " no caminho " + path));
    }

    @GetMapping("/get/{username}/{path}/{file}")
    public Mono<ResponseEntity<String>> downloadFile(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return Mono.just(ResponseEntity.ok("Arquivo " + file + " baixado com sucesso para " + username + " no caminho " + path));
    }

    @PostMapping("/cp/{username}/{sourcePath}/{sourceFile}/{destPath}/{destFile}")
    public Mono<ResponseEntity<String>> copyFile(
            @PathVariable String username,
            @PathVariable String sourcePath,
            @PathVariable String sourceFile,
            @PathVariable String destPath,
            @PathVariable String destFile) {
        return Mono.just(ResponseEntity.ok("Arquivo " + sourceFile + " copiado de " + sourcePath + " para " + destPath + " com o nome " + destFile));
    }

    @DeleteMapping("/rm/{username}/{path}/{file}")
    public Mono<ResponseEntity<String>> removeFile(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return Mono.just(ResponseEntity.ok("Arquivo " + file + " removido com sucesso de " + username + " no caminho " + path));
    }

    @GetMapping("/file/{username}/{path}/{file}")
    public Mono<ResponseEntity<String>> fileInfo(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return Mono.just(ResponseEntity.ok("Informações sobre o arquivo " + file + " para " + username + " no caminho " + path));
    }
}
