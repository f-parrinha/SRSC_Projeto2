package com.api.services;

import com.api.requests.MkDirRequest;

import java.io.IOException;

public interface StorageService<T> {

    T createDirectory(String username, MkDirRequest mkDirRequest) throws IOException, InterruptedException;

    /** @TODO */
    T getFile(String username, String path, String file) throws IOException, InterruptedException;

    /** @TODO */
    T createFile();

    /** @TODO */
    T removeFile();

    /** @TODO */
    T listContent();

    /** @TODO */
    T copyFile();
}
