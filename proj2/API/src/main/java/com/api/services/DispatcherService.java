package com.api.services;

import com.api.requests.CopyRequest;
import com.api.requests.LoginRequest;
import com.api.requests.MkDirRequest;

import java.io.IOException;

/**
 * Interface  FServerService  describes the API for the FServer usage
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public interface DispatcherService<T> {

    /**
     * Requests "login" command
     *
     * @param username username to login
     * @param password correct password
     * @return Response (text)
     */
    T login(LoginRequest loginRequest) throws IOException, InterruptedException;

    /**
     * Requests "ls" command
     *
     * @param username who is requesting
     * @return response (text)
     */
    T listFiles(String username) throws IOException, InterruptedException;


    /**
     * Requests "ls" command
     *
     * @param username who is requesting
     * @param path     directory path to list the files
     * @return response (text)
     */
    T listFiles(String username, String path) throws IOException, InterruptedException;

    /**
     * Requests "mkdir" command
     *
     * @param username who is requesting
     * @param path     where to create the new directory
     * @return Response (text)
     */
    T makeDirectory(String username, MkDirRequest mkDirRequest) throws IOException, InterruptedException;

    /**
     * Request "put" command
     *
     * @param username who is requesting
     * @param path     where the put the file
     * @param fileName the file name
     * @return Response (text)
     */
    T put(String username, String path, String fileName);

    /**
     * Request "get" command
     *
     * @param username who is requesting
     * @param path     where the put the file
     * @param fileName the file name
     * @return Response (text)
     */
    T get(String username, String path, String fileName);

    /**
     * Requests "cpy" command
     *
     * @param username   who is requesting
     * @param sourcePath source of what to copy
     * @param sourceFile file with the desired content
     * @param destPath   where to create the new file
     * @param destFile   the name of the new file
     * @return Response (text)
     */
    T copy(String username, CopyRequest copyRequest) throws IOException, InterruptedException;

    /**
     * Requests "rm" command
     *
     * @param username who is requesting
     * @param path     path to file
     * @param fileName name of the file to be removed
     * @return Response (text)
     */
    T remove(String username, String path, String fileName) throws IOException, InterruptedException;

    /**
     * Requests "file" command
     *
     * @param username who is requesting
     * @param path     path to file
     * @param fileName name of the file to inspect
     * @return Response (text)
     */
    T file(String username, String path, String fileName) throws IOException, InterruptedException;
}