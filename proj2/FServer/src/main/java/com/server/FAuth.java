package com.server;

import com.api.*;
import com.api.auth.SecureLogin;
import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.requests.AuthRSAPublicKey;
import com.api.requests.authenticate.*;
import com.api.User;
import com.api.services.AuthService;
import com.api.utils.JwtTokenUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@SpringBootApplication
@RestController
public class FAuth extends FServer implements AuthService<ResponseEntity<String>> {

    /**
     * Constants
     */
    public static final int PORT = 8082;
    public static final String KEYSTORE_PATH = "classpath:fauth-ks.jks";
    public static final String KEY_ALIAS = "fauth";
    public static final String TRUSTSTORE_PATH = "classpath:fauth-ts.jks";
    private RSADigitalSignature rsaDigitalSignature;
    private static Map<String, User> users;
    private static Map<String, SecureLogin> usersInLoginProcess;

    public static void main(String[] args) {
        SpringApplication.run(FAuth.class, args);
        initializeUsersDB();
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() throws NoSuchAlgorithmException {
        StorePasswords passwords = Shell.loadTrustKeyStoresPass();
        users = new HashMap<>();
        usersInLoginProcess = new HashMap<>();
        rsaDigitalSignature = new RSADigitalSignature();

        return createWebServerFactory(PORT, KEYSTORE_PATH, KEY_ALIAS, TRUSTSTORE_PATH, passwords);
    }

    @Override
    @GetMapping("/request-RSA-Key")
    public ResponseEntity<String> rsaPublicKeyExchange() {
        AuthRSAPublicKey key = new AuthRSAPublicKey(rsaDigitalSignature.getPublicKey());
        return new RestResponse(HttpStatus.OK).buildResponse(key.serialize().toString());
    }

    @PostMapping("/init-connection-auth")
    @Override
    public ResponseEntity<String> requestDHPublicKey(@RequestBody String request) {

        RequestKeyExchange clientRequest = RequestKeyExchange.fromJsonString(request);
        String username = clientRequest.username();

        System.out.println(username + " started authentication process...");

        if (!users.containsKey(username))
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("User " + username + " does not exist!");

        try {
            SecureLogin secureLogin = new SecureLogin();
            byte[] secureRandom = secureLogin.generateRandomBytes();

            AuthenticateUsernameResponse response = new AuthenticateUsernameResponse(secureRandom, secureLogin.getDHPublicKey());
            usersInLoginProcess.put(username, secureLogin);

            return new RestResponse(HttpStatus.OK).buildResponse(response.serialize().toString());

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            return new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR).buildResponse(e.toString());
        }
    }

    @PostMapping("/authenticate")
    @Override
    public ResponseEntity<String> authenticateUser(@RequestBody String stringLoginRequest) throws Exception {

        AuthenticatePasswordRequest loginRequest = AuthenticatePasswordRequest.fromJsonString(stringLoginRequest);
        SecureLogin secureLogin = usersInLoginProcess.get(loginRequest.username());

        byte[] hashedPWD = secureLogin.receiveLoginRequest(loginRequest);

        if (hashedPWD == null)
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("ATTENTION! Possible replay attack detected!");

        if (!users.get(loginRequest.username()).authenticate(Base64.getEncoder().encodeToString(hashedPWD)))
            return new RestResponse(HttpStatus.UNAUTHORIZED).buildResponse("Wrong password!");

        String token = JwtTokenUtil.createJwtToken(rsaDigitalSignature.getPrivateKey(), loginRequest.username(), "FAuth");
        String response = secureLogin.confirmSuccessfulLogin(token, loginRequest.secureRandom(), loginRequest.secureRandom());

        System.out.println("User " + loginRequest.username() + " authenticated successfully!");
        usersInLoginProcess.remove(loginRequest.username());

        return new RestResponse(HttpStatus.OK).buildResponse(response);

    }

    /**
     * Loads all users from a pre-defined file
     */
    private static void initializeUsersDB() {
        try {
            System.out.println("Reading of the file initialized.");

            // Load the auth.conf file using the ClassPathResource
            ClassPathResource resource = new ClassPathResource("auth.conf");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Parse each line and add users to the map
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Line: " + line);
                String[] parts = line.split(":");
                if (parts.length == 5) {
                    String username = parts[0].trim();
                    String email = parts[1].trim();
                    String name = parts[2].trim();
                    String passwordHash = hashPasswordSHA256(parts[3].trim());
                    boolean canAuthenticate = Boolean.parseBoolean(parts[4].trim());

                    User user = new User(username, email, name, passwordHash, canAuthenticate);
                    users.put(username, user);
                }
            }

            System.out.println("Reading finished");

            // Close the resources
            reader.close();
            inputStream.close();

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * Returns hashed password
     * @param password to be hashed
     * @return hashed password as a String
     */
    private static String hashPasswordSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to a hexadecimal string
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }


}
