package com.client.shell;

import com.client.serviceClients.FClient;
import com.client.shell.commands.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Class  FClientShell  creates an interactive shell to communicate with the FServer
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
@SpringBootApplication
@PropertySource("classpath:application.yml")
public class FClientShell implements CommandLineRunner{

    /** Constants */
    private static final String SERVER_URL = "http://localhost:8081";
    private static final String DASH = "> ";
    private static final String EMPTY = " ";
    public static final String RESULT_ENTRY = "RESULT: ";
    public static final String FINE_ENTRY = "FINE: ";
    public static final String ERROR_ENTRY = "ERROR: ";
    private static final String DEFAULT_ERROR_MESSAGE = "Unknown command.";
    private static final String DEFAULT_EXIT_MESSAGE = "Closing client.. See you next time! :)";
    private static final String DEFAULT_WELCOME_MESSAGE = "Welcome to the FServer platform!";

    /** Variables */
    private final BufferedReader reader;
    private final FClient client;


    public FClientShell() throws URISyntaxException, SSLException {
        client = new FClient(new URI(SERVER_URL));
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public static void main(String[] args) {
        SpringApplication.run(FClientShell.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String input = "";

        // Start execution
        printLine(DEFAULT_WELCOME_MESSAGE);
        while (!input.equalsIgnoreCase(Command.Type.exit.toString())) {
            executeCommand(reader.readLine().trim().split(EMPTY));
        }
    }

    /**
     * Prints a new custom line starting with a "> " on the shell interface
     * @param text the output text
     */
    public static void printLine(String text){
        if(text.isEmpty()) { System.out.print(DASH); return;}

        System.out.println(DASH + text);
        System.out.print(DASH);
    }

    /**
     * Prints text with a 'result' label
     * @param text result to print
     */
    public static void printResult(String text) {
        printLine(RESULT_ENTRY + text);
    }

    /**
     * Prints text with a 'fine' label
     * @param text text to print
     */
    public static void printFine(String text) {
        printLine(FINE_ENTRY + text);
    }

    /**
     * Prints text with a 'error' label
     * @param text error to print
     */
    public static void printError(String text) {
        printLine(ERROR_ENTRY + text);
    }

    /**
     * Responsible for dispatching the execution of the different commands based on user input
     * @param input input command
     */
    private void executeCommand(String[] input) {
        if(ShellPreconditions.noCommandGiven(input)) { printLine(""); return; }    // Check empty input

        // Read command
        String commandInput = input[0];

        if (commandInput.equalsIgnoreCase(Command.Type.login.toString())) {
            new LoginCommand(client).execute(input);
        } else if (commandInput.equalsIgnoreCase(Command.Type.ls.toString())) {
            new LsCommand(client).execute(input);
        } else if(commandInput.equalsIgnoreCase(Command.Type.mkdir.toString())) {
            new MkDirCommand(client).execute(input);
        } else if (commandInput.equalsIgnoreCase(Command.Type.put.toString())) {
            new PutCommand(client).execute(input);
        } else if (commandInput.equalsIgnoreCase(Command.Type.get.toString())) {
            new GetCommand(client).execute(input);
        } else if (commandInput.equalsIgnoreCase(Command.Type.cp.toString())) {
            new CpCommand(client).execute(input);
        } else if (commandInput.equalsIgnoreCase(Command.Type.rm.toString())) {
            new RmCommand(client).execute(input);
        } else if (commandInput.equalsIgnoreCase(Command.Type.file.toString())) {
            new FileCommand(client).execute(input);
        } else if (commandInput.equalsIgnoreCase(Command.Type.exit.toString())) {
            printFine(DEFAULT_EXIT_MESSAGE);
            System.exit(0);
        } else {
            printError(DEFAULT_ERROR_MESSAGE);
        }
    }
}
