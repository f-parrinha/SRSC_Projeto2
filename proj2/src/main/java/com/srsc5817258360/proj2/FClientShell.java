package com.srsc5817258360.proj2;

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

    //@TODO Extend for the remaining commands
    private enum Input{
        exit,
        ls,
        cp,
        helloworld
    }

    public FClientShell() {
        client = new FClient(SERVER_URL);
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
        if(command.equals(Input.ls.toString())) {
            lsCommand();
        } else if (command.equals(Input.cp.toString())) {
            cpCommand();
        } else if (command.equals(Input.helloworld.toString())) {
            helloworldCommand();
        } else {
            System.out.println(DEFAULT_ERROR_MESSAGE);
        }
    }

    private void helloworldCommand(){
        System.out.println("Requesting 'helloworld'..");
        client.getHelloWorld();
    }

    private void lsCommand() {
        System.out.println("(WIP) Executed 'ls' command.");
    }

    private void cpCommand() {
        System.out.println("(WIP) Executed 'cp' command.");
    }
}
