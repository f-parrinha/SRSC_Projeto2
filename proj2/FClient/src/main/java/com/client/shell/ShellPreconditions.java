package com.client.shell;

/**
 * Class  ShellPreconditions  offers precondition tools for the client shell
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class ShellPreconditions {
    public static final String WRONG_ARGS_MESSAGE = "Wrong argument list. Make sure you are following the right pattern ";
    public static final String LOGIN_ARGS = "'login username password'";
    public static final String LS_ARGS = "'ls username'";
    public static final String MKDIR_ARGS = "'mkdir username path'";
    public static final String PUT_ARGS = "'put username path/file'";
    public static final String GET_ARGS = "'get username path/file'";
    public static final String CP_ARGS = "'cp username path1/file1 path2/file2'";
    public static final String RM_ARGS = "'rm username path/file'";
    public static final String FILE_ARGS = "'file username'";

    /**
     * Checks if the given argument list size is wrong or not, considering a given desired size
     * @param args argument list
     * @param expectedArgsSize the expected size
     * @return true if wrong, false if correct
     */
    public boolean wrongArgSize(String[] args, int expectedArgsSize) {
        return args.length != expectedArgsSize;
    }
}