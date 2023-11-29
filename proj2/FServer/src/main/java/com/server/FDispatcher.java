package com.server;

import com.api.RestResponse;
import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.common.tls.TLSClientConfig;
import com.api.common.tls.TLSConfigFactory;
import com.api.requests.CopyRequest;
import com.api.requests.LoginRequest;
import com.api.requests.MkDirRequest;
import com.api.services.DispatcherService;
import com.client.serviceClients.FAccessClient;
import com.client.serviceClients.FAuthClient;
import com.client.serviceClients.FStorageClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


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
    public static final InputStream KEYSTORE_FILE = FDispatcher.class.getClassLoader().getResourceAsStream("fdispatcher-ks.jks");
    public static final InputStream TRUSTSTORE_FILE = FDispatcher.class.getClassLoader().getResourceAsStream("fdispatcher-ts.jks");


    /** Variables */
    private FStorageClient storageClient;
    private FAuthClient authClient;
    private FAccessClient accessClient;

    public static void main(String[] args) {
        SpringApplication.run(FDispatcher.class, args);
    }


    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() throws IOException, URISyntaxException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        StorePasswords passwords = Shell.loadTrustKeyStoresPass();
        TLSClientConfig tls = TLSConfigFactory.getInstance().forClient()
                .withConfigFile(CLIENT_CONFIG_FILE)
                .withKeyStoreFile(KEYSTORE_FILE)
                .withKeyStorePass(passwords.keyStorePass())
                .withTrustStoreFile(TRUSTSTORE_FILE)
                .withTrustStorePass(passwords.trustStorePass())
                .build();

        authClient = new FAuthClient(AUTH_URL, tls.getSslContext(), tls.getSslParameters());
        accessClient = new FAccessClient(ACCESS_URL, tls.getSslContext(), tls.getSslParameters());
        storageClient = new FStorageClient(STORAGE_URL, tls.getSslContext(), tls.getSslParameters());
        return createWebServerFactory(PORT, KEYSTORE_PATH, KEY_ALIAS, TRUSTSTORE_PATH, passwords);
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
    public ResponseEntity<String> makeDirectory(@PathVariable String username, @RequestBody MkDirRequest mkDirRequest) throws IOException, InterruptedException {
        var storageResponse = storageClient.createDirectory(username, mkDirRequest);
        HttpStatus status = HttpStatus.resolve(storageResponse.statusCode());
        return new RestResponse(status).buildResponse(storageResponse.body());
    }

    @PostMapping("/put/{username}/{path}/{file}")
    @Override
    public ResponseEntity<String>  put(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return ResponseEntity.ok("Arquivo " + file + " enviado com sucesso para " + username + " no caminho " + path);
    }

    @GetMapping("/get/{username}/{path}/{file}")
    @Override
    public ResponseEntity<String>  get(@PathVariable String username, @PathVariable String path, @PathVariable String file) throws IOException, InterruptedException {
        var storageResponse = storageClient.getFile(username, path, file);
        HttpStatus status = HttpStatus.resolve(storageResponse.statusCode());
        return new RestResponse(status).buildResponse(storageResponse.body());
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
