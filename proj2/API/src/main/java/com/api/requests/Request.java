package com.api.requests;

import javax.json.JsonObject;

public interface Request<T> {

    /**
     * Serializes the request itself into a JSON object
     * @return Json object
     */
    JsonObject serialize();
}
