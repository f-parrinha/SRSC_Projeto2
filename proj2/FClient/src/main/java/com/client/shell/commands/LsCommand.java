package com.client.shell.commands;

import com.client.serviceClients.FClient;
import com.client.shell.ShellPreconditions;

public class LsCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 1;
    public static int ARG_SIZE_WITH_PATH = 2;


    public LsCommand(FClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.LS_ARGS, ARG_SIZE, ARG_SIZE_WITH_PATH)) return;

        // Get input
        String username = input[1];
        String path = input.length == ARG_SIZE_WITH_PATH + 1 ? input[2] : "";   // +1 due to "ls" on the input

        var response = path.isEmpty() ? client.listFiles(username) : client.listFiles(username, path);
        client.readResponse(response);
    }
}
