package com.api;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class RSADigitalSignature {

    private KeyPair keyPair;
    private int KEY_SIZE = 2048;

    public RSADigitalSignature() throws NoSuchAlgorithmException {
        keyPair = generateRSAKeyPair();
    }

    private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(KEY_SIZE); // Adjust the key size based on your security requirements
        return keyPairGenerator.generateKeyPair();
    }

    public byte[] signMessage(byte[] message) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(message);
        return signature.sign();
    }

    public byte[] getPublicKey() {
        return keyPair.getPublic().getEncoded();
    }
}
