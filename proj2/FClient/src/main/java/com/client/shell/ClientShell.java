package com.client.shell;

import com.api.common.shell.Shell;
import com.api.common.shell.ShellPreconditions;
import com.api.common.shell.StorePasswords;
import com.api.common.tls.TLSClientConfig;
import com.api.common.tls.TLSClientConfigBuilder;
import com.client.serviceClients.FDispatcherClient;
import com.client.shell.commands.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Class  FClientShell  creates an interactive shell to communicate with the FServer
 *
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
    private static final String DEFAULT_WELCOME_MESSAGE = "Welcome to the FServer platform!";


    public static void main(String[] args) throws IOException, UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, URISyntaxException, KeyManagementException, InterruptedException {
        StorePasswords passwords = Shell.loadTrustKeyStoresPass();
        TLSClientConfig tls = new TLSClientConfigBuilder()
                .withConfigFile(CONFIG_PATH)
                .withKeyStoreFile(KEYSTORE_FILE)
                .withKeyStorePass(passwords.keyStorePass())
                .withTrustStoreFile(TRUSTSTORE_FILE)
                .withTrustStorePass(passwords.trustStorePass())
                .build();
        FDispatcherClient client = new FDispatcherClient(new URI(DISPATCHER_URL), tls.getSslContext(), tls.getSslParameters());

        // Read commands...
        Shell.printLineInput(DEFAULT_WELCOME_MESSAGE);
        String input = "";
        while (!input.equalsIgnoreCase(Command.Type.exit.toString())) {
            executeCommand(client, CONSOLE.readLine().trim().split(EMPTY));
        }
    }


    /**
     * Responsible for dispatching the execution of the different commands based on user input
     * @param input input command
     */
    private static void executeCommand(FDispatcherClient client, String[] input) throws IOException, InterruptedException {
        if(ShellPreconditions.noCommandGiven(input)) { Shell.printLine(""); return; }    // Check empty input

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
}
