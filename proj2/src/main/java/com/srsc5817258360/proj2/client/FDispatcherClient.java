package com.srsc5817258360.proj2.client;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class FDispatcherClient extends FClient {
    public FDispatcherClient(String URL){
        super(URL);
    }


    public Mono<ResponseEntity<String>> login(String username, String password) {
        return webClient.post()
                .uri("/login")
                .body(Mono.just(new LoginRequest(username, password)), LoginRequest.class)
                .retrieve()
                .toEntity(String.class);
    }

    // 2. ls username
    public Mono<ResponseEntity<String>> listFiles(String username) {
        return webClient.get()
                .uri("/ls/{username}", username)
                .retrieve()
                .toEntity(String.class);
    }

    // 3. ls username path
    public Mono<ResponseEntity<String>> listFilesAtPath(String username, String path) {
        return webClient.get()
                .uri("/ls/{username}/{path}", username, path)
                .retrieve()
                .toEntity(String.class);
    }

    // 4. mkdir username path
    public Mono<ResponseEntity<String>> createDirectory(String username, String path) {
        return webClient.post()
                .uri("/mkdir/{username}/{path}", username, path)
                .retrieve()
                .toEntity(String.class);
    }

    // 5. put username path/file
    public Mono<ResponseEntity<String>> uploadFile(String username, String path, String fileName) {
        // Implementação para envio de arquivo
        // Você precisará construir um objeto de requisição que inclua o arquivo a ser enviado.
        // Considere usar webClient.post()...body(BodyInserters.fromMultipartData(...)) para upload de arquivo.
        return null;
    }

    // 6. get username path/file
    public Mono<ResponseEntity<String>> downloadFile(String username, String path, String fileName) {
        return webClient.get()
                .uri("/get/{username}/{path}/{file}", username, path, fileName)
                .retrieve()
                .toEntity(String.class);
    }

    // 7. cp username path1/file1 path2/file2
    public Mono<ResponseEntity<String>> copyFile(String username, String sourcePath, String sourceFile, String destPath, String destFile) {
        return webClient.post()
                .uri("/cp/{username}/{sourcePath}/{sourceFile}/{destPath}/{destFile}",
                        username, sourcePath, sourceFile, destPath, destFile)
                .retrieve()
                .toEntity(String.class);
    }

    // 8. rm username path/file
    public Mono<ResponseEntity<String>> removeFile(String username, String path, String fileName) {
        return webClient.delete()
                .uri("/rm/{username}/{path}/{file}", username, path, fileName)
                .retrieve()
                .toEntity(String.class);
    }

    // 9. file username path/file
    public Mono<ResponseEntity<String>> fileInfo(String username, String path, String fileName) {
        return webClient.get()
                .uri("/file/{username}/{path}/{file}", username, path, fileName)
                .retrieve()
                .toEntity(String.class);
    }

    // Classe interna para representar a requisição de login
    private static class LoginRequest {
        private final String username;
        private final String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
