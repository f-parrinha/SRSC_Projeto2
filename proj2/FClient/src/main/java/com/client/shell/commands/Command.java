package com.client.shell.commands;

/**
 * Interface  Command  defines the blueprint of a command
 * It follows the "Command Pattern" design pattern
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public interface Command {

    /**
     * Command types in the program
     */
    enum Type {
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

    /**
     * Main command execution
     * @param input input by user. Set of tokens
     */
    void execute(String[] input);
}
