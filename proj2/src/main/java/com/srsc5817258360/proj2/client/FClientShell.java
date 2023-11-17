package com.srsc5817258360.proj2.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Class <c> FClientShell </c> creates an interactive shell to communicate with the FServer
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */

@SpringBootApplication
public class FClientShell implements CommandLineRunner{
    private static final String SERVER_URL = "TODO";
    private static final String DEFAULT_ERROR_MESSAGE = "Unknown command.";
    private static final String DEFAULT_WELCOME_MESSAGE = "Welcome to the FServer platform!";
    private final BufferedReader reader;
    private final FClient client;

    private enum Input {
        exit,
        login,
        mkdir,
        put,
        get,
        rm,
        ls,
        cp,
        file
    }

    public FClientShell() {
        client = new FDispatcherClient(SERVER_URL);
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public static void main(String[] args) {
        SpringApplication.run(FClientShell.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String input = "";

        // Start execution
        System.out.println(DEFAULT_WELCOME_MESSAGE);
        while (!input.equals(Input.exit.toString())) {
            System.out.print("> ");
            executeCommand(input = reader.readLine());
        }
    }

    /**
     * Responsible for executing the different commands based on user input
     * @param command input command
     */
    private void executeCommand(String command) {
        if (command.equals(Input.login.toString())) {
            loginCommand();
        } else if(command.equals(Input.mkdir.toString())) {
            mkdirCommand();
        } else if (command.equals(Input.put.toString())) {
            putCommand();
        } else if (command.equals(Input.get.toString())) {
            getCommand();
        } else if (command.equals(Input.rm.toString())) {
            rmCommand();
        } else if (command.equals(Input.ls.toString())) {
            lsCommand();
        } else if (command.equals(Input.cp.toString())) {
            cpCommand();
        } else if (command.equals(Input.file.toString())) {
            fileCommand();
        } else {
            System.out.println(DEFAULT_ERROR_MESSAGE);
        }
    }


    /** -- Command Execution -- */

    private void loginCommand() {
        System.out.println("(WIP) Executed 'login' command.");
    }
    private  void mkdirCommand() {
        System.out.println("(WIP) Executed 'mkdir' command.");
    }
    private void putCommand() {
        System.out.println("(WIP) Executed 'put' command.");
    }
    private void getCommand() {
        System.out.println("(WIP) Executed 'get' command.");
    }
    private void rmCommand() {
        System.out.println("(WIP) Executed 'rm' command.");
    }
    private void lsCommand() {
        System.out.println("(WIP) Executed 'ls' command.");
    }
    private void cpCommand() {
        System.out.println("(WIP) Executed 'cp' command.");
    }
    private void fileCommand() {
        System.out.println("(WIP) Executed 'file' command.");
    }
}
