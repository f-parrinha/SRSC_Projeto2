package com.fileManager;

import java.io.InputStream;

public class File {
    private final InputStream content;
    private final Directory parent;
    private final String path;
    private final String name;

    public File(String name, String path, Directory parent, InputStream content) {
        this.name = name;
        this.path = path;
        this.parent = parent;
        this.content = content;
    }

    public InputStream getContent() {
        return content;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Directory getParent() {
        return parent;
    }
}
