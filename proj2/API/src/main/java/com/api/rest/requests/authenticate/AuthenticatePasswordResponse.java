package com.api.requests.authenticate;

import com.api.requests.Request;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;
import java.util.Base64;

public record AuthenticatePasswordResponse(String token, byte[] secureRandom)
        implements Request<AuthenticatePasswordResponse> {
    @Override
    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("token", token)
                .add("secureRandom", Base64.getEncoder().encodeToString(secureRandom));
        return builder.build();
    }

    public static AuthenticatePasswordResponse fromJsonString(String jsonString) {
        JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();
        return new AuthenticatePasswordResponse(
                jsonObject.getString("token"),
                Base64.getDecoder().decode(jsonObject.getString("secureRandom"))
        );
    }


}
