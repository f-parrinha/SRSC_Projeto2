package com.client.shell.commands;

import com.client.serviceClients.FDispatcherClient;
import com.api.common.shell.ShellPreconditions;

import java.io.IOException;

public class FileCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 2;


    public FileCommand(FDispatcherClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) throws IOException, InterruptedException {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.FILE_ARGS, ARG_SIZE)) return;

        // Get input
        String username = input[1];
        String path_file = input[2];

        // Process input before sending request
        String[] tmp = super.seperatePathAndFile(path_file);
        String path = tmp[0];
        String file = tmp[1];

        var response = client.file(username, path, file);
        client.readResponse(response);
    }
}
