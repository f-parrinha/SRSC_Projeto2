package com.client.shell.commands;

import com.client.serviceClients.FClient;
import com.client.shell.ShellPreconditions;

public class RmCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 2;


    public RmCommand(FClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.RM_ARGS, ARG_SIZE)) return;

        // Get input
        String username = input[1];
        String path_file = input[2];

        // Process input before sending request
        String[] tmp = super.seperatePathAndFile(path_file);
        String path = tmp[0];
        String file = tmp[1];

        var response = client.remove(username, path, file);
        client.readResponse(response);
    }
}
