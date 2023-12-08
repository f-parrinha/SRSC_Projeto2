package com.api.rest.requests;

import javax.json.JsonObject;
import java.io.IOException;

public interface Request<T> {
    enum Type {
        POST,
        GET,
        PUT,
        DELETE
    }

    /**
     * Serializes the request itself into a JSON object
     * @return Json object
     */
    JsonObject serialize() throws IOException;
}
