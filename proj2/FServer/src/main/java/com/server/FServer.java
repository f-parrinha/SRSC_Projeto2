package com.server;

import com.api.LoginRequest;
import com.api.common.tls.TLSServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;


/**
 * Class  FServer  creates the main server (dispatcher server) for the FServer service
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 *
 * @TODO Add the right functionalities. Right now it just prints the requests' info
 */
@SpringBootApplication
@RestController
@Configuration
public class FServer {

    /** Constants */
    public static final int PORT = 8081;
    public static final InputStream CONFIG_FILE = FServer.class.getClassLoader().getResourceAsStream("servertls.conf");
    public static final String KEYSTORE_PATH = "classpath:fserver-dispatcher-ks.jks";
    public static final String KEYSTORE_PASS = "password";  /* Place this in a secure file *** ignored by git!! *** */
    public static final String KEY_ALIAS = "fserver-dispatcher";
    public static final String KEY_PASS = "password";   /* Place this in a secure file *** ignored by git!! *** */
    public static final String TRUSTSTORE_PATH = "classpath:fserver-dispatcher-ts.jks";
    public static final String TRUSTSTORE_PASS = "password";

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            try {
                TLSServerConfig tls = new TLSServerConfig.Builder()
                        .withConfigFile(CONFIG_FILE)
                        .withKeyStorePath(KEYSTORE_PATH)
                        .withKeyStorePass(KEYSTORE_PASS)
                        .withKeyAlias(KEY_ALIAS)
                        .withKeyPass(KEY_PASS)
                        .withTrustStorePath(TRUSTSTORE_PATH)
                        .withTrustStorePass(TRUSTSTORE_PASS)
                        .build();

                factory.setSsl(tls.getSslContext());
                factory.setPort(PORT);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(FServer.class, args);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>>  login(@RequestBody LoginRequest loginRequest) {
        return Mono.just(ResponseEntity.ok("Login realizado com sucesso"));
    }

    @GetMapping("/ls/{username}")
    public Mono<ResponseEntity<String>>  listFiles(@PathVariable String username) {
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
    public Mono<ResponseEntity<String>>  uploadFile(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return Mono.just(ResponseEntity.ok("Arquivo " + file + " enviado com sucesso para " + username + " no caminho " + path));
    }

    @GetMapping("/get/{username}/{path}/{file}")
    public Mono<ResponseEntity<String>>  downloadFile(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return Mono.just(ResponseEntity.ok("Arquivo " + file + " baixado com sucesso para " + username + " no caminho " + path));
    }

    @PostMapping("/cp/{username}/{sourcePath}/{sourceFile}/{destPath}/{destFile}")
    public Mono<ResponseEntity<String>>  copyFile(
            @PathVariable String username,
            @PathVariable String sourcePath,
            @PathVariable String sourceFile,
            @PathVariable String destPath,
            @PathVariable String destFile) {
        return Mono.just(ResponseEntity.ok("Arquivo " + sourceFile + " copiado de " + sourcePath + " para " + destPath + " com o nome " + destFile));
    }

    @DeleteMapping("/rm/{username}/{path}/{file}")
    public Mono<ResponseEntity<String>>  removeFile(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return Mono.just(ResponseEntity.ok("Arquivo " + file + " removido com sucesso de " + username + " no caminho " + path));
    }

    @GetMapping("/file/{username}/{path}/{file}")
    public Mono<ResponseEntity<String>>  fileInfo(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return Mono.just(ResponseEntity.ok("Informações sobre o arquivo " + file + " para " + username + " no caminho " + path));
    }
}
