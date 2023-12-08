package com.api.services;

import com.api.rest.requests.MkDirRequest;
import com.api.rest.requests.PutRequest;

public interface StorageService<T> {

    /**
     * Lists a user's folders and files at the root directory
     * @param username owner
     * @return set of directories (Folders and Files)
     */
    T listDirectories(String username);

    /**
     * Lists a user's folders and files at a given path
     * @param username owner
     * @param path path to list
     * @return set of directories (Folders and Files)
     */
    T listDirectories(String username, String path);

    /**
     * Creates a new folder
     * @param username owner
     * @param mkDirRequest mkdir command request
     * @return new folder
     */
    T createFolder(String username, MkDirRequest mkDirRequest);

    /**
     * Uploads a new file
     * @param username owner
     * @param putRequest mkdir command request
     * @return new file
     */
    T  createFile(String username, PutRequest putRequest);

    /**
     * Downloads a user's file at the given path
     * @param username owner
     * @param path path to file
     * @return file to download
     */
    T getFile(String username, String path);

    /**
     * Removes a file at a given path
     * @param username owner
     * @param path path to file to remove
     * @return removed file
     */
    T removeFile(String username, String path);

    /** @TODO */
    T copyFile();
}
