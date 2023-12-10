package com.server.fileManager;

import com.api.utils.Utils;

import java.util.Arrays;

/**
 * Class  FileManager  manages files and directories
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class FileManager {
    public static final String ROOT = "root";

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
     * @param file file to be created
     * @return true if it created, false if not
     */
    public boolean createFile(Folder parent, File file) {
        return parent.addFile(file);    // Check if it did not create
    }

    /**
     * Updates a given a file in a given folder with new information from another given file
     * @param parent parent directory, where the file to update resides
     * @param file file to update
     * @param newFile file with new content
     * @return updated file
     */
    public File updateFile(Folder parent, File file, File newFile) {
        return parent.updateFile(file, newFile);
    }

    /**
     * Returns the directory with the given path
     * @param path path to directory
     * @return directory
     */
    public Folder getFolder(String path) {
        if (path == null || path.isEmpty() || path.equals(Directory.ROOT_DIR)) {
            return rootDirectory;
        }

        String[] paths = path.split("/");
        Folder current = rootDirectory;

        // Travel through path
        for (var folder : paths) {
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
     * @param parent parent folder that houses the file
     * @param file file to download
     * @return file to download
     */
    public File getFile(Folder parent, String file) {
        return parent.getFile(file);
    }

    /**
     * Removes a file from the manager
     * @param parent parent folder that houses the file
     * @param file file to remove
     * @return removed file
     */
    public File removeFile(Folder parent, String file) {
        return parent.removeFile(file);
    }
}
