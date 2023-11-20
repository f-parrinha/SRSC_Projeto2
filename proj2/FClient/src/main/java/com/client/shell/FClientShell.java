package com.client.shell;

import com.client.serviceClients.FClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Class  FClientShell  creates an interactive shell to communicate with the FServer
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
@SpringBootApplication
public class FClientShell implements CommandLineRunner{

    /** Constants */
    private static final String DASH = "> ";
    private static final String EMPTY = " ";
    private static final String SERVER_URL = "http://localhost:8081";
    private static final String DEFAULT_ERROR_MESSAGE = DASH+ "Unknown command.\n" + DASH;
    private static final String DEFAULT_WELCOME_MESSAGE = DASH + "Welcome to the FServer platform!\n" + DASH;

    /** Variables */
    private final BufferedReader reader;
    private final FClient client;   /*@TODO Use client to create requests on the command execution methods */
    private final ShellPreconditions preconditions;
    private enum Command {
        exit,
        login,
        ls,
        mkdir,
        put,
        get,
        cp,
        rm,
        file
    }

    public FClientShell() {
        client = new FClient(SERVER_URL);
        reader = new BufferedReader(new InputStreamReader(System.in));
        preconditions = new ShellPreconditions();
    }

    public static void main(String[] args) {
        SpringApplication.run(FClientShell.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String input = "";

        // Start execution
        System.out.println(DEFAULT_WELCOME_MESSAGE);
        while (!input.equals(Command.exit.toString())) {
            System.out.print(DASH);

            input = reader.readLine();
            String[] tokens = input.trim().split(EMPTY);

            executeCommand(tokens);
        }
    }

    /**
     * Responsible for dispatching the execution of the different commands based on user input
     * @param input input command
     */
    private void executeCommand(String[] input) {

        // Skips if input is empty
        if(!preconditions.wrongArgSize(input, 0)) {
            System.out.println(DASH);
            return;
        }

        // Read command
        String command = input[0];

        if (command.equals(Command.login.toString())) {
            loginCommand();
        } else if (command.equals(Command.ls.toString())) {
            lsCommand(input);
        } else if(command.equals(Command.mkdir.toString())) {
            mkdirCommand();
        } else if (command.equals(Command.put.toString())) {
            putCommand();
        } else if (command.equals(Command.get.toString())) {
            getCommand();
        } else if (command.equals(Command.cp.toString())) {
            cpCommand();
        } else if (command.equals(Command.rm.toString())) {
            rmCommand();
        } else if (command.equals(Command.file.toString())) {
            fileCommand();
        } else {
            System.out.println(DEFAULT_ERROR_MESSAGE);
        }
    }


    /** -- Command Execution -- */

    private void loginCommand() {
        System.out.println("(WIP) Executed 'login' command.");
    }
    private void lsCommand(String[] input) {

        // Check args size
        if(preconditions.wrongArgSize(input, 2)) {
            System.out.println(ShellPreconditions.WRONG_ARGS_MESSAGE + ShellPreconditions.LS_ARGS);
            return;
        }

        // Generate request
        String username = input[1];
        var response = client.listFiles(username);

        // Process response
        response.subscribe(
            result -> {
                System.out.println("Result: " + result);
            },
            error -> {
                System.err.println("Error: " + error.getMessage());
            },
            () -> { /* Leaving empty... */ }
        );
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
    private void cpCommand() {
        System.out.println("(WIP) Executed 'cp' command.");
    }
    private void rmCommand() {
        System.out.println("(WIP) Executed 'rm' command.");
    }
    private void fileCommand() {
        System.out.println("(WIP) Executed 'file' command.");
    }
}
