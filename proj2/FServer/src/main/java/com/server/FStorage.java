package com.server;

import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.rest.RestResponse;
import com.api.rest.requests.CopyRequest;
import com.api.rest.requests.MkDirRequest;
import com.api.rest.requests.PutRequest;
import com.api.services.StorageService;
import com.api.utils.Utils;
import com.server.fileManager.File;
import com.server.fileManager.FileManager;
import com.server.fileManager.Folder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Class  FServer  is the storage module of the FServer system. Manages each user's FileManagers, allowing them
 *  to create and store different files and directories in the FServer system.
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
@SpringBootApplication
@RestController
public class FStorage extends FServer implements StorageService<ResponseEntity<String>> {

    /** Constants */
    public static final int PORT = 8084;
    public static final String KEYSTORE_PATH = "classpath:fstorage-ks.jks";
    public static final String KEY_ALIAS = "fstorage";
    public static final String TRUSTSTORE_PATH = "classpath:fstorage-ts.jks";
    private static String[] args;
    Map<String, FileManager> fileManagers;

    public static void main(String[] args) {
        FStorage.args = args;
        SpringApplication.run(FStorage.class, args);
    }


    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() {
        fileManagers = new HashMap<>();
        StorePasswords passwords = Shell.loadTrustKeyStoresPass(args);
        return createWebServerFactory(PORT, KEYSTORE_PATH, KEY_ALIAS, TRUSTSTORE_PATH, passwords);
    }

    @GetMapping("/storage/ls/{username}")
    @Override
    public ResponseEntity<String> listDirectories(@PathVariable String username) {
        FileManager fileManager = fileManagers.get(username);

        // Check if user has content first
        if (fileManager == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("User '" + username + "' does not have any content yet.");
        }

        String[] result = fileManager.getRootDirectory().listContent();
        return new RestResponse(HttpStatus.OK).buildResponse(Arrays.toString(result));
    }

    @GetMapping("/storage/ls/{username}/{*path}")
    @Override
    public ResponseEntity<String> listDirectories(@PathVariable String username, @PathVariable String path) {
        FileManager fileManager = fileManagers.get(username);
        path = path.substring(1);           // Remove '/' from the beginning (due to URI mapping conflicts)

        // Check if user has content first
        if (fileManager == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("User '" + username + "' does not have any content yet.");
        }

        // Check if path is valid
        if(Utils.isPathInvalid(path)) {
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("The provided path is invalid. Check for incorrect '/' characters.");
        }

        // Check if folder at path exists
        Folder parent = fileManager.getFolder(path);
        if (parent == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("Folder at path '" + path + "' not found.");
        }

        // Send result
        String[] result = parent.listContent();
        return new RestResponse(HttpStatus.OK).buildResponse(Arrays.toString(result));
    }

    @PostMapping("/storage/mkdir/{username}")
    @Override
    public ResponseEntity<String> createFolder(@PathVariable String username, @RequestBody MkDirRequest mkDirRequest) {
        FileManager fileManager = fileManagers.get(username);

        // Check if user has content first, if not, create a new fileManager to store his content
        if (fileManager == null) {
            fileManager = new FileManager();
        }

        // Check if path is valid
        if(Utils.isPathInvalid(mkDirRequest.path())) {
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("The provided path is invalid. Check for incorrect '/' characters.");
        }

        // Check if directory already exists
        if (!fileManager.createFolder(mkDirRequest.path())) {
            return new RestResponse(HttpStatus.CONFLICT).buildResponse("Directory already exists.");
        }

        // Updates user fileManager
        fileManagers.put(username, fileManager);
        return new RestResponse(HttpStatus.OK).buildResponse("Created new directory at path '" + mkDirRequest.path() + "'");
    }

    @PutMapping("/storage/put/{username}")
    @Override
    public ResponseEntity<String>  createFile(@PathVariable String username, @RequestBody PutRequest putRequest)  {
        FileManager fileManager = fileManagers.get(username);

        // Check if user has content first, if not, create a new fileManager to store his content
        if (fileManager == null) {
            fileManager = new FileManager();
        }

        // Check if path is valid
        if(Utils.isPathInvalid(putRequest.path())) {
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("The provided path is invalid. Check for incorrect '/' characters.");
        }

        Folder parent = fileManager.getFolder(putRequest.path());

        // Check if parent folder exists
        if (parent == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("Folder at '" + putRequest.path() + "' was not found.");
        }

        File file = fileManager.getFile(parent, putRequest.fileName());
        File newfile = new File.Builder()
                .withName(putRequest.fileName())
                .withAuthor(username)
                .withPath(putRequest.path())
                .withContent(Utils.decodeToFile(putRequest.encodedContent()))
                .build();

        // Update file if it already exists
        if (file != null) {
            fileManager.updateFile(parent, file, newfile);
            fileManagers.put(username, fileManager);

            return new RestResponse(HttpStatus.OK).buildResponse("Updated file with name '" +  file.getName() + "' and with path '"+ file.getPath() + "' with new content.");
        }

        // Created new File
        fileManager.createFile(parent, newfile);
        fileManagers.put(username, fileManager);
        return new RestResponse(HttpStatus.OK).buildResponse("Created new file with name '" + newfile.getName() + "' and with path '"+ newfile.getPath() + "'");
    }

