package com.server;

import com.api.*;
import com.api.common.UtilsBase;
import com.client.serviceClients.FAccessControlClient;
import com.client.serviceClients.FAuthClient;
import com.client.serviceClients.FStorageClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class  FServer  creates the main server (dispatcher server) for the FServer service
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 * @TODO Add the right functionalities. Right now it just prints the requests' info
 */
@SpringBootApplication
@RestController
public class FServer {
    private static final String SERVER_AUTH_URL = "http://localhost:8082";
    private static final String SERVER_ACCESS_URL = "http://localhost:8083";
    private static final String SERVER_STORAGE_URL = "http://localhost:8084";
    private byte[] secureRandom;
    private final FAuthClient authClient;
    private final FAccessControlClient accessControlClient;
    private final FStorageClient storageClient;
    private Map<String, String> validTokens;
    private SecureLogin secureLogin;
    private PublicKey rsaPublicKey;

    public FServer() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        this.authClient = new FAuthClient(SERVER_AUTH_URL);
        this.storageClient = new FStorageClient(SERVER_ACCESS_URL);
        this.accessControlClient = new FAccessControlClient(SERVER_STORAGE_URL);
        this.secureLogin = new SecureLogin();
        validTokens = new HashMap<>();
    }

    public static void main(String[] args) {
        SpringApplication.run(FServer.class, args);
    }

    @PostMapping("/init-connection")
    public Mono<ResponseEntity<AuthenticateUsernameResponse>> performDHKeyExchange(@RequestBody String username) {
        byte[] publicKey = secureLogin.getDHPublicKey();
        AuthenticateUsernameRequest request = new AuthenticateUsernameRequest(username.getBytes(), publicKey);
        return authClient.requestDHPublicKey(request)
                .flatMap(response -> {
                    AuthenticateUsernameResponse responseBody = response.getBody(); // Assuming the response type is a ResponseEntity<byte[]>

                    if (responseBody != null) {
                        return Mono.just(ResponseEntity.ok(responseBody));
                    } else {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                });
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody AuthenticatePasswordRequest loginRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        byte[] key = secureLogin.calculateKey(loginRequest.publicKey());
        secureRandom = loginRequest.secureRandom();
        secureLogin.generateDHSharedSecret(loginRequest.connectedPublicKey());
        AuthenticatePasswordRequest requestAuth = new AuthenticatePasswordRequest(loginRequest.cipheredData(), loginRequest.secureRandom(), loginRequest.publicKey(), key);
        return authClient.authenticateUser(requestAuth)
                .flatMap(response -> {
                    byte[] loggedIn = response.getBody();

                    if (loggedIn != null) {
                        try {
                            byte[] plainData = secureLogin.decryptData(loggedIn, secureRandom);
                            storeToken(plainData);
                        } catch (IOException | ClassNotFoundException | BadPaddingException | InvalidKeyException |
                                 NoSuchAlgorithmException | InvalidKeySpecException |
                                 InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Autenticacao do user realizada com sucesso");
                        return Mono.just(ResponseEntity.ok("Autenticacao do user realizada com sucesso: " + UtilsBase.bytesToLong(loggedIn)));
                    } else {
                        System.out.println("Nao foi possivel autenticar o user");
                        return Mono.just(ResponseEntity.ok("Nao foi possivel autenticar o user "));
                    }
                });
    }

    @GetMapping("/ls/{username}")
    public Mono<ResponseEntity<String>> listFiles(@PathVariable String username) {
        return Mono.just(ResponseEntity.ok("Lista de arquivos/diretórios para " + username));
    }

    @GetMapping("/ls/{username}/{path}")
    public Mono<ResponseEntity<String>> listFiles(@PathVariable String username, @PathVariable String path) {
        return Mono.just(ResponseEntity.ok("Lista de arquivos/diretórios para " + username + " no caminho " + path));
    }
import com.api.common.shell.StorePasswords;
import com.api.common.tls.TLSConfigFactory;
import com.api.common.tls.TLSServerConfig;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

public abstract class FServer {

    /** Constants */
    protected static final InputStream SERVER_CONFIG_FILE = FServer.class.getClassLoader().getResourceAsStream("servertls.conf");
    protected static final InputStream CLIENT_CONFIG_FILE = FServer.class.getClassLoader().getResourceAsStream("clienttls.conf");
    protected static final URI AUTH_URL = URI.create("https://localhost:8082");
    protected static final URI ACCESS_URL = URI.create("https://localhost:8083");
    protected static final URI STORAGE_URL = URI.create("https://localhost:8084");

    protected WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> createWebServerFactory(
            int port, String keyStorePath, String keyAlias, String trustStorePath, StorePasswords passwords) {
        return factory -> {
            try {
                TLSServerConfig tls = TLSConfigFactory.getInstance().forServer()
                        .withConfigFile(SERVER_CONFIG_FILE)
                        .withKeyStorePath(keyStorePath)
                        .withKeyStorePass(passwords.keyStorePass())
                        .withKeyAlias(keyAlias)
                        .withKeyPass(passwords.keyStorePass())
                        .withTrustStorePath(trustStorePath)
                        .withTrustStorePass(passwords.trustStorePass())
                        .build();

                factory.setSsl(tls.getSslContext());
                factory.setPort(port);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void storeToken(byte[] message) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {


        AuthenticatePasswordResponse resp = deserializeRecord(message);
        rsaPublicKey = createRSAPublicKey(resp.rsaPublicKey());

        if(verifySignature(rsaPublicKey, resp.plainData(), resp.signedData())){
            System.out.println("Signature verified successfully!");
            String token = new String(resp.plainData(), StandardCharsets.UTF_8);
            SecretKey secretKey = new SecretKeySpec(resp.secureRandom(), "HmacSHA256");

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String id = claims.get("id", String.class);
            String username = claims.getSubject();
            System.out.println("Token " + id + " belongs to " + username);

        }


        System.out.println("Deserialized Record: " + resp.signedData() + ", "
                + resp.secureRandom() + ", " + resp.rsaPublicKey());
    }

    public static AuthenticatePasswordResponse deserializeRecord(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(bis);
        return (AuthenticatePasswordResponse) in.readObject();
    }

    public PublicKey createRSAPublicKey(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(publicKeySpec);
    }
    public static boolean verifySignature(PublicKey publicKey, byte[] originalMessage, byte[] digitalSignature) {
        try {
            // Initialize Signature with the public key
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            // Update the Signature object with the original message
            signature.update(originalMessage);
            // Verify the signature with the provided digital signature
            return signature.verify(digitalSignature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
