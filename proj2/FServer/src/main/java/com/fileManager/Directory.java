package com.fileManager;

public class Directory {

    /** Variables */
    private Directory[] siblings;
    private Directory parent;
    private Directory[] children;
    private String path;

    public Directory(String path) {
        initDirectory();
    }

    private void initDirectory() {

    }
}
