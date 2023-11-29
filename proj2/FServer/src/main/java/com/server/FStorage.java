package com.server;

import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.requests.MkDirRequest;
import com.api.services.StorageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@SpringBootApplication
@RestController
public class FStorage extends FServer implements StorageService<ResponseEntity<String>> {

    /** Constants */
    public static final int PORT = 8084;
    public static final String KEYSTORE_PATH = "classpath:fstorage-ks.jks";
    public static final String KEY_ALIAS = "fstorage";
    public static final String TRUSTSTORE_PATH = "classpath:fstorage-ts.jks";


    public static void main(String[] args) {
        SpringApplication.run(FStorage.class, args);
    }


    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() {
        StorePasswords passwords = Shell.loadTrustKeyStoresPass();
        return createWebServerFactory(PORT, KEYSTORE_PATH, KEY_ALIAS, TRUSTSTORE_PATH, passwords);
    }

    @PostMapping("/storage/mkdir/{username}")
    @Override
    public ResponseEntity<String> createDirectory(@PathVariable String username, @RequestBody MkDirRequest mkDirRequest) throws IOException, InterruptedException {
        return ResponseEntity.ok("MKDIR TEST");
    }

    @GetMapping("/storage/get/{username}/{path}/{file}")
    @Override
    public ResponseEntity<String> getFile(@PathVariable String username, @PathVariable String path, @PathVariable String file) {
        return ResponseEntity.ok("GETFILE TEST");
    }

    @Override
    public ResponseEntity<String> createFile() {
        return null;
    }

    @Override
    public ResponseEntity<String> removeFile() {
        return null;
    }

    @Override
    public ResponseEntity<String> listContent() {
        return null;
    }

    @Override
    public ResponseEntity<String> copyFile() {
        return null;
    }
}
