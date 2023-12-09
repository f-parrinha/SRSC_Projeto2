package com.server.fileManager;

import com.api.common.shell.Shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.stream.Collectors;

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
    private boolean isCopied;


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
    public boolean isCopied() {
        return isCopied;
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
    private void setIsCopied(boolean copied) {
        this.isCopied = copied;
    }

    public String listProperties() {
        String result = "Listing...\n\n";

        String contentText = new BufferedReader(
                new InputStreamReader(content, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        try {
            content.reset();
        } catch (IOException e) {
            Shell.printError("There was an IO problem while reading file's content.");
        }

        result = result.concat("\tName: " + name + "\n");
        result = result.concat("\tPath: " + (path == null || path.isEmpty() ? "(root)" : path) + "\n");
        result = result.concat("\tAuthor: " + author + "\n");
        result = result.concat("\tCreation date: " + creationDate.toString() + "\n");
        result = result.concat("\tLast update date: " + lastUpdateDate.toString() + "\n");
        result = result.concat("\tIs original: " + (!isCopied ? "yes" : "no") + "\n");
        result = result.concat("\tContent: " + contentText);
        return result;
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
        public Builder withIsCopied(boolean copied) {
            file.setIsCopied(copied);
            return this;
        }
    }
}
