package com.client.shell;

import com.api.common.shell.Shell;
import com.api.common.shell.ShellPreconditions;
import com.api.common.shell.StorePasswords;
import com.api.common.tls.TLSClientConfig;
import com.api.common.tls.TLSConfigFactory;
import com.client.serviceClients.FDispatcherClient;
import com.client.shell.commands.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Class  FClientShell  creates an interactive shell to communicate with the FServer
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class ClientShell extends Shell {

    /** Constants */
    public static final InputStream CONFIG_PATH = ClientShell.class.getClassLoader().getResourceAsStream("clienttls.conf");
    public static final InputStream KEYSTORE_FILE= ClientShell.class.getClassLoader().getResourceAsStream( "client-ks.jks");
    public static final InputStream TRUSTSTORE_FILE = ClientShell.class.getClassLoader().getResourceAsStream( "client-ts.jks");
    private static final String DISPATCHER_URL = "https://localhost:8081";
    private static final String DEFAULT_ERROR_MESSAGE = "Unknown command.";
    private static final String DEFAULT_EXIT_MESSAGE = "Closing client.. See you next time! :)";

    public static void main(String[] args) {
        StorePasswords passwords = Shell.loadTrustKeyStoresPass(args);
        FDispatcherClient client = null;
        try {
            TLSClientConfig tls = TLSConfigFactory.getInstance().forClient()
                    .withConfigFile(CONFIG_PATH)
                    .withKeyStoreFile(KEYSTORE_FILE)
                    .withKeyStorePass(passwords.keyStorePass())
                    .withTrustStoreFile(TRUSTSTORE_FILE)
                    .withTrustStorePass(passwords.trustStorePass())
                    .build();
            client = new FDispatcherClient(new URI(DISPATCHER_URL), tls.getSslContext(), tls.getSslParameters());
        } catch (URISyntaxException e) {
            Shell.printError("Wrong URI syntax given.");
            return;
        }

        // Read commands...
        initText();
        String input = "";
        while (!input.equalsIgnoreCase(Command.Type.exit.toString())) {
            try {
                executeCommand(client, CONSOLE.readLine().trim().split(EMPTY));
            } catch (IOException e) {
                Shell.printError("Unable to read input.");
            } catch (InterruptedException e) {
                Shell.printError("Interpreter was interrupted.");
            }
        }
    }


    /**
     * Responsible for dispatching the execution of the different commands based on user input
     * @param input input command
     */
    private static void executeCommand(FDispatcherClient client, String[] input) throws IOException, InterruptedException {
        if(ShellPreconditions.noCommandGiven(input)) { Shell.printInput(); return; }    // Check empty input

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
            Shell.printLine(DEFAULT_EXIT_MESSAGE);
            System.exit(0);
        } else {
            Shell.printError(DEFAULT_ERROR_MESSAGE);
        }

        // Enter input DASH
        Shell.printInput();
    }

    private static void initText() {
        System.out.println();
        Shell.printLine("Welcome to the FServer platform!");
        Shell.printLine("A platform to store your folders and files, on the web!");
        Shell.printLine("");
        Shell.printLine("");
        Shell.printLine("These are all of the available commands:");
        Shell.printLine(" * login (username, password): use this command to authenticate yourself in the FServer platform");
        Shell.printLine(" * ls (username): use this command to list all your folders in your 'root' directory");
        Shell.printLine(" * ls (username, path): use this command to list all your folders at 'path'");
        Shell.printLine(" * mkdir (username, path): use this command to create new folders in your FileManager");
        Shell.printLine(" * put (username, path/file): use this command to put new files inside your folders in you FileManager");
        Shell.printLine(" * get (username, path/file): use this command to get one 'file' from your FileManager at the given 'path'");
        Shell.printLine(" * cp (username, path/file1, path/file2): use this command to copy file 1 to file 2");
        Shell.printLine(" * rm (username, path/file): use this command to remove one file");
        Shell.printLine(" * file (username, path/file): use this command to view a file's properties");
        Shell.printLine("");
        Shell.printLine("Authors: Francisco Parrinha and Martin Magdalinchev");
        Shell.printLineInput("");
    }
}
