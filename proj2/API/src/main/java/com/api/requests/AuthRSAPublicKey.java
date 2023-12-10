package com.api.requests;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;
import java.util.Base64;

public record AuthRSAPublicKey(byte[] key) {

    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("key", Base64.getEncoder().encodeToString(key));

        return builder.build();
    }

    public static AuthRSAPublicKey fromJsonString(String jsonString) {
        JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();
        return new AuthRSAPublicKey(
                Base64.getDecoder().decode(jsonObject.getString("key"))
        );
    }
}
