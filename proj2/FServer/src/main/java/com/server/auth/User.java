package com.server.auth;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String username;
    private String email;
    private String name;
    private String passwordHash;
    private boolean canAuthenticate;

    public User(String username, String email, String name, String passwordHash, boolean canAuthenticate) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.passwordHash = passwordHash;
        this.canAuthenticate = canAuthenticate;
    }

    public boolean authenticate(String password) {
        return this.passwordHash.equals(password) && canAuthenticate;
    }
}

