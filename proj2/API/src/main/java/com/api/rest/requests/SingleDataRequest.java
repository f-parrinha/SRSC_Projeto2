package com.api.rest.requests;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;
import java.util.Base64;

public record SingleDataRequest(byte[] data) implements Request<SingleDataRequest>{

    @Override
    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("data", Base64.getEncoder().encodeToString(data));

        return builder.build();
    }

    public static SingleDataRequest fromJsonString(String jsonString) {
        JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();
        return new SingleDataRequest(
                Base64.getDecoder().decode(jsonObject.getString("data"))
        );
    }
}
