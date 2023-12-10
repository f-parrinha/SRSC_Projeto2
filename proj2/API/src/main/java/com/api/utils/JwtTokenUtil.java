package com.api.utils;

import com.api.access.PermissionsType;
import com.api.requests.Request;
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

public class JwtTokenUtil {

    public static String createJwtToken(PrivateKey privateKey, String ... args) {
        Date creationTime = new Date();
        Date expirationTime = new Date(creationTime.getTime() + 3600000); // Valid for 1 hour
        String tokenID = UUID.randomUUID().toString();

        if(args.length == 2)
            return createToken(args[0], privateKey, args[1], expirationTime, tokenID);

        return createToken(args[0], privateKey, args[1], expirationTime, tokenID,  PermissionsType.fromString(args[2]));
    }

    public static boolean verifyJwtToken(String token, PublicKey publicKey, String ... arg) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        return arg.length == 0 ? verifyToken(token,publicKey, claimsSet) : verifyToken(token, publicKey, Request.Type.valueOf(arg[0]), claimsSet);
    }

    public static String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private static boolean verifyToken(String token, PublicKey publicKey, JWTClaimsSet claimsSet) {
        return verifySignature(token, publicKey)  && verifyTTL(claimsSet);
    }

    private static boolean verifyToken(String token, PublicKey publicKey, Request.Type requestType, JWTClaimsSet claimsSet) throws ParseException {
        return verifySignature(token, publicKey) && verifyTTL(claimsSet) && verifyAccess(claimsSet, requestType);
    }

    private static String createToken(String username, PrivateKey privateKey, String issuer, Date expirationTime, String idClaim) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(expirationTime)
                .claim("id", idClaim)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private static String createToken(String username, PrivateKey privateKey, String issuer, Date expirationTime, String idClaim,  PermissionsType accessLevel) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(expirationTime)
                .claim("id", idClaim)
                .claim("accessLevel", accessLevel.toString())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private static boolean verifySignature(String token, PublicKey publicKey) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);

            return signedJWT.verify(verifier);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean verifyTTL(JWTClaimsSet claimsSet) {

        Date expirationTime = claimsSet.getExpirationTime();

        if (expirationTime == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.before(expirationTime);
    }

    private static boolean verifyAccess(JWTClaimsSet claimsSet, Request.Type requestType) throws ParseException {

        PermissionsType accessLevel = PermissionsType.fromString(claimsSet.getStringClaim("accessLevel"));

        switch (requestType) {
            case GET:
                return accessLevel.canRead() || accessLevel.canWrite();
            case POST, PUT, DELETE:
                return accessLevel.canWrite();
            default:
                return false;
        }

    }



}
