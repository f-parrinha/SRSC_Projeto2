package com.server;

import com.api.rest.RestRequest;
import com.api.rest.RestResponse;
import com.api.rest.requests.SingleDataRequest;
import com.api.utils.JwtTokenUtil;
import com.api.utils.UtilsBase;
import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.common.tls.TLSClientConfig;
import com.api.common.tls.TLSConfigFactory;
import com.api.rest.requests.*;
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
import java.net.http.HttpResponse;
import java.security.spec.InvalidKeySpecException;

import java.util.Objects;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;


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
    public static final String SIGNATURE_ALGORITHM = "RSA";
    private static String[] args;


    /** Variables */
    private static PublicKey authRSAPublicKey;
    private static PublicKey accessRSAPublicKey;
    /**
     * Variables
     */
    private FStorageClient storageClient;
    private static FAuthClient authClient;
    private static FAccessClient accessClient;

    public static void main(String[] args) {
        FDispatcher.args = args;
        SpringApplication.run(FDispatcher.class, args);
        requestAccessRSAPublicKey();
        requestAuthRSAPublicKey();
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

    @GetMapping("/init-connection/{username}")
    @Override
    public ResponseEntity<String> requestDHPublicKey(@PathVariable String username) {
        var response = authClient.requestDHPublicKey(username);
        HttpStatus status = HttpStatus.resolve(response.statusCode());

        return new RestResponse(status).buildResponse(response.body());
    }

    @PostMapping("/login/{username}")
    @Override
    public ResponseEntity<String> login(@RequestBody String request, @PathVariable String username) {
        var response = authClient.authenticateUser(request, username);
        HttpStatus status = HttpStatus.resolve(response.statusCode());

        return new RestResponse(status).buildResponse(response.body());

    }

    @GetMapping("/ls/{username}")
    @Override
    public ResponseEntity<String> listFiles(@PathVariable String username,  @RequestHeader("Authorization") String authHeader, @RequestHeader("Access") String accessHeader) {
        // Check authentication
        String authToken = JwtTokenUtil.extractToken(authHeader);
        if (!JwtTokenUtil.verifyAuthToken(authToken, authRSAPublicKey, username)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User not authenticated for this operation.");
        }

        // Check access
        String accessToken = JwtTokenUtil.extractToken(accessHeader);
        if (!JwtTokenUtil.verifyAccessToken(accessToken, accessRSAPublicKey, Request.Type.GET)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User does not have the correct rights for this operation.");
        }

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
    public ResponseEntity<String> listFiles(@PathVariable String username, @PathVariable String path, @RequestHeader("Authorization") String authHeader, @RequestHeader("Access") String accessHeader) {
        // Check authentication
        String authToken = JwtTokenUtil.extractToken(authHeader);
        if (!JwtTokenUtil.verifyAuthToken(authToken, authRSAPublicKey, username)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User not authenticated for this operation.");
        }

        // Check access
        String accessToken = JwtTokenUtil.extractToken(accessHeader);
        if (!JwtTokenUtil.verifyAccessToken(accessToken, accessRSAPublicKey, Request.Type.GET)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User does not have the correct rights for this operation.");
        }

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
    public ResponseEntity<String> makeDirectory(@PathVariable String username, @RequestBody MkDirRequest mkDirRequest,  @RequestHeader("Authorization") String authHeader, @RequestHeader("Access") String accessHeader) {
        // Check authentication
        String authToken = JwtTokenUtil.extractToken(authHeader);
        if (!JwtTokenUtil.verifyAuthToken(authToken, authRSAPublicKey, username)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User not authenticated for this operation.");
        }

        // Check access
        String accessToken = JwtTokenUtil.extractToken(accessHeader);
        if (!JwtTokenUtil.verifyAccessToken(accessToken, accessRSAPublicKey, Request.Type.POST)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User does not have the correct rights for this operation.");
        }

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
    public ResponseEntity<String> put(@PathVariable String username, @RequestBody PutRequest request,  @RequestHeader("Authorization") String authHeader, @RequestHeader("Access") String accessHeader) {
        // Check authentication
        String authToken = JwtTokenUtil.extractToken(authHeader);
        if (!JwtTokenUtil.verifyAuthToken(authToken, authRSAPublicKey, username)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User not authenticated for this operation.");
        }

        // Check access
        String accessToken = JwtTokenUtil.extractToken(accessHeader);
        if (!JwtTokenUtil.verifyAccessToken(accessToken, accessRSAPublicKey, Request.Type.PUT)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User does not have the correct rights for this operation.");
        }
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
    public ResponseEntity<String> get(@PathVariable String username, @PathVariable String path, @RequestHeader("Authorization") String authHeader, @RequestHeader("Access") String accessHeader) {
        // Check authentication
        String authToken = JwtTokenUtil.extractToken(authHeader);
        if (!JwtTokenUtil.verifyAuthToken(authToken, authRSAPublicKey, username)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User not authenticated for this operation.");
        }

        // Check access
        String accessToken = JwtTokenUtil.extractToken(accessHeader);
        if (!JwtTokenUtil.verifyAccessToken(accessToken, accessRSAPublicKey, Request.Type.GET)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User does not have the correct rights for this operation.");
        }

        var response = storageClient.getFile(username, path.substring(1));

        if (response == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("No response came from the storage server. It may be currently off.");
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());
        return new RestResponse(status).buildResponse(response.body());
    }

    @PutMapping("/cp/{username}")
    @Override
    public ResponseEntity<String> copy(@PathVariable String username, @RequestBody CopyRequest copyRequest, @RequestHeader("Authorization") String authHeader, @RequestHeader("Access") String accessHeader) {
        // Check authentication
        String authToken = JwtTokenUtil.extractToken(authHeader);
        if (!JwtTokenUtil.verifyAuthToken(authToken, authRSAPublicKey, username)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User not authenticated for this operation.");
        }

        // Check access
        String accessToken = JwtTokenUtil.extractToken(accessHeader);
        if (!JwtTokenUtil.verifyAccessToken(accessToken, accessRSAPublicKey, Request.Type.PUT)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User does not have the correct rights for this operation.");
        }

        var response = storageClient.copyFile(username, copyRequest);

        // Check if FStorage is on
        if (response == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("No response came from the storage server. It may be currently off.");
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());
        return new RestResponse(status).buildResponse(response.body());
    }

    @DeleteMapping("/rm/{username}/{*path}")
    @Override
    public ResponseEntity<String> remove(@PathVariable String username, @PathVariable String path, @RequestHeader("Authorization") String authHeader, @RequestHeader("Access") String accessHeader) {
        // Check authentication
        String authToken = JwtTokenUtil.extractToken(authHeader);
        if (!JwtTokenUtil.verifyAuthToken(authToken, authRSAPublicKey, username)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User not authenticated for this operation.");
        }

        // Check access
        String accessToken = JwtTokenUtil.extractToken(accessHeader);
        if (!JwtTokenUtil.verifyAccessToken(accessToken, accessRSAPublicKey, Request.Type.DELETE)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User does not have the correct rights for this operation.");
        }

        var response = storageClient.removeFile(username, path.substring(1));

        if (response == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("No response came from the storage server. It may be currently off.");
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());
        return new RestResponse(status).buildResponse(response.body());
    }

    @GetMapping("/file/{username}/{*path}")
    @Override
    public ResponseEntity<String> file(@PathVariable String username, @PathVariable String path, @RequestHeader("Authorization") String authHeader, @RequestHeader("Access") String accessHeader) {
        // Check authentication
        String authToken = JwtTokenUtil.extractToken(authHeader);
        if (!JwtTokenUtil.verifyAuthToken(authToken, authRSAPublicKey, username)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User not authenticated for this operation.");
        }

        // Check access
        String accessToken = JwtTokenUtil.extractToken(accessHeader);
        if (!JwtTokenUtil.verifyAccessToken(accessToken, accessRSAPublicKey, Request.Type.GET)) {
            return new RestResponse(HttpStatus.FORBIDDEN).buildResponse("User does not have the correct rights for this operation.");
        }

        var response = storageClient.fileProperties(username, path.substring(1));

        if (response == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("No response came from the storage server. It may be currently off.");
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());
        return new RestResponse(status).buildResponse(response.body());
    }

    @GetMapping("/access/{username}")
    private ResponseEntity<String> requestAccessToken(@PathVariable String username, @RequestHeader("Authorization") String authorizationHeader, @RequestHeader("Access") String accessHeader) {
        try {
            String authToken = JwtTokenUtil.extractToken(authorizationHeader);

            if (!JwtTokenUtil.verifyAuthToken(authToken, authRSAPublicKey, username))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authentication token");

            HttpResponse<String> responseEntity = accessClient.requestAccessControlToken(authToken, username);
            HttpStatus status = HttpStatus.resolve(responseEntity.statusCode());

            return new RestResponse(status).buildResponse(responseEntity.body());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
        }
    }

    /**
     * Makes a request to the FAuth module, seeking to obtain the RSA Public Key in order to be able to verify
     * tokens authenticity
     */
    private static void requestAuthRSAPublicKey() {
        try {
            var response = authClient.rsaPublicKeyExchange();

            // Check if there was a response
            if (response == null) {
                Shell.printError("Could not contact FAuth server.");
                return;
            }

            HttpStatus status = HttpStatus.resolve(response.statusCode());

            if (Objects.requireNonNull(status).is2xxSuccessful()) {
                SingleDataRequest key = SingleDataRequest.fromJsonString(response.body());
                authRSAPublicKey = UtilsBase.createPublicKey(key.data(), SIGNATURE_ALGORITHM);
            }
        } catch (NoSuchAlgorithmException e) {
            Shell.printDebug("No such algorithm for RSA signature.");
        } catch (InvalidKeySpecException e) {
            Shell.printError("Invalid key spec.");
        }
    }

    /**
     *  Makes a request to the FAccess module, seeking to obtain the RSA Public Key in order to be able to verify
     *  tokens authenticity
     */
    private static void requestAccessRSAPublicKey() {
        try {
            HttpResponse<String> responseEntity = accessClient.rsaPublicKeyExchange();
            HttpStatus status = HttpStatus.resolve(responseEntity.statusCode());

            if (Objects.requireNonNull(status).is2xxSuccessful()) {
                SingleDataRequest key = SingleDataRequest.fromJsonString(responseEntity.body());
                accessRSAPublicKey = UtilsBase.createPublicKey(key.data(), SIGNATURE_ALGORITHM);
            }
        } catch (NoSuchAlgorithmException e) {
            Shell.printError("No such algorithm for access signatures");
        } catch (InvalidKeySpecException e) {
            Shell.printError("Invalid key for access signatures");
        }
    }
}
