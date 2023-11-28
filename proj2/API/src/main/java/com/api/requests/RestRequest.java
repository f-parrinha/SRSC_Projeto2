package com.api.requests;


import com.api.common.shell.Shell;
import org.springframework.http.HttpHeaders;

import javax.json.JsonObject;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class  RestRquest  creates a singleton used for RESTful requests
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class RestRequest<T> {

    /** Constants */
    public static final String ARGS_SIZE_VARS_MISMATCH_ERROR = "Warning. Args size and the number of variables are different";

    public static final String MEDIA_TYPE = "application/json";
    private final URI baseUri;

    /** Variables */
    private static RestRequest<?> request;


    private RestRequest(URI baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * Returns the instance of RestRequest
     * @param baseUri base uri for the rquest (server uri)
     * @return RestRequest singleton
     */
    public static RestRequest<?> getInstance(URI baseUri) {
        request = request == null ? new RestRequest<>(baseUri) : request;
        return request;
    }

    /**
     * Sends a GET request
     * @param uri endpoint uri
     * @param pathArgs args to the path variables
     * @return HttpRquest GET object
     */
    public HttpRequest get(String uri, String ... pathArgs) {
        return HttpRequest.newBuilder()
                .uri(baseUri.resolve(processPathArgs(uri, pathArgs)))
                .header(HttpHeaders.ACCEPT, MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE)
                .GET()
                .build();
    }

    /**
     * Sends a POST request containing a JSON object
     * @param uri endpoint uri
     * @param json json content
     * @param pathArgs args to the path variables
     * @return HttpRquest POST object
     */
    public HttpRequest post(String uri, JsonObject json, String ... pathArgs){
        return HttpRequest.newBuilder()
                .uri(baseUri.resolve(processPathArgs(uri, pathArgs)))
                .header(HttpHeaders.ACCEPT, MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
    }

    /**
     * Sends a PUT request containing a JSON object
     * @param uri endpoint uri
     * @param json json content
     * @param pathArgs args to the path variables
     * @return HttpRquest PUT object
     */
    public HttpRequest put(String uri, JsonObject json, String ... pathArgs){
        return HttpRequest.newBuilder()
                .uri(baseUri.resolve(processPathArgs(uri, pathArgs)))
                .header(HttpHeaders.ACCEPT, MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE)
                .PUT(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
    }

    /**
     * Sends a DELETE request
     * @param uri endpoint uri
     * @param pathArgs args to the path variables
     * @return HttpRquest GET object
     */
    public HttpRequest delete(String uri, String ... pathArgs) {
        return HttpRequest.newBuilder()
                .uri(baseUri.resolve(processPathArgs(uri, pathArgs)))
                .header(HttpHeaders.ACCEPT, MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE)
                .DELETE()
                .build();
    }

    private String processPathArgs(String uri, String ... pathArgs) {
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(uri);
        int argIdx = 0;
        int lastIdx = 0;

        // Check Exception
        if (matcher.results().count()!= pathArgs.length) {
            Shell.printError(ARGS_SIZE_VARS_MISMATCH_ERROR);
            return uri;
        }

        // Process ..
        matcher.reset();
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            result.append(uri, lastIdx, matcher.start());
            String value = pathArgs[argIdx++];
            result.append(value);
            lastIdx = matcher.end();
        }

        result.append(uri.substring(lastIdx));
        return result.toString();
    }
}
