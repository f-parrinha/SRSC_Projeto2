package com.api.requests.authenticate;

import com.api.requests.Request;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;

public record RequestKeyExchange(String username) implements Request<RequestKeyExchange> {
    @Override
    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("username", username);

        return builder.build();
    }

    public static RequestKeyExchange fromJsonString(String jsonString) {
        JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();
        return new RequestKeyExchange(
                jsonObject.getString("username")
        );
    }
}
