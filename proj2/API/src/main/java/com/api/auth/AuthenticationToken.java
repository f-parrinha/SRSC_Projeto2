package com.api.auth;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

public class AuthenticationToken {

    /**
     *
     * @param username
     * @param secretKey
     * @return
     */
    public static String createToken(String username, PrivateKey secretKey) {
        Date creationTime = new Date();
        Date expirationTime = new Date(creationTime.getTime() + 3600000); // Valido para 1 hora
        String tokenID = UUID.randomUUID().toString();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(creationTime)
                .setNotBefore(creationTime)
                .setExpiration(expirationTime)
                .claim("id", tokenID)
                .signWith(secretKey, SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     *
     * @param token
     * @param publicKey
     * @return
     * @throws ParseException
     */
    public static boolean verifyToken(String token, PublicKey publicKey) throws ParseException {
        return verifySignature(token, publicKey)  && verifyTTL(token);
    }

    /**
     *
     * @param token
     * @param publicKey
     * @return
     */
    private static boolean verifySignature(String token, PublicKey publicKey) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);

            return signedJWT.verify(verifier);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws ParseException
     */
    private static boolean verifyTTL(String token) throws ParseException {

        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        Date expirationTime = claimsSet.getExpirationTime();

        if (expirationTime == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.before(expirationTime);
    }

    public static String extractToken(String authorizationHeader) {

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
