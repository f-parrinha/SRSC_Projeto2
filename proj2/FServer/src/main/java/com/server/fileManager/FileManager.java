package com.server.fileManager;

import java.io.InputStream;
import java.util.Arrays;
import com.api.common.Utils;
import com.api.common.shell.Shell;

/**
 * Class  FileManager  manages files and directories
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class FileManager {
    public static final String ROOT = "root";
    public static final String DIR_DIVIDER =  "/";

    private final Folder rootDirectory;

    public FileManager(){
        this.rootDirectory = new Folder.Builder()
                .withName(ROOT)
                .withPath(ROOT)
                .build();
    }


    public Folder getRootDirectory(){
        return rootDirectory;
    }

    /**
     * Creates a new directory given its path. If the path contains a set of new directories on lower levels, it creates
     *  them iteratively (top down).
     * @param srcPath path to directory
     * @return created or not (true, false)
     */
    public boolean createFolder(String srcPath) {
        boolean didCreate = false;
        int idx = 0;
        String[] paths = srcPath.split("/");
        Folder parent = rootDirectory;

        // Travel through path
        for (var folder : paths) {
            String path = Utils.createPathString(Arrays.copyOfRange(paths, 0 ,idx+1));
            Folder result = parent.getFolder(folder);

            // Check if it needs to create a folder (in case current does not exist)
            if (result == null) {
                result = new Folder.Builder()
                        .withPath(path)
                        .withName(folder)
                        .withParent(parent)
                        .build();
                didCreate = true;
            }

            parent = result;
            idx++;
        }

        return didCreate;
    }

    /**
     * Creates a new file at a given by path
     * @param author author of the file
     * @param path path to parent folder
     * @param fileName name of the new file
     * @param content file's content
     * @return true if it created, false if not
     */
    public boolean createFile(String author, String path, String fileName, InputStream content) {
        Folder parent = getFolder(path);

        // Parent folder at path was not found
        if (parent == null) {
            return false;
        }

        File file = new File.Builder()
                .withName(fileName)
                .withPath(path)
                .withContent(content)
                .withAuthor(author)
                .build();

        // Check if it did not create
        return parent.addFile(file);
    }

    /**
     * Returns the directory with the given path
     * @param path path to directory
     * @return directory
     */
    public Folder getFolder(String path) {
        if (path == null || path.isEmpty()) {
            return rootDirectory;
        }

        String[] paths = path.split("/");
        Folder current = rootDirectory;

        // Travel through path
        for (var folder : paths) {
            Shell.printDebug(folder);
            Folder next = current.getFolder(folder);

            // Check if it does not exit
            if (next == null) {
                return null;
            }

            current = next;
        }

        return current;
    }

    /**
     * Gets the requested 'file' at 'path'
     * @param path path to parent folder
     * @param file file to download
     * @return file to download
     */
    public File getFile(String path, String file) {
        Folder parent = getFolder(path);
        return parent.getFile(file);
    }

    /**
     * Removes a file from the manager
     * @param path path to parent folder
     * @param file file to remove
     * @return removed file
     */
    public File removeFile(String path, String file) {
        Folder parent = getFolder(path);
        return parent.removeFile(file);
    }
}
