package com.server;

import com.api.requests.CopyRequest;
import com.api.requests.LoginRequest;
import com.api.requests.MkDirRequest;
import com.api.services.DispatcherService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Class  com.server.com.server.FServer  creates the main server (dispatcher server) for the com.server.com.server.FServer service
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
@SpringBootApplication
@RestController
public class FDispatcher extends FServer implements DispatcherService<ResponseEntity<String>> {

    /** Constants */
    public static final int PORT = 8081;
    public static final String KEYSTORE_PATH = "classpath:fdispatcher-ks.jks";
    public static final String KEY_ALIAS = "fdispatcher";
    public static final String TRUSTSTORE_PATH = "classpath:fdispatcher-ts.jks";

    public static void main(String[] args) {
        SpringApplication.run(FDispatcher.class, args);
    }


    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() {
        return createWebServerFactory(PORT, KEYSTORE_PATH, KEY_ALIAS, TRUSTSTORE_PATH);
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<String>  login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok("Login realizado com sucesso");
    }

    @GetMapping("/ls/{username}")
    @Override
    public ResponseEntity<String> listFiles(@PathVariable String username) {
        return ResponseEntity.ok("Lista de arquivos/diretórios para " + username);
    }

    @GetMapping("/ls/{username}/{path}")
    @Override
    public ResponseEntity<String> listFiles(@PathVariable String username, @PathVariable String path) {
        return ResponseEntity.ok("Lista de arquivos/diretórios para " + username + " no caminho " + path);
    }

    @PostMapping("/mkdir/{username}")
    @Override
    public ResponseEntity<String> makeDirectory(@PathVariable String username, @RequestBody MkDirRequest mkDirRequest) {
        return ResponseEntity.ok("Diretório criado com sucesso para " + username + " no caminho " + mkDirRequest.path());
    }

    @PostMapping("/put/{username}/{path}/{file}")
    @Override
    public ResponseEntity<String>  put(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return ResponseEntity.ok("Arquivo " + file + " enviado com sucesso para " + username + " no caminho " + path);
    }

    @GetMapping("/get/{username}/{path}/{file}")
    public ResponseEntity<String>  get(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return ResponseEntity.ok("Arquivo " + file + " baixado com sucesso para " + username + " no caminho " + path);
    }

    @PostMapping("/cp/{username}")
    @Override
    public ResponseEntity<String>  copy(@PathVariable String username, @RequestBody CopyRequest copyRequest) {
        return ResponseEntity.ok("Arquivo " + copyRequest.sourceFile() + " copiado de " + copyRequest.sourcePath() +
                " para " + copyRequest.destPath() + " com o nome " + copyRequest.destFile());
    }

    @DeleteMapping("/rm/{username}/{path}/{file}")
    @Override
    public ResponseEntity<String>  remove(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return ResponseEntity.ok("Arquivo " + file + " removido com sucesso de " + username + " no caminho " + path);
    }

    @GetMapping("/file/{username}/{path}/{file}")
    @Override
    public ResponseEntity<String>  file(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return ResponseEntity.ok("Informações sobre o arquivo " + file + " para " + username + " no caminho " + path);
    }
}
