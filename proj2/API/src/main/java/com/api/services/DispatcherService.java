package com.api.services;

import com.api.rest.requests.CopyRequest;
import com.api.rest.requests.LoginRequest;
import com.api.rest.requests.MkDirRequest;
import com.api.rest.requests.PutRequest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

/**
 * Interface  FServerService  describes the API for the FServer usage
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public interface DispatcherService<T> {

    /**
     * Requests key exchange to initiate login process
     *
     * @param requestKeyExchange the request to be sent
     * @return Response (text)
     */
    T requestDHPublicKey(String requestKeyExchange) throws IOException, InterruptedException;

    /**
     * Requests "login" command
     *
     * @param loginRequest the login request to be sent
     * @return Response (text)
     */
    T login(String arg1, String arg2) throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, ParseException;

    /**
     * Requests "ls" command
     * @param username who is requesting
     * @return response (text)
     */
    T listFiles(String username) ;


    /**
     * Requests "ls" command
     * @param username who is requesting
     * @param path     directory path to list the files
     * @return response (text)
     */
    T listFiles(String username, String path) ;

    /**
     * Requests "mkdir" command
     * @param username who is requesting
     * @param mkDirRequest record containing mandatory values for the folder creation on the FServer's FileManager
     * @return Response (text)
     */
    T makeDirectory(String username, MkDirRequest mkDirRequest) ;

    /**
     * Request "put" command
     * @param username who is requesting
     * @param request contains parameters used for the file creation on the FServer's FileManager (path, name, content)
     * @return Response (text)
     */
    T put(String username, PutRequest request) ;

    /**
     * Request "get" command
     * @param username who is requesting
     * @param path     where the get the file
     * @return Response (text)
     */
    T get(String username, String path) ;

    /**
     * Requests "cpy" command
     * @param username   who is requesting
     * @return Response (text)
     */
    T copy(String username, CopyRequest copyRequest) ;

    /**
     * Requests "rm" command
     * @param username who is requesting
     * @param path     path to file
     * @return Response (text)
     */
    T remove(String username, String path) ;

    /**
     * Requests "file" command
     * @param username who is requesting
     * @param path     path to file
     * @return Response (text)
     */
    T file(String username, String path) ;
}