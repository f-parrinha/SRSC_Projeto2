package com.api.rest.requests;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public record MkDirRequest(String path) implements Request<MkDirRequest> {
    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("path", path);

        return builder.build();
    }
}
