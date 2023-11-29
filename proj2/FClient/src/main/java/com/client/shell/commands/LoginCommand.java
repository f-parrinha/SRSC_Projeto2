package com.client.shell.commands;

import com.api.requests.LoginRequest;
import com.client.serviceClients.FDispatcherClient;
import com.api.common.shell.ShellPreconditions;

import java.io.IOException;

public class LoginCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 2;


    public LoginCommand(FDispatcherClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) throws IOException, InterruptedException {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.LOGIN_ARGS, ARG_SIZE)) return;

        String username = input[1];
        String password = input[2];

        var response = client.login(new LoginRequest(username, password));
        client.readResponse(response);
    }
}
