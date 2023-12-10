package com.api.auth;

import com.api.utils.UtilsBase;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class DHAlgorithm {
    private static final String ALGORITHM = "DH";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int KEY_SIZE = 4096;
    private KeyPair dhKeyPair;
    private final KeyAgreement keyAgreement;
    private byte[] sharedSecret;

    public DHAlgorithm() throws NoSuchAlgorithmException, InvalidKeyException {
        this.keyAgreement = KeyAgreement.getInstance(ALGORITHM);
        generateDHKeys2();
    }

    /**
     * @return
     */
    public byte[] getPublicKey() {
        return dhKeyPair.getPublic().getEncoded();
    }

    /**
     * @return
     */
    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    /**
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private void generateDHKeys2() throws NoSuchAlgorithmException, InvalidKeyException {
        KeyPair kPair = UtilsBase.generateKeyPair(ALGORITHM, KEY_SIZE);
        keyAgreement.init(kPair.getPrivate());

        dhKeyPair = kPair;

    }

    /**
     * @param serverPublicKeyBytes
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     */
    public void calculateSharedKey(byte[] serverPublicKeyBytes) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {

        PublicKey serverPublicKey = UtilsBase.createPublicKey(serverPublicKeyBytes, ALGORITHM);
        keyAgreement.doPhase(serverPublicKey, true);

        MessageDigest hash = MessageDigest.getInstance(HASH_ALGORITHM);
        sharedSecret = hash.digest(keyAgreement.generateSecret());

    }

}
