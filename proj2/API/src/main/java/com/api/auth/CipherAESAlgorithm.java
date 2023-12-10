package com.api.auth;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;

public class CipherAESAlgorithm {
    public static final String AES = "AES";
    private static final String cipherAlgorithm = "AES/GCM/NoPadding";
    private static final String hashAlgorithm = "SHA-256";
    private SecretKey keyCipher;
    private final Cipher cipher;
    private final MessageDigest hash;


    public CipherAESAlgorithm() throws NoSuchPaddingException, NoSuchAlgorithmException {
        super();
        cipher = Cipher.getInstance(cipherAlgorithm);
        hash = MessageDigest.getInstance(hashAlgorithm);
    }

    private static GCMParameterSpec createGcmIvForAes(byte[] nonce) {

        byte[] ivBytes = Arrays.copyOf(nonce, 12);

        return new GCMParameterSpec(128, ivBytes);
    }

    private void initKeyCipher(byte[] sharedSecret) {
        keyCipher = new SecretKeySpec(sharedSecret, AES);
    }

    public byte[] encryptHashedData(byte[] hashedData, byte[] sharedSecret, byte[] secureRandom2) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        initKeyCipher(sharedSecret);
        GCMParameterSpec gcmParameterSpec = createGcmIvForAes(secureRandom2);

        cipher.init(Cipher.ENCRYPT_MODE, keyCipher, gcmParameterSpec);
        return cipher.doFinal(hashedData);
    }

    public byte[] decryptData(byte[] data, byte[] secureRandom, byte[] sharedSecret) throws InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        initKeyCipher(sharedSecret);
        GCMParameterSpec gcmParameterSpec = createGcmIvForAes(secureRandom);

        cipher.init(Cipher.DECRYPT_MODE, keyCipher, gcmParameterSpec);
        return cipher.doFinal(data);

    }

    public byte[] hashData(byte[] data) {
        return hash.digest(data);
    }


}
