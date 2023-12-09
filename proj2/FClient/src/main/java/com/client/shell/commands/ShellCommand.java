package com.client.shell.commands;

import com.client.serviceClients.FDispatcherClient;

/**
 * Abstract Class  ShellCommand  defines the basics of command ran by the shell.
 * It follows the "Command Pattern" design pattern
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public abstract class ShellCommand {

    /** Constants */
    public static final String SEARCH_FOLDER = "SEARCH_FOLDER";
    public static final String SEARCH_FILE = "SEARCH_FILE";
    private static final char SLASH_CHAR = '/';

    /** Variables */
    protected FDispatcherClient client;


    /**
     * Constructor
     * @param client is an FClient
     */
    public ShellCommand(FDispatcherClient client) {
        this.client = client;
    }

    /**
     * Receives a string containing both path and file descriptions and separates them into two strings
     * @param path_file path/filename string
     * @return (path, file)
     */
    protected String[] seperatePathAndFile(String path_file) {
        if (!path_file.contains(String.valueOf(SLASH_CHAR))){
            return  new String[] { "", path_file };
        }

        // Contains a '/', which means there is a 'path' to the 'file'
        char[] pathFileAsChar = path_file.toCharArray();
        int splitIdx = pathFileAsChar.length - 1;     // Start at the end

        // Find for the first '/', counting from the end, getting the file name
        while (splitIdx > 0 && pathFileAsChar[splitIdx] != SLASH_CHAR){
            splitIdx--;
        }

        String path = path_file.substring(0, splitIdx);
        String file = path_file.substring(splitIdx+1);
        return new String[] { path, file };
    }
}
