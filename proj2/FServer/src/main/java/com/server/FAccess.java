package com.server;

import com.api.RestResponse;
import com.api.access.PermissionsType;
import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.requests.SingleDataRequest;
import com.api.services.AccessService;
import com.api.utils.JwtTokenUtil;
import com.api.utils.UtilsBase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class FAccess extends FServer implements AccessService<ResponseEntity<String>> {

    /**
     * Constants
     */
    public static final int PORT = 8083;
    public static final String KEYSTORE_PATH = "classpath:faccess-ks.jks";
    public static final String KEY_ALIAS = "faccess";
    public static final String TRUSTSTORE_PATH = "classpath:faccess-ts.jks";
    private static final int KEY_SIZE = 2048;
    private static final String SIGNATURE_ALGORITHM = "RSA";

    private KeyPair rsaKeyPair;
    private static Map<String, PermissionsType> accessControlMap;

    public static void main(String[] args) {
        SpringApplication.run(FAccess.class, args);
        initializeAccessControl();
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() throws NoSuchAlgorithmException {
        StorePasswords passwords = Shell.loadTrustKeyStoresPass();
        accessControlMap = new HashMap<>();
        rsaKeyPair = UtilsBase.generateKeyPair(SIGNATURE_ALGORITHM, KEY_SIZE);

        return createWebServerFactory(PORT, KEYSTORE_PATH, KEY_ALIAS, TRUSTSTORE_PATH, passwords);
    }

    @GetMapping("/access/token/{username}")
    @Override
    public ResponseEntity<String> requestAccessControlToken(@PathVariable String username, @RequestHeader("Authorization") String authToken) {

        System.out.println("Access Token request received from " + username);

        PermissionsType type = accessControlMap.get(username);
        if (type == null || type == PermissionsType.DENY) {
            System.out.println("Wrong permissions...aborting!");
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("Wrong permissions!");
        }
        String token = JwtTokenUtil.createJwtToken(rsaKeyPair.getPrivate(), username, "FAccess", type.getValue());
        System.out.println("Token generated successfully: " + token);
        return new RestResponse(HttpStatus.OK).buildResponse(token);
    }

    @GetMapping("/access/RSAKeyExchange")
    @Override
    public ResponseEntity<String> rsaPublicKeyExchange() {
        SingleDataRequest key = new SingleDataRequest(rsaKeyPair.getPublic().getEncoded());
        return new RestResponse(HttpStatus.OK).buildResponse(key.serialize().toString());
    }

    /**
     * Loads all user's permissions from a file
     */
    private static void initializeAccessControl() {
        try {
            // Load the access.conf file using the ClassPathResource
            ClassPathResource resource = new ClassPathResource("access.conf");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Parse each line and add access control rules to the map
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Line: " + line);
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0].trim();
                    String accessLevelString = parts[1].trim();
                    PermissionsType accessLevel = PermissionsType.fromString(accessLevelString);
                    accessControlMap.put(username, accessLevel);
                }
            }
            System.out.println(accessControlMap.size());
            // Close the resources
            reader.close();
            inputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}



