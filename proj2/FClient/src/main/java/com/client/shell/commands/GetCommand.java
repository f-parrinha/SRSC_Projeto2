package com.client.shell.commands;

import com.api.utils.JwtTokenUtil;
import com.client.serviceClients.FDispatcherClient;
import com.api.common.shell.ShellPreconditions;

import java.io.IOException;

/**
 * Class  RmCommand  defines the "get" command ran by the shell.
 * It follows the "Command Pattern" design pattern
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class GetCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 2;


    public GetCommand(FDispatcherClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) throws IOException, InterruptedException {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.GET_ARGS, ARG_SIZE)) return;

        // Get input
        String username = input[1];
        String path_file = input[2];

        var response = client.get(username, path_file, JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN);
        client.readResponse(response);
    }
}
