package com.server.modules;

import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.common.tls.TLSServerConfig;
import com.api.common.tls.TLSServerConfigBuilder;
import com.api.requests.CopyRequest;
import com.api.requests.LoginRequest;
import com.api.requests.MkDirRequest;
import com.api.services.DispatcherService;
import com.server.FServer;
import com.server.ServerConfigs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;


/**
 * Class  com.server.com.server.FServer  creates the main server (dispatcher server) for the com.server.com.server.FServer service
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
@SpringBootApplication
@RestController
@Configuration
public class FDispatcher extends FServer implements DispatcherService<ResponseEntity<String>> {

    /** Constants */
    public static final int PORT = 8081;
    public static final InputStream CONFIG_FILE = FDispatcher.class.getClassLoader().getResourceAsStream("servertls.conf");
    public static final String KEYSTORE_PATH = "classpath:fserver-dispatcher-ks.jks";
    public static final String KEY_ALIAS = "fserver-dispatcher";
    public static final String TRUSTSTORE_PATH = "classpath:fserver-dispatcher-ts.jks";


    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() {
        StorePasswords passwords = Shell.loadTrustKeyStoresPass();
        return createWebServerFactory(new ServerConfigs(PORT, CONFIG_FILE, KEYSTORE_PATH, KEY_ALIAS, TRUSTSTORE_PATH, passwords));
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
