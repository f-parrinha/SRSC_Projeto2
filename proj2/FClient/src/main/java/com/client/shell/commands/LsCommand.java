package com.client.shell.commands;

import com.client.serviceClients.FDispatcherClient;
import com.api.common.shell.ShellPreconditions;

import java.io.IOException;

public class LsCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 1;
    public static int ARG_SIZE_WITH_PATH = 2;


    public LsCommand(FDispatcherClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) throws IOException, InterruptedException {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.LS_ARGS, ARG_SIZE, ARG_SIZE_WITH_PATH)) return;

        // Get input
        String username = input[1];
        String path = input.length == ARG_SIZE_WITH_PATH + 1 ? input[2] : "";   // +1 due to "ls" on the input

        var response = path.isEmpty() ? client.listFiles(username) : client.listFiles(username, path);
        client.readResponse(response);
    }
}
