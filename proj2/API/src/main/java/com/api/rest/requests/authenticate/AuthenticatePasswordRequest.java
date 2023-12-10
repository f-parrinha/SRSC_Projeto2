package com.api.rest.requests.authenticate;

import com.api.rest.requests.Request;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;
import java.util.Base64;

public record AuthenticatePasswordRequest(byte[] cipheredData, byte[] secureRandom, byte[] publicKey)
        implements Request<AuthenticatePasswordRequest> {

    @Override
    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("cipheredData", Base64.getEncoder().encodeToString(cipheredData))
                .add("secureRandom", Base64.getEncoder().encodeToString(secureRandom))
                .add("publicKey", Base64.getEncoder().encodeToString(publicKey));

        return builder.build();
    }

    public static AuthenticatePasswordRequest fromJsonString(String jsonString) {
        JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();
        return new AuthenticatePasswordRequest(
                Base64.getDecoder().decode(jsonObject.getString("cipheredData")),
                Base64.getDecoder().decode(jsonObject.getString("secureRandom")),
                Base64.getDecoder().decode(jsonObject.getString("publicKey"))
        );
    }
}

