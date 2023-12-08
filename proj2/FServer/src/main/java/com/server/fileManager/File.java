package com.server.fileManager;

import java.io.InputStream;
import java.time.LocalDate;

/**
 * Class  File  represents a file in the file manager system.
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class File extends Directory{
    private final LocalDate creationDate;
    private LocalDate lastUpdateDate;
    private InputStream content;
    private String author;


    public File() {
        this.creationDate = LocalDate.now();
        this.lastUpdateDate = LocalDate.now();
    }

    /** Getters */
    public InputStream getContent() {
        return content;
    }
    public LocalDate getCreationDate() {
        return creationDate;
    }
    public LocalDate getLastUpdateDate() {
        return lastUpdateDate;
    }
    public String getAuthor() {
        return author;
    }

    /** Setters */
    public void setContent(InputStream content) {
        this.content = content;
    }
    public void setLastUpdateDate(LocalDate lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
    private void setAuthor(String author) {
        this.author = author;
    }

    public static class Builder {
        private final File file;

        public Builder() {
            this.file = new File();
        }

        public File build() {
            return file;
        }
        public Builder withName(String name){
            file.setName(name);
            return this;
        }
        public Builder withPath(String path) {
            file.setPath(path);
            return this;
        }
        public Builder withParent(Folder parent) {
            file.setParent(parent);
            parent.addFile(file);
            return this;
        }
        public  Builder withAuthor(String author){
            file.setAuthor(author);
            return this;
        }
        public Builder withContent(InputStream content) {
            file.setContent(content);
            return this;
        }
    }
}
