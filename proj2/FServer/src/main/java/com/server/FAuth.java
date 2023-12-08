package com.server;

import com.api.*;
import com.api.auth.SecureLogin;
import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.rest.requests.SingleDataRequest;
import com.api.rest.requests.authenticate.*;
import com.api.User;
import com.api.services.AuthService;
import com.api.services.AuthService;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@SpringBootApplication
@RestController
public class FAuth extends FServer implements AuthService<ResponseEntity<String>> {

    /** Constants */
    public static final int PORT = 8082;
    public static final String KEYSTORE_PATH = "classpath:fauth-ks.jks";
    public static final String KEY_ALIAS = "fauth";
    public static final String TRUSTSTORE_PATH = "classpath:fauth-ts.jks";
    private static String[] args;
    private static final int KEY_SIZE = 2048;
    private static final String SIGNATURE_ALGORITHM = "RSA";
    private KeyPair rsaKeyPair;
    private static Map<String, User> users;
    private static Map<String, SecureLogin> usersInLoginProcess;

    public static void main(String[] args) {
        SpringApplication.run(FAuth.class, args);
        initializeUsersDB();
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() {
        StorePasswords passwords = Shell.loadTrustKeyStoresPass(args);
        users = new HashMap<>();
        usersInLoginProcess = new HashMap<>();
        rsaKeyPair = UtilsBase.generateKeyPair(SIGNATURE_ALGORITHM, KEY_SIZE);

        return createWebServerFactory(PORT, KEYSTORE_PATH, KEY_ALIAS, TRUSTSTORE_PATH, passwords);
    }

    @Override
    @GetMapping("/request-RSA-Key")
    public ResponseEntity<String> rsaPublicKeyExchange() {
        SingleDataRequest key = new SingleDataRequest(rsaKeyPair.getPublic().getEncoded());
        return new RestResponse(HttpStatus.OK).buildResponse(key.serialize().toString());
    }

    @GetMapping("/auth/init-connection/{username}")
    @Override
    public ResponseEntity<String> requestDHPublicKey(@PathVariable String username) {

        System.out.println(username + " started authentication process...");

        if (!users.containsKey(username))
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("User " + username + " does not exist!");

        try {
            SecureLogin secureLogin = new SecureLogin();
            secureLogin.generateRandomBytes();

            AuthenticateUsernameResponse response = new AuthenticateUsernameResponse(secureLogin.getSecureRandom(), secureLogin.getDHPublicKey());
            usersInLoginProcess.put(username, secureLogin);

            return new RestResponse(HttpStatus.OK).buildResponse(response.serialize().toString());

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            return new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR).buildResponse(e.toString());
        }
    }

    @PostMapping("/auth/login/{username}")
    @Override
    public ResponseEntity<String> authenticateUser(@RequestBody String stringLoginRequest, @PathVariable String username) throws Exception {

        AuthenticatePasswordRequest loginRequest = AuthenticatePasswordRequest.fromJsonString(stringLoginRequest);
        SecureLogin secureLogin = usersInLoginProcess.get(username);

        byte[] hashedPWD = secureLogin.receiveLoginRequest(loginRequest);

        if (hashedPWD == null)
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("ATTENTION! Possible replay attack detected!");

        if (!users.get(username).authenticate(Base64.getEncoder().encodeToString(hashedPWD)))
            return new RestResponse(HttpStatus.UNAUTHORIZED).buildResponse("Wrong password!");

        String token = JwtTokenUtil.createJwtToken(rsaKeyPair.getPrivate(), username, "FAuth");
        String response = secureLogin.confirmSuccessfulLogin(token, loginRequest.secureRandom(), loginRequest.secureRandom());

        System.out.println("User " + username + " authenticated successfully!");
        usersInLoginProcess.remove(username);

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
