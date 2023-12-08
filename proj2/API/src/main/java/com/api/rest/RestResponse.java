package com.api.rest;

import com.api.common.shell.Shell;
import com.api.rest.requests.Request;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Objects;

public class RestResponse {

    /** Constants */
    public static final String OK_MESSAGE = "200 -> ";
    public static final String NOT_FOUND_MESSAGE = "404 -> ";
    public static final String FORBIDDEN_MESSAGE = "403 -> ";
    public static final String BAD_REQUEST_MESSAGE = "400 -> ";
    public static final String INTERNAL_ERROR_MESSAGE = "500 -> ";
    private static final String CONFLICT_MESSAGE = "409 -> ";
    public static final String WRONG_STATUS = "Unexpected HTTP status value. ";
    public static final String NO_STATUS_GIVEN = "No HTTP status was given.";


    /** Variables */
    private final HttpStatus status;

    public RestResponse(HttpStatus status) {
        this.status = status;
    }


    /**
     * Checks if there is a HttpStatus object
     * @return true if exists, false if not
     */
    public boolean checkStatusExists(HttpStatus status){
        return status != null;
    }


    /**
     * Builds a final Response (server side)
     * @return a server ResponseEntity (Spring's)
     */
    public ResponseEntity<String> buildResponse(String content){
        if(!checkStatusExists(this.status)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR_MESSAGE + NO_STATUS_GIVEN);
        }

        Shell.printDebug("Creating REST response '" + content + "'");
        String body = statusToString(status, content);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Gets the status of a request and converts to String. Adds user defined content on OK status.
     * @param status HttpRequest status
     * @param content user additional content
     * @return Status as String
     */
    public String statusToString(HttpStatus status, String content) {
        switch (Objects.requireNonNull(status)) {
            case OK -> {
                return (content.contains(OK_MESSAGE) ? "" : OK_MESSAGE) + content;
            }
            case NOT_FOUND -> {
                return (content.contains(NOT_FOUND_MESSAGE) ? "" : NOT_FOUND_MESSAGE) + content;
            }
            case BAD_REQUEST -> {
                return (content.contains(BAD_REQUEST_MESSAGE) ? "" : BAD_REQUEST_MESSAGE) + content;
            }
            case FORBIDDEN -> {
                return (content.contains(FORBIDDEN_MESSAGE) ? "" : FORBIDDEN_MESSAGE) + content;
            }
            case INTERNAL_SERVER_ERROR -> {
                return (content.contains(INTERNAL_ERROR_MESSAGE) ? "" : INTERNAL_ERROR_MESSAGE) + content;
            }
            case CONFLICT -> {
                return (content.contains(CONFLICT_MESSAGE) ? "" : CONFLICT_MESSAGE) + content;
            }
            default -> {
                return (content.contains(WRONG_STATUS) ? "" : WRONG_STATUS) + content;
            }
        }
    }
}
