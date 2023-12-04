package com.api.common.shell;

/**
 * Class  ShellPreconditions  offers precondition tools for the client shell
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class ShellPreconditions {
    public static final String DEFAULT_WRONG_ARG_SIZES_ERROR = "No expected arg sizes were given. ";
    public static final String WRONG_ARGS_MESSAGE = "Wrong argument list. Make sure you are following the right pattern ";
    public static final String LOGIN_ARGS = "'login username password'";
    public static final String LS_ARGS = "'ls username' or 'ls username path'";
    public static final String MKDIR_ARGS = "'mkdir username path'";
    public static final String PUT_ARGS = "'put username path/file'";
    public static final String GET_ARGS = "'get username path/file'";
    public static final String CP_ARGS = "'cp username path1/file1 path2/file2'";
    public static final String RM_ARGS = "'rm username path/file'";
    public static final String FILE_ARGS = "'file username path'";

    /**
     * Checks if the given argument list size is wrong or not, considering a given desired size
     * @param args argument list
     * @param argsString the args accepted by this method in string. Used in combination with an error message
     * @param argSizes the set of sizes that are meant to be verified
     * @return true if wrong, false if correct
     */
    public static boolean wrongArgSize(String[] args, String argsString, int ... argSizes) {
        if (argSizes.length == 0) { Shell.printError(DEFAULT_WRONG_ARG_SIZES_ERROR); return true; }

        for (int n : argSizes) {
            if (n == (args.length - 1)) {
                return false;
            }
        }

        Shell.printError(WRONG_ARGS_MESSAGE + argsString);
        return true;
    }

    /**
     * Checks whether the given input is empty or not
     * @param input the user input, tokenized
     * @return true if empty, false if not
     */
    public static boolean noCommandGiven(String[] input) {
        return input.length == 0 || input[0].isEmpty();
    }
}