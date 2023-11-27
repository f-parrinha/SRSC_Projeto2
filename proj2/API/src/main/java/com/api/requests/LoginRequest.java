package com.api.requests;


import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public record LoginRequest(String username, String password) implements Request<LoginRequest> {
    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("username", username)
                .add("password", password);

        return builder.build();
    }
}
