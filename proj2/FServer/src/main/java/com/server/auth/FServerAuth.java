package com.server.auth;

import com.api.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class FServerAuth {

    private Map<String, User> users;

    public FServerAuth() throws IOException {
        this.users = new HashMap<>();
        initializeUsersDB();
        //users.put("martin", new User("martin", "m.magdalinchev@gamil.com", "Martin Krastev Magdalinchev","123456789", true));

    }

    public static void main(String[] args) {
        SpringApplication.run(FServerAuth.class, args);
    }

    private void initializeUsersDB() throws IOException {
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
                    String passwordHash = parts[3];
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
        }

    }

    @PostMapping("/authenticate")
    public boolean authenticateUser(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        if(users.containsKey(username)) {
            User user = users.get(username);
            return user.authenticate(password);
        }

        return false;
    }
}



