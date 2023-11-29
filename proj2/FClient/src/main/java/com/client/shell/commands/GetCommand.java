package com.client.shell.commands;

import com.client.serviceClients.FDispatcherClient;
import com.api.common.shell.ShellPreconditions;

public class GetCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 2;


    public GetCommand(FDispatcherClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.GET_ARGS, ARG_SIZE)) return;

        // Get input
        String username = input[1];
        String path_file = input[2];

        // Process input before sending request
        String[] tmp = super.seperatePathAndFile(path_file);
        String path = tmp[0];
        String file = tmp[1];

        var response = client.get(username, path, file);
        client.readResponse(response);
    }
}
