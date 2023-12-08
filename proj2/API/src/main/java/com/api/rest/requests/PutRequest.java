package com.api.rest.requests;

import com.api.common.shell.Shell;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public record PutRequest(String path, String fileName, String encodedContent) implements Request<MkDirRequest> {
    public JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("path", path)
                .add("fileName", fileName)
                .add("encodedContent", encodedContent);

        return builder.build();
    }
}