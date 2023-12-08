package com.server;

import com.api.common.Utils;
import com.api.common.shell.ShellPreconditions;
import com.api.rest.RestResponse;
import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.rest.requests.MkDirRequest;
import com.api.rest.requests.PutRequest;
import com.api.services.StorageService;
import com.server.fileManager.Folder;
import com.server.fileManager.File;
import com.server.fileManager.FileManager;
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
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("There are empty folders on the path.");
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
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("There are empty folders on the path.");
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
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("There are empty folders on the path.");
        }

        // Check if file already exists
        if (!fileManager.createFile(username, putRequest.path(), putRequest.fileName(), Utils.decodeToFile(putRequest.encodedContent()))) {
            return new RestResponse(HttpStatus.CONFLICT).buildResponse("File already exists.");
        }

        // Updates user fileManager
        fileManagers.put(username, fileManager);
        return new RestResponse(HttpStatus.OK).buildResponse("Created new file with name '" + putRequest.fileName() + "' and with path '"+ putRequest.path() + "'");
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
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("There are empty folders on the path.");
        }

        // Create new file
        String[] tokens = path.split("/");
        File newFile = fileManager.getFile(Utils.createPathString(Arrays.copyOfRange(tokens, 0, tokens.length - 1)), tokens[tokens.length - 1]);

        // Check if directory already exists
        if (newFile == null) {
            return new RestResponse(HttpStatus.CONFLICT).buildResponse("File does not exist.");
        }

        return new RestResponse(HttpStatus.OK).buildResponse("Downloaded file '" + newFile.getName() + "'.");
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
            return new RestResponse(HttpStatus.BAD_REQUEST).buildResponse("There are empty folders on the path.");
        }

        // Remove file
        String[] tokens = path.split("/");
        File removedFile = fileManager.removeFile(Utils.createPathString(Arrays.copyOfRange(tokens, 0, tokens.length - 1)), tokens[tokens.length - 1]);

        // Check if directory already exists
        if (removedFile == null) {
            return new RestResponse(HttpStatus.CONFLICT).buildResponse("File does not exist.");
        }

        return new RestResponse(HttpStatus.OK).buildResponse("Removed file '" + removedFile.getName() + "' from storage.");
    }

    @Override
    public ResponseEntity<String> copyFile() {
        return null;
    }
}