    @GetMapping("/storage/get/{username}/{*path}")
    @Override
    public ResponseEntity<String> getFile(@PathVariable String username, @PathVariable String path) {
        FileManager fileManager = fileManagers.get(username);
        path = path.substring(1);           // Remove '/' from the beginning (due to URI mapping conflicts)


        // Check if user has content first
        if (fileManager == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("User '" + username + "' does not have any content yet.");
        }

        // Check if path is valid
        if(Utils.isPathInvalid(path)) {
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("The provided path is invalid. Check for incorrect '/' characters.");
        }

        String[] path_file = separatePathAndFile(path);
        Folder parent = fileManager.getFolder(path_file[0]);

        // Check if parent folder exists
        if (parent == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("Folder at '" + path_file[0] + "' was not found.");
        }

        File file = fileManager.getFile(parent, path_file[1]);

        // Check if file does not exist
        if (file == null) {
            return new RestResponse(HttpStatus.CONFLICT).buildResponse("File does not exist.");
        }

        var encodedContent = Utils.encodeFileToJSON(file.getContent());
        return new RestResponse(HttpStatus.OK).buildResponse("Downloaded file '" + file.getName() + "'." + Utils.generateDownloadCode(file.getName(), encodedContent));
    }

    @DeleteMapping("/storage/rm/{username}/{*path}")
    @Override
    public ResponseEntity<String> removeFile(@PathVariable String username, @PathVariable String path) {
        FileManager fileManager = fileManagers.get(username);
        path = path.substring(1);           // Remove '/' from the beginning (due to URI mapping conflicts)

        // Check if user has content first
        if (fileManager == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("User '" + username + "' does not have any content yet.");
        }

        // Check if path is valid
        if(Utils.isPathInvalid(path)) {
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("The provided path is invalid. Check for incorrect '/' characters.");
        }

        String[] path_file = separatePathAndFile(path);
        Folder parent = fileManager.getFolder(path_file[0]);

        // Check if parent folder exists
        if (parent == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("Folder at '" + path_file[0] + "' was not found.");
        }

        File removedFile = fileManager.removeFile(parent, path_file[1]);

        // Check if directory already exists
        if (removedFile == null) {
            return new RestResponse(HttpStatus.CONFLICT).buildResponse("File does not exist.");
        }

        return new RestResponse(HttpStatus.OK).buildResponse("Removed file '" + removedFile.getName() + "' from storage.");
    }

    @PutMapping("/storage/cp/{username}")
    @Override
    public ResponseEntity<String> copyFile(@PathVariable String username, @RequestBody CopyRequest cpRequest) {
        FileManager fileManager = fileManagers.get(username);

        // Check if user has content first
        if (fileManager == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("User '" + username + "' does not have any content yet.");
        }

        // Check parent folders
        Folder srcParent = fileManager.getFolder(cpRequest.sourcePath());
        Folder destParent = fileManager.getFolder(cpRequest.destPath());
        if (srcParent == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("Source folder at '" + cpRequest.sourcePath() + "' was not found.");
        }
        if (destParent == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("Destination folder at '" + cpRequest.destPath() + "' was not found.");
        }

        File fileToCopy = fileManager.getFile(srcParent, cpRequest.sourceFile());

        // Check if source file exists
        if (fileToCopy == null) {
            return new RestResponse(HttpStatus.CONFLICT).buildResponse("File at source path not found.");
        }

        File destFile = fileManager.getFile(destParent, cpRequest.destFile());
        File copyFile = new File.Builder()
                .withAuthor(fileToCopy.getAuthor())
                .withContent(fileToCopy.getContent())
                .withName(cpRequest.destFile() != null && !cpRequest.destFile().isEmpty() ? cpRequest.destFile() : fileToCopy.getName())
                .withPath(cpRequest.destPath())
                .withIsCopied(true)
                .build();

        // Update file if it already exists
        if (destFile != null) {
            fileManager.updateFile(destParent, destFile, copyFile);
            fileManagers.put(username, fileManager);
            return new RestResponse(HttpStatus.OK).buildResponse("Updated file with name '" + destFile.getName() + "' and with path '" + destFile.getPath() + "' with new content.");
        }

        // Check if new file already exists
        fileManager.createFile(destParent, copyFile);
        return new RestResponse(HttpStatus.OK).buildResponse("Copied file '"+fileToCopy.getName()+"' from '"+fileToCopy.getPath()+"' to '"+copyFile.getPath()+"/"+copyFile.getName()+"'");
    }

    @GetMapping("/storage/file/{username}/{*path}")
    @Override
    public ResponseEntity<String> fileProperties(@PathVariable String username, @PathVariable String path) {
        FileManager fileManager = fileManagers.get(username);
        path = path.substring(1);           // Remove '/' from the beginning (due to URI mapping conflicts)

        // Check if user has content first
        if (fileManager == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("User '" + username + "' does not have any content yet.");
        }

        // Check if path is valid
        if(Utils.isPathInvalid(path)) {
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("The provided path is invalid. Check for incorrect '/' characters.");
        }

        String[] path_file = separatePathAndFile(path);
        Folder parent = fileManager.getFolder(path_file[0]);

        // Check if parent folder exists
        if (parent == null) {
            return new RestResponse(HttpStatus.NOT_FOUND).buildResponse("Folder at '" + path_file[0] + "' was not found.");
        }

        File file = fileManager.getFile(parent, path_file[1]);

        // Check if file does not exist
        if (file == null) {
            return new RestResponse(HttpStatus.CONFLICT).buildResponse("File does not exist.");
        }

        return new RestResponse(HttpStatus.OK).buildResponse(file.listProperties());
    }

    private String[] separatePathAndFile(String path) {
        String[] tokens = path.split("/");
        String parentPath = Utils.createPathString(Arrays.copyOfRange(tokens, 0, tokens.length - 1));
        String fileName = tokens[tokens.length - 1];
        return new String[] {parentPath, fileName};
    }
}
