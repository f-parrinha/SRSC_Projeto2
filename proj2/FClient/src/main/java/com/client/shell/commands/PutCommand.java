package com.client.shell.commands;

import com.api.common.Utils;
import com.api.common.shell.Shell;
import com.api.rest.requests.PutRequest;
import com.client.AbstractClient;
import com.client.serviceClients.FDispatcherClient;
import com.api.common.shell.ShellPreconditions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Class  RmCommand  defines the "put" command ran by the shell.
 * It follows the "Command Pattern" design pattern
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class PutCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 2;


    public PutCommand(FDispatcherClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) throws IOException, InterruptedException {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.PUT_ARGS, ARG_SIZE)) return;

        // Get input
        String username = input[1];
        String path_file = input[2];

        // Process input before sending request
        String[] tmp = super.seperatePathAndFile(path_file);
        String path = tmp[0];
        String file = tmp[1];

        // Get file
        try (InputStream content = new FileInputStream(System.getProperty("user.dir") + "/" + file)) {
            var response = client.put(username, new PutRequest(path, file, Utils.encodeFileToJSON(content)));
            client.readResponse(response);
        } catch (FileNotFoundException e){
            Shell.printError("No file '" + file + " 'found at the current working directory.");
        }
    }
}
