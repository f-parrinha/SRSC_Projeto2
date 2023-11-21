package com.client.shell.commands;

import com.client.serviceClients.FClient;
import com.client.shell.ShellPreconditions;

public class CpCommand extends ShellCommand implements Command {
    public static int ARG_SIZE = 3;


    public CpCommand(FClient client) {
        super(client);
    }


    @Override
    public void execute(String[] input) {
        if (ShellPreconditions.wrongArgSize(input, ShellPreconditions.CP_ARGS, ARG_SIZE)) return;

        // Get input
        String username = input[1];
        String path_file1 = input[2];
        String path_file2 = input[3];

        // Process input before sending request
        String[] tmp1 = super.seperatePathAndFile(path_file1);
        String[] tmp2 = super.seperatePathAndFile(path_file2);
        String path1 = tmp1[0]; String path2 = tmp2[0];
        String file1 = tmp1[1]; String file2 = tmp2[1];

        var response = client.copy(username, path1, file1, path2, file2);
        client.readResponse(response);
    }
}
