package com.api.common.shell;

import java.io.Console;

public class Shell {

    /** Constants */
    public static final String DASH = "> ";
    public static final String EMPTY = " ";
    public static final String RESULT_ENTRY = "RESULT: ";
    public static final String DEBUG_ENTRY = "DEBUG: ";
    public static final String FINE_ENTRY = "FINE: ";
    public static final String ERROR_ENTRY = "ERROR: ";
    public static final Console CONSOLE = System.console();

    private static boolean debugOn;

    /**
     * Prints a new custom line starting with a "> " on the shell interface
     * @param text the output text
     */
    public static void printLine(String text){
        System.out.println(DASH + text);
    }

    /**
     * Prints the input DASH
     */
    public static void printInput() {
        System.out.print(DASH);
    }

    /**
     * Prints a new line and adds the input DASH
     * @param text the output text
     */
    public static void printLineInput(String text){
        System.out.println(DASH + text);
        System.out.print(DASH);
    }

    /**
     * Prints text with a 'debug' label
     * @param text debug to print
     */
    public static void printDebug(String text) {
        if (debugOn) printLine(DEBUG_ENTRY + text);
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

    public static StorePasswords loadTrustKeyStoresPass(String[] args) {
        if(args.length == 2) {
            return new StorePasswords(args[0], args[1]);
        } else if (args.length == 3) {
            debugOn = args[2].equalsIgnoreCase("true");
            return new StorePasswords(args[0], args[1]);
        }

        // No passwords were given as input...
        printLineInput("Enter KeyStore password:");
        String keyStorePass = String.copyValueOf(CONSOLE.readPassword());
        printLineInput("Enter TrustStore password:");
        String trustStorePass = String.copyValueOf(CONSOLE.readPassword());

        return new StorePasswords(keyStorePass, trustStorePass);
    }
}
