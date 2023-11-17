package com.srsc5817258360.proj2.server.mainDispatcher;

import com.srsc5817258360.proj2.client.FAccessControlClient;
import com.srsc5817258360.proj2.client.FAuthClient;
import com.srsc5817258360.proj2.client.FStorageClient;
import com.srsc5817258360.proj2.server.FServer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FServerDispatcher extends FServer {

    private final FAuthClient authClient;
    private final FAccessControlClient accessControlClient;
    private final FStorageClient fStorageClient;
    public FServerDispatcher(FAuthClient authClient, FAccessControlClient accessControlClient, FStorageClient fStorageClient) {
        this.authClient = authClient;
        this.accessControlClient = accessControlClient;
        this.fStorageClient = fStorageClient;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // Implemente a lógica de autenticação utilizando o authClient
        // Retorne a resposta adequada
        return ResponseEntity.ok("Login realizado com sucesso");
    }

    // 2. ls username
    @GetMapping("/ls/{username}")
    public ResponseEntity<String> listFiles(@PathVariable String username) {
        // Implemente a lógica para listar arquivos/diretórios usando o accessControlClient
        // Retorne a resposta adequada
        return ResponseEntity.ok("Lista de arquivos/diretórios para " + username);
    }

    // 3. ls username path
    @GetMapping("/ls/{username}/{path}")
    public ResponseEntity<String> listFilesAtPath(@PathVariable String username, @PathVariable String path) {
        // Implemente a lógica para listar arquivos/diretórios em um caminho específico
        // Retorne a resposta adequada
        return ResponseEntity.ok("Lista de arquivos/diretórios para " + username + " no caminho " + path);
    }

    // 4. mkdir username path
    @PostMapping("/mkdir/{username}/{path}")
    public ResponseEntity<String> createDirectory(@PathVariable String username, @PathVariable String path) {
        // Implemente a lógica para criar um diretório usando o accessControlClient
        // Retorne a resposta adequada
        return ResponseEntity.ok("Diretório criado com sucesso para " + username + " no caminho " + path);
    }

    // 5. put username path/file
    @PostMapping("/put/{username}/{path}/{file}")
    public ResponseEntity<String> uploadFile(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        // Implemente a lógica para fazer upload de um arquivo usando o fStorageClient
        // Retorne a resposta adequada
        return ResponseEntity.ok("Arquivo " + file + " enviado com sucesso para " + username + " no caminho " + path);
    }

    // 6. get username path/file
    @GetMapping("/get/{username}/{path}/{file}")
    public ResponseEntity<String> downloadFile(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        // Implemente a lógica para fazer download de um arquivo usando o fStorageClient
        // Retorne a resposta adequada
        return ResponseEntity.ok("Arquivo " + file + " baixado com sucesso para " + username + " no caminho " + path);
    }

    // 7. cp username path1/file1 path2/file2
    @PostMapping("/cp/{username}/{sourcePath}/{sourceFile}/{destPath}/{destFile}")
    public ResponseEntity<String> copyFile(
            @PathVariable String username,
            @PathVariable String sourcePath,
            @PathVariable String sourceFile,
            @PathVariable String destPath,
            @PathVariable String destFile) {
        // Implemente a lógica para copiar um arquivo usando o accessControlClient e o fStorageClient
        // Retorne a resposta adequada
        return ResponseEntity.ok("Arquivo " + sourceFile + " copiado de " + sourcePath + " para " + destPath + " com o nome " + destFile);
    }

    // 8. rm username path/file
    @DeleteMapping("/rm/{username}/{path}/{file}")
    public ResponseEntity<String> removeFile(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        // Implemente a lógica para remover um arquivo usando o accessControlClient e o fStorageClient
        // Retorne a resposta adequada
        return ResponseEntity.ok("Arquivo " + file + " removido com sucesso de " + username + " no caminho " + path);
    }

    // 9. file username path/file
    @GetMapping("/file/{username}/{path}/{file}")
    public ResponseEntity<String> fileInfo(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        // Implemente a lógica para obter informações sobre um arquivo usando o fStorageClient
        // Retorne a resposta adequada
        return ResponseEntity.ok("Informações sobre o arquivo " + file + " para " + username + " no caminho " + path);
    }

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
