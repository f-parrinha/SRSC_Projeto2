package com.client.shell.commands;

import com.api.rest.requests.MkDirRequest;
import com.api.utils.JwtTokenUtil;
import com.client.serviceClients.FDispatcherClient;
import com.api.common.shell.ShellPreconditions;

import java.io.IOException;

/**
 * Class  RmCommand  defines the "mkdir" command ran by the shell.
 * It follows the "Command Pattern" design pattern
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class MkDirCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 2;


    public MkDirCommand(FDispatcherClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) throws IOException, InterruptedException {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.MKDIR_ARGS, ARG_SIZE)) return;

        // Get input
        String username = input[1];
        String path = input[2];

        var response = client.makeDirectory(username, new MkDirRequest(path), JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN);
        client.readResponse(response);
    }
}
