package com.api.requests;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;

public record LoginRequest(String username, String password) implements Request<LoginRequest> {
    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("username", username)
                .add("password", password);

        return builder.build();
    }

    public static LoginRequest fromJsonString(String jsonString) {
        JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();
        return new LoginRequest(
                jsonObject.getString("username"),
                jsonObject.getString("password"));
    }
}
