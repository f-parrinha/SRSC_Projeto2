package com.api.rest.requests;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public record CopyRequest(String sourcePath, String sourceFile, String destPath, String destFile) implements Request<CopyRequest> {
    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("sourcePath", sourcePath)
                .add("sourceFile", sourceFile)
                .add("destPath", destPath)
                .add("destFile", destFile);

        return builder.build();
    }


}
