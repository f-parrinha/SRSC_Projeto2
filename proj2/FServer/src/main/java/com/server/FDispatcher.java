package com.server;

import com.api.rest.RestResponse;
import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.common.tls.TLSClientConfig;
import com.api.common.tls.TLSConfigFactory;
import com.api.rest.requests.CopyRequest;
import com.api.rest.requests.LoginRequest;
import com.api.rest.requests.MkDirRequest;
import com.api.rest.requests.PutRequest;
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

import java.io.InputStream;


/**
 * Class  FDispatcher  dispatches the different requests for the different modules (Auth, Access and Storage) in the FServer platform
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
    private static String[] args;


    /** Variables */
    private FStorageClient storageClient;
    private FAuthClient authClient;
    private FAccessClient accessClient;

    public static void main(String[] args) {
        FDispatcher.args = args;
        SpringApplication.run(FDispatcher.class, args);
    }


    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() {
        StorePasswords passwords = Shell.loadTrustKeyStoresPass(args);
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
        var response = storageClient.listDirectories(username);

        // Check if FStorage is on
        if (response == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("No response came from the storage server. It may be currently off.");
        }
        
        HttpStatus status = HttpStatus.resolve(response.statusCode());
        return new RestResponse(status).buildResponse(response.body());
    }

    @GetMapping("/ls/{username}/{*path}")
    @Override
    public ResponseEntity<String> listFiles(@PathVariable String username, @PathVariable String path) {
        var response = storageClient.listDirectories(username, path.substring(1));

        // Check if FStorage is on
        if (response == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("No response came from the storage server. It may be currently off.");
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());
        return new RestResponse(status).buildResponse(response.body());
    }

    @PostMapping("/mkdir/{username}")
    @Override
    public ResponseEntity<String> makeDirectory(@PathVariable String username, @RequestBody MkDirRequest mkDirRequest) {
        var response = storageClient.createFolder(username, mkDirRequest);

        // Check if FStorage is on
        if (response == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("No response came from the storage server. It may be currently off.");
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());
        return new RestResponse(status).buildResponse(response.body());
    }

    @PutMapping("/put/{username}")
    @Override
    public ResponseEntity<String> put(@PathVariable String username, @RequestBody PutRequest request) {
        var response = storageClient.createFile(username, request);

        // Check if FStorage is on
        if (response == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("No response came from the storage server. It may be currently off.");
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());
        return new RestResponse(status).buildResponse(response.body());
    }

    @GetMapping("/get/{username}/{*path}")
    @Override
    public ResponseEntity<String> get(@PathVariable String username, @PathVariable String path) {
        var response = storageClient.getFile(username, path.substring(1));

        if (response == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("No response came from the storage server. It may be currently off.");
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());
        return new RestResponse(status).buildResponse(response.body());
    }

    @PostMapping("/cp/{username}")
    @Override
    public ResponseEntity<String> copy(@PathVariable String username, @RequestBody CopyRequest copyRequest) {
        return ResponseEntity.ok("Arquivo " + copyRequest.sourceFile() + " copiado de " + copyRequest.sourcePath() +
                " para " + copyRequest.destPath() + " com o nome " + copyRequest.destFile());
    }

    @DeleteMapping("/rm/{username}/{*path}")
    @Override
    public ResponseEntity<String> remove(@PathVariable String username, @PathVariable String path) {
        var response = storageClient.removeFile(username, path.substring(1));

        if (response == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("No response came from the storage server. It may be currently off.");
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());
        return new RestResponse(status).buildResponse(response.body());
    }

    @GetMapping("/file/{username}/{path}")
    @Override
    public ResponseEntity<String> file(@PathVariable String username, @PathVariable String path) {
        return ResponseEntity.ok("Informações sobre o arquivo para " + username + " no caminho " + path);
    }
}
