package com.server.auth;

import com.api.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;

import com.api.common.UtilsBase;


@SpringBootApplication
@RestController
public class FServerAuth {
    private RSADigitalSignature rsaDigitalSignature;
    private SecureLogin secureLogin;
    private static Map<String, User> users;
    private static Map<byte[], String> usersInLoginProcess;

    public FServerAuth() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        users = new HashMap<>();
        usersInLoginProcess = new HashMap<>();
        secureLogin = new SecureLogin();
        rsaDigitalSignature = new RSADigitalSignature();
    }

    public static void main(String[] args) {
        SpringApplication.run(FServerAuth.class, args);
        initializeUsersDB();
    }

    @PostMapping("/authenticate")
    public byte[] authenticateUser(@RequestBody AuthenticatePasswordRequest loginRequest) throws Exception {
        //TODO: Retornar "{AssinaturaAServer ( Ktoken1024 ) || SecureRandom2+1}Ks"
        byte[] credentialsWithRandom = secureLogin.receiveLoginRequest(loginRequest);
        byte[] hashedPWD = Arrays.copyOfRange(credentialsWithRandom, 0, credentialsWithRandom.length-16);
        byte[] secondSecureRandom = Arrays.copyOfRange(credentialsWithRandom, 16, credentialsWithRandom.length);

        byte[] incrementedRandom = UtilsBase.incrementByteArray(secondSecureRandom);
        String username = usersInLoginProcess.get(secureLogin.getSecureRandom());

        if(users.get(username).authenticate(Base64.getEncoder().encodeToString(hashedPWD))) {
            String token = createToken(username, incrementedRandom);
            byte[] signedToken = signToken(token);
            byte[] loginConfirmed = secureLogin.confirmSuccessfulLogin(signedToken, token.getBytes(), loginRequest.secureRandom(), incrementedRandom, rsaDigitalSignature.getPublicKey());
            System.out.println("User authenticated successfully! Sending token: " + loginConfirmed.toString());
            return loginConfirmed;
        }
        return null;
    }

    /**
     *
     * @param request
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     */
    @PostMapping("/init-connection-auth")
    public AuthenticateUsernameResponse requestDHPublicKey(@RequestBody AuthenticateUsernameRequest request) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {

        if (users.containsKey(new String(request.username(), StandardCharsets.UTF_8))) {
            System.out.println("User " + new String(request.username(), StandardCharsets.UTF_8) + " found! Establishing connection...");

            byte[] key = secureLogin.calculateKey(request.publicKey());
            byte[] secureRandom = secureLogin.generateRandomBytes();
            AuthenticateUsernameResponse response = new AuthenticateUsernameResponse(secureRandom, secureLogin.getDHPublicKey(), key);
            // TODO: check
            usersInLoginProcess.put(secureRandom, new String(request.username(), StandardCharsets.UTF_8));

            //byte[] payload = UtilsBase.concatArrays(secureRandom, secureLogin.getDHPublicKey());

            return response;
        }
        System.out.println("User " + new String(request.username(), StandardCharsets.UTF_8) + " does not exists!");
        return null;
    }

    private static void initializeUsersDB() {
        try {
            System.out.println("Reading of the file initialized.");
            // Read the credentials file
            BufferedReader reader = new BufferedReader(new FileReader("/home/martin/Documents/SRSC/SRSC_Projeto2/proj2/FServer/src/main/java/com/server/auth/credentials"));

            // Parse each line and add users to the map
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println("Line: " + line);
                String[] parts = line.split(":");
                if (parts.length == 5) {
                    String username = parts[0];
                    String email = parts[1];
                    String name = parts[2];
                    String passwordHash = hashPasswordSHA256(parts[3]);
                    boolean canAuthenticate = Boolean.parseBoolean(parts[4]);

                    User user = new User(username, email, name, passwordHash, canAuthenticate);
                    users.put(username, user);
                }
            }

            System.out.println("Reading finished");
            // Close the reader
            reader.close();
        } catch (IOException e) {
            // Handle exception (e.g., log it)
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    private static String hashPasswordSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to a hexadecimal string
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); // Handle the exception based on your application's needs
            return null;
        }
    }

    public static String createToken(String username, byte[] keyBytes) {
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        Date creationTime = new Date();
        Date expirationTime = new Date(creationTime.getTime() + 3600000); // Valido para 1 hora
        String tokenID = UUID.randomUUID().toString();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(creationTime)
                .setNotBefore(creationTime)
                .setExpiration(expirationTime)
                .claim("id", tokenID)
                .signWith(secretKey)
                .compact();
    }
    private byte[] signToken(String token) throws Exception {
        return rsaDigitalSignature.signMessage(token.getBytes(StandardCharsets.UTF_8));
    }

}