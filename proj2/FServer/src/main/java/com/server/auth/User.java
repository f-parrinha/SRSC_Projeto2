package com.server.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {

    private String username;
    private String email;
    private String name;
    private String passwordHash;
    private boolean canAuthenticate;
    private MessageDigest hash;

    public User(String username, String email, String name, String passwordHash, boolean canAuthenticate) throws NoSuchAlgorithmException {
        this.username = username;
        this.email = email;
        this.name = name;
        this.passwordHash = passwordHash;
        this.canAuthenticate = canAuthenticate;
        hash = MessageDigest.getInstance("SHA-256");
    }

    public boolean authenticate(String password) {
        return password.equals(passwordHash) && canAuthenticate;
    }

}

