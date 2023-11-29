package com.fileManager;

public class FileManager {

    private Directory rootDirectory;

    public FileManager(){
        this.rootDirectory = null;
    }

    public FileManager(Directory rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public Directory getRootDirectory() {
        return rootDirectory;
    }
}
