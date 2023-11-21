package com.client.serviceClients;

import com.client.AbstractClient;
import com.api.LoginRequest;
import com.api.services.FServerService;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Class  FDispatcherClient  offers tools to create requests to the FServer (Dispatcher module)
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */

public class FClient extends AbstractClient implements FServerService {
    public FClient(String URL){
        super(URL);
    }


    @Override
    public Mono<ResponseEntity<String>> login(String username, String password) {
        return webClient.post()
                .uri("/login")
                .body(Mono.just(new LoginRequest(username, password)), LoginRequest.class)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Mono<ResponseEntity<String>> listFiles(String username) {
        return webClient.get()
                .uri("/ls/{username}", username)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Mono<ResponseEntity<String>> listFiles(String username, String path) {
        return webClient.get()
                .uri("/ls/{username}/{path}", username, path)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Mono<ResponseEntity<String>> makeDirectory(String username, String path) {
        return webClient.post()
                .uri("/mkdir/{username}/{path}", username, path)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Mono<ResponseEntity<String>> put(String username, String path, String fileName) {
        /*@TODO:
         *  - Add File resource
         *  - Add request using that resource and place it on body
         */
        return null;
    }

    @Override
    public Mono<ResponseEntity<String>> get(String username, String path, String fileName) {
        /*@TODO*/
        return null;
    }

    @Override
    public Mono<ResponseEntity<String>> copy(String username, String sourcePath, String sourceFile, String destPath, String destFile) {
        return webClient.post()
                .uri("/cp/{username}/{sourcePath}/{sourceFile}/{destPath}/{destFile}",
                        username, sourcePath, sourceFile, destPath, destFile)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Mono<ResponseEntity<String>> remove(String username, String path, String fileName) {
        return webClient.delete()
                .uri("/rm/{username}/{path}/{file}", username, path, fileName)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Mono<ResponseEntity<String>> file(String username, String path, String fileName) {
        return webClient.get()
                .uri("/file/{username}/{path}/{file}", username, path, fileName)
                .retrieve()
                .toEntity(String.class);
    }
}
