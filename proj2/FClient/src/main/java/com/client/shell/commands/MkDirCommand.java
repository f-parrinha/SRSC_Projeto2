package com.client.shell.commands;

import com.client.serviceClients.FClient;
import com.client.shell.ShellPreconditions;

public class MkDirCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 2;


    public MkDirCommand(FClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.MKDIR_ARGS, ARG_SIZE)) return;

        // Get input
        String username = input[1];
        String path = input[2];

        var response = client.makeDirectory(username, path);
        client.readResponse(response);
    }
}
