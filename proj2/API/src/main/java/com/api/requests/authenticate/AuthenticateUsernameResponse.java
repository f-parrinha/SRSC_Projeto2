package com.api.requests.authenticate;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;
import java.util.Base64;

public record AuthenticateUsernameResponse(byte[] secureRandom, byte[] publicKey) {
    public static AuthenticateUsernameResponse fromJsonString(String jsonString) {
        System.out.println("JSON String: " + jsonString);
        JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();

        String secureRandomStr = jsonObject.getJsonString("secureRandom").getString();
        String publicKeyStr = jsonObject.getJsonString("publicKey").getString();

        return new AuthenticateUsernameResponse(
                Base64.getDecoder().decode(secureRandomStr),
                Base64.getDecoder().decode(publicKeyStr)
        );
    }

    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("secureRandom", Base64.getEncoder().encodeToString(secureRandom))
                .add("publicKey", Base64.getEncoder().encodeToString(publicKey));
        return builder.build();
    }
}

