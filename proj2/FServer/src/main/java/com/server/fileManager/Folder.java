package com.server.fileManager;

import java.time.LocalDate;

/**
 * Class  Directory  represents a directory in the file manager system.
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class Folder extends Directory {

    /** Constants */
    private static final int INITIAL_SIZE = 3;
    private static final int RESIZE_FACTOR = 2;


    /** Variables */
    private Folder parent;
    private Folder[] folders;
    private File[] files;
    private int foldersCounter;
    private int filesCounter;

    public Folder() {
        this.folders = new Folder[INITIAL_SIZE];
        this.files = new File[INITIAL_SIZE];
        this.foldersCounter = 0;
        this.filesCounter = 0;
    }

    /** Getters */
    public Folder getFolder(String directory) {
        if (foldersCounter == 0) return null;

        SearchResult result = binarySearch(directory, folders, foldersCounter);
        int idx = result.index();
        return result.found() ? folders[idx] : null;
    }
    public Folder[] getFolders() {
        return folders;
    }
    public File getFile(String directory) {
        if (filesCounter == 0) return null;

        SearchResult result = binarySearch(directory, files, filesCounter);
        int idx = result.index();
        return result.found() ? files[idx] : null;
    }
    public File[] getFiles() {
        return files;
    }

    /**
     * Joins both files' and folders' arrays into one
     * @return array with files and folders
     */
    public String[] listContent() {
        String[] result = new String[foldersCounter + filesCounter];
        int idx = 0;

        while(idx < foldersCounter) {
            result[idx] = "(Folder) " + folders[idx].getName();
            idx++;
        }

        while(idx - foldersCounter < filesCounter) {
            result[idx] = "(File) " + files[idx-foldersCounter].getName();
            idx++;
        }

        return result;
    }

    /** Setters */
    public void setFolders(Folder[] folders) {
        this.folders = folders;
    }
    public void setFiles(File[] files) {
        this.files = files;
    }

    /**
     * Creates a new folder. The insertion is ordered.
     *  Uses binary search to find the index behind the insertion point
     * @param newFolder new folder to add as a child
     * @return true/added, false/already exists
     */
    public boolean addFolder(Folder newFolder) {
        if (foldersCounter == 0) {
            addChildAtIndex(folders, foldersCounter++, newFolder, 0);
            return true;
        }

        // At least has one child... search for the index to insert the new directory
        SearchResult result = binarySearch(newFolder.getName(), folders, foldersCounter);
        int idx = result.index();

        // Check if already exists
        if (result.found()) return false;

        // Add child
        folders = (Folder[]) addChildAtIndex(folders, foldersCounter++, newFolder, idx);
        return true;
    }

    /**
     * Creates a new file. The insertion is ordered.
     *  Uses binary search to find the index behind the insertion point
     * @param newFile new file to add as a child
     * @return true/added, false/already exists
     */
    public boolean addFile(File newFile) {
        if (filesCounter == 0) {
            addChildAtIndex(files, filesCounter++, newFile, 0);
            return true;
        }

        // At least has one child... search for the index to insert the new directory
        SearchResult result = binarySearch(newFile.getName(), files, filesCounter);
        int idx = result.index();

        // Check if already exists
        if (result.found()) return false;

        // Add child
        files = (File[]) addChildAtIndex(files, filesCounter++, newFile, idx);
        return true;
    }

    /**
     * Updates an exiting file in the folder
     * PRE: file must not be null
     * PRE: newFile must not be null
     * @param file file to update
     * @param newFile new file
     * @return updated file
     */
    public File updateFile(File file, File newFile) {
        file.setLastUpdateDate(LocalDate.now());
        file.setContent(newFile.getContent());

        return file;
    }


    /**
     * Removes one child from the folder
     * @param folder folder to remove
     * @return removed folder
     */
    public Folder removeFolder(String folder) {
        if(foldersCounter == 0) return null;

        SearchResult result = binarySearch(folder, folders, foldersCounter);
        int idx = result.index();

        // Check if child does not exist
        if (!result.found()) return null;

        // Remove
        Folder removed = folders[idx];
        folders[idx] = null;

        // Push remaining tail
        for (int i = idx; i < foldersCounter - 1; i++) {
            folders[idx] = folders[idx + 1];
        }

        folders[foldersCounter--] = null;
        return removed;
    }

    /**
     * Removes one child from the file
     * @param file file to remove
     * @return removed file
     */
    public File removeFile(String file) {
        if(filesCounter == 0) return null;

        SearchResult result = binarySearch(file, files, filesCounter);
        int idx = result.index();

        // Check if child does not exist
        if (!result.found()) return null;

        // Remove
        File removed = files[idx];
        files[idx] = null;

        // Push remaining tail
        for (int i = idx; i < filesCounter - 1; i++) {
            files[idx] = files[idx + 1];
        }

        files[filesCounter--] = null;
        return removed;
    }

    /**
     * Finds an element in a list of directories (Files or Folders) by its name, using binary search.
     *  (PRE) list needs to gave at least two elements.
     * @param element directory element to search for
     * @param list where to search
     * @param limit max counter in list
     * @return (idx, found)
     */
    private SearchResult binarySearch(String element, Directory[] list, int limit) {
        if(limit == 1) {
            return new SearchResult(0, list[0].getName().equals(element));
        }

        // Start binary search
        int start = 0;
        int last = limit-1;
        int middleIdx = last / 2;

        while (start <= last) {
            middleIdx = start + (last - start) / 2;

            // Return if found
            if (list[middleIdx].getName().equals(element)) {
                return new SearchResult(middleIdx, true);
            }

            // Check if it needs to leave before advancing the search
            if(last == start) {
                break;
            }

            // Go to right half
            if (list[middleIdx].getName().compareTo(element) < 0) {
                start = middleIdx + 1;
                continue;
            }

            // Go to left half
            last = middleIdx - 1;
        }

        return new SearchResult(middleIdx, list[middleIdx].getName().equals(element));
    }

    /**
     * Adds a new child (File or Folder) in the respective array at the given index
     * @param child new Folder or File to add
     * @param idx where (the index) to add
     */
    private Directory[] addChildAtIndex(Directory[] originalSet, int setCounter, Directory child, int idx){
        int offset = originalSet[idx] == null ? 0 : child.getName().compareTo(originalSet[idx].getName()) > 0 ? 1 : 0;

        // Add space for the new element
        for (int i = setCounter; i > idx + offset; i--) {
            originalSet[i] = originalSet[i-1];
        }

        // Add child
        child.setParent(this);
        originalSet[idx + offset] = child;

        // Resize if needed
        if (setCounter == originalSet.length-1) originalSet = resizeDirectories(originalSet);
        return originalSet;
    }

    private Directory[] resizeDirectories(Directory[] list) {
        int idx = 0;
        Directory[] newList = Type.getType(list) == Type.Folder ? new Folder[folders.length * RESIZE_FACTOR] : new File[files.length * RESIZE_FACTOR];

        for (Directory f : list) {
            newList[idx++] = f;
        }

        return newList;
    }

    public static class Builder {
        private final Folder folder;

        public Builder() {
            this.folder = new Folder();
        }

        public Folder build() {
            return folder;
        }
        public Builder withName(String name){
            folder.setName(name);
            return this;
        }
        public Builder withPath(String path) {
            folder.setPath(path);
            return this;
        }
        public Builder withParent(Folder parent) {
            folder.setParent(parent);
            parent.addFolder(folder);
            return this;
        }
        public Builder withChildren(Folder[] children) {
            folder.setFolders(children);

            for (var child : children) {
                child.setParent(folder);
            }

            return this;
        }
    }
}
