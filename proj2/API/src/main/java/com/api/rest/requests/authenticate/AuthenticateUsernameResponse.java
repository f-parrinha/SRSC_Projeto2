package com.api.rest.requests.authenticate;

import com.api.requests.Request;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;
import java.util.Base64;

public record AuthenticateUsernameResponse(byte[] secureRandom, byte[] publicKey)
        implements Request<AuthenticateUsernameResponse> {

    @Override
    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("secureRandom", Base64.getEncoder().encodeToString(secureRandom))
                .add("publicKey", Base64.getEncoder().encodeToString(publicKey));
        return builder.build();
    }


    public static AuthenticateUsernameResponse fromJsonString(String jsonString) {
        JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();


        return new AuthenticateUsernameResponse(
                Base64.getDecoder().decode(jsonObject.getString("secureRandom")),
                Base64.getDecoder().decode(jsonObject.getString("publicKey"))
        );
    }


}

