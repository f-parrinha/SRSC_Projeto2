package com.client.shell.commands;

import com.client.serviceClients.FClient;
import com.client.shell.ShellPreconditions;

public class LoginCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 2;


    public LoginCommand(FClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.LOGIN_ARGS, ARG_SIZE)) return;

        String username = input[1];
        String password = input[2];

        var response = client.login(username, password);
        client.readResponse(response);
    }
}
