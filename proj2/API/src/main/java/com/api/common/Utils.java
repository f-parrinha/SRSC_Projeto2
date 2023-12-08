package com.api.common;

import com.api.common.shell.Shell;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Class  Utils  offers utility functions that may be useful around certain scenarios in most classes
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class Utils {

    /**
     * Checks if a given path is invalid
     * @param path path to validate
     * @return true if invalid, false if valid
     */
    public static boolean isPathInvalid(String path) {
        boolean ends_with_slash = !path.isEmpty() && path.substring(path.length() - 1).contains("/");
        boolean starts_with_slash = !path.isEmpty() && path.substring(0, 1).contains("/");
        return path.contains("//") || ends_with_slash || starts_with_slash;
    }

    /**
     * From path tokens (folders), create a String with the full path
     * @param tokens set of ordered folders
     * @return String in a format like folder1/folder2/.../folderN
     */
    public static String createPathString(String[] tokens) {
        int idx = 0;
        String result = "";
        for (String path : tokens) {
            result = result.concat(path + (idx + 1 != tokens.length ? "/" : ""));
            idx++;
        }

        return result;
    }


    /**
     * Encodes a file's content into a Base64 String
     * @param content file's content
     * @return Base64 encoded object (String)
     */
    public static String encodeFileToJSON(InputStream content) {
        String result = null;

        try {
            byte[] contentBytes = content.readAllBytes();
            result = Base64.getEncoder().encodeToString(contentBytes);
        } catch (IOException e) {
            Shell.printError("Could not read content during file encoding.");
        }

        return result;
    }

    /**
     * Decodes a Base64 encoded file into an InputStream, to get the desired file's content
     * @param encodedContent the Base64 encoded string (representing the file's content)
     * @return File's content as an InputStream
     */
    public static InputStream decodeToFile(String encodedContent) {
        byte[] contentBytes = Base64.getDecoder().decode(encodedContent);

        return new ByteArrayInputStream(contentBytes);
    }
}
