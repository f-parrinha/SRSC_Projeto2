package com.api.requests.authenticate;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;
import java.util.Base64;

public record SuccessfullAuthenticationResponse(byte[] encryptedData) {
    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("encryptedData", Base64.getEncoder().encodeToString(encryptedData));

        return builder.build();
    }

    public static SuccessfullAuthenticationResponse fromJsonString(String jsonString) {
        JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();
        return new SuccessfullAuthenticationResponse(
                Base64.getDecoder().decode(jsonObject.getString("encryptedData"))
        );
    }
}
