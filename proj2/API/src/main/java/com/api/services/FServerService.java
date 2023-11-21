package com.api.services;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Interface  FServerService  describes the API for the FServer usage
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public interface FServerService {

    /**
     * Requests "login" command
     * @param username username to login
     * @param password correct password
     * @return Response (text)
     */
    Mono<ResponseEntity<String>> login(String username, String password);

    /**
     * Requests "ls" command
     * @param username who is requesting
     * @return response (text)
     */
    Mono<ResponseEntity<String>> listFiles(String username);


    /**
     * Requests "ls" command
     * @param username who is requesting
     * @param path directory path to list the files
     * @return response (text)
     */
    Mono<ResponseEntity<String>> listFiles(String username, String path);

    /**
     * Requests "mkdir" command
     * @param username who is requesting
     * @param path where to create the new directory
     * @return Response (text)
     */
    Mono<ResponseEntity<String>> makeDirectory(String username, String path);

    /**
     * Request "put" command
     * @param username who is requesting
     * @param path where the put the file
     * @param fileName the file name
     * @return Response (text)
     */
    Mono<ResponseEntity<String>> put(String username, String path, String fileName);

    /**
     * Request "get" command
     * @param username who is requesting
     * @param path where the put the file
     * @param fileName the file name
     * @return Response (text)
     */
    Mono<ResponseEntity<String>> get(String username, String path, String fileName);

    /**
     * Requests "cpy" command
     * @param username who is requesting
     * @param sourcePath source of what to copy
     * @param sourceFile file with the desired content
     * @param destPath where to create the new file
     * @param destFile the name of the new file
     * @return Response (text)
     */
    Mono<ResponseEntity<String>> copy(String username, String sourcePath, String sourceFile, String destPath, String destFile);

    /**
     * Requests "rm" command
     * @param username who is requesting
     * @param path path to file
     * @param fileName name of the file to be removed
     * @return Response (text)
     */
    Mono<ResponseEntity<String>> remove(String username, String path, String fileName);

    /**
     * Requests "file" command
     * @param username who is requesting
     * @param path path to file
     * @param fileName name of the file to inspect
     * @return Response (text)
     */
    Mono<ResponseEntity<String>> file(String username, String path, String fileName);
}
