package com.api.rest;


import com.api.common.shell.Shell;
import com.api.rest.requests.Request;
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
    public static final String ARGS_SIZE_VARS_MISMATCH_ERROR = "Warning. Args size and the number of variables are different.";

    public static final String MEDIA_TYPE = "application/json";
    private URI baseUri;

    /** Variables */
    private static RestRequest<?> request;
    private boolean debugOn;


    private RestRequest(URI baseUri) {
        this.baseUri = baseUri;
        this.debugOn = false;
    }
    private RestRequest(URI baseUri, boolean debugOn) {
        this.baseUri = baseUri;
        this.debugOn = debugOn;
    }

    /**
     * Returns the instance of RestRequest (with debug mode off as default)
     * @param baseUri base uri for the rquest (server uri)
     * @return RestRequest singleton
     */
    public static RestRequest<?> getInstance(URI baseUri) {
        if(request == null)
            request = new RestRequest<>(baseUri);
        request.setUri(baseUri);
        return request;
    }

    private void setUri(URI baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * Returns the instance of RestRequest (with custom debug mode)
     * @param baseUri base uri for the rquest (server uri)
     * @return RestRequest singleton
     */
    public static RestRequest<?> getInstance(URI baseUri, boolean debugOn) {
        if(request == null)
            request = new RestRequest<>(baseUri);
        request.setUri(baseUri);
        request.setDebugOn(debugOn);
        return request;
    }



    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    /**
     * Sends a GET request
     * @param url endpoint url
     * @param pathArgs args to the path variables
     * @return HttpRquest GET object
     */
    public HttpRequest get(String url) {
        URI uri = baseUri.resolve(processPathArgs(url));
        Shell.printDebug("Send REST request to '" + uri + "' with request type as '" + Request.Type.GET + "'");
        return HttpRequest.newBuilder()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE)
                .GET()
                .build();
    }

    /**
     * Sends a GET request
     * @param url endpoint url
     * @param pathArgs args to the path variables
     * @return HttpRquest GET object
     */
    public HttpRequest get(String url, String token, String ... pathArgs) {
        URI uri = baseUri.resolve(processPathArgs(url, pathArgs));
        Shell.printDebug("Send REST request to '" + uri + "' with request type as '" + Request.Type.GET + "'");
        return HttpRequest.newBuilder()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE)
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
    }

    /**
     * Sends a POST request containing a JSON object
     * @param url endpoint url
     * @param json json content
     * @param pathArgs args to the path variables
     * @return HttpRquest POST object
     */
    public HttpRequest post(String url, JsonObject json, String ... pathArgs){
        URI uri = baseUri.resolve(processPathArgs(url, pathArgs));
        Shell.printDebug("Send REST request to '" + uri + "' with request type as '" + Request.Type.POST + "'");
        return HttpRequest.newBuilder()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
    }

    /**
     * Sends a PUT request containing a JSON object
     * @param url endpoint url
     * @param json json content
     * @param pathArgs args to the path variables
     * @return HttpRquest PUT object
     */
    public HttpRequest put(String url, JsonObject json, String ... pathArgs){
        URI uri = baseUri.resolve(processPathArgs(url, pathArgs));
        Shell.printDebug("Send REST request to '" + uri + "' with request type as '" + Request.Type.PUT + "'");
        return HttpRequest.newBuilder()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE)
                .PUT(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
    }

    /**
     * Sends a DELETE request
     * @param url endpoint url
     * @param pathArgs args to the path variables
     * @return HttpRquest GET object
     */
    public HttpRequest delete(String url, String ... pathArgs) {
        URI uri = baseUri.resolve(processPathArgs(url, pathArgs));
        Shell.printDebug("Send REST request to '" + uri + "' with request type as '" + Request.Type.DELETE + "'");
        return HttpRequest.newBuilder()
                .uri(uri)
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
