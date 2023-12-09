package com.server.fileManager;

public abstract class Directory {
    public static final String ROOT_DIR = ".";
    public enum Type {
        Folder,
        File;

        public static Type getType(Directory directory){
            return directory instanceof Folder ? Folder : File;
        }
        public static Type getType(Directory[] directory){
            return directory instanceof Folder[] ? Folder : File;
        }
    }
    protected String path;
    protected String name;
    protected Directory parent;

    public Directory() {
        this.parent = null;
        this.name = null;
        this.path = null;
    }

    public String getName() {
        return name;
    }
    public String getPath() {
        return path.isEmpty() ? ROOT_DIR : path;
    }
    public Directory getParent() {
        return parent;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public void setParent(Folder parent) {
        this.parent = parent;
    }
}
