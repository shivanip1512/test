package com.cannontech.core.authorization.support;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used to convert a command string into a permission
 */
public class CommandPermissionConverter {

    private List<CommandPermissionMapping> commandPermissionList = null;

    public void setCommandPermissionList(List<CommandPermissionMapping> commandPermissionList) {
        this.commandPermissionList = commandPermissionList;
    }

    public Permission getPermission(String command) {
        for (CommandPermissionMapping mapping : commandPermissionList) {
            if (mapping.isMatchingCommand(command)) {
                return mapping.getPermission();
            }
        }

        return Permission.OTHER_COMMAND;
    }

    /**
     * Static inner class which represents a mapping from a command string to a
     * permission
     */
    public static class CommandPermissionMapping {

        /**
         * commandString can be any string. Star (*) characters in the
         * commandString represent a wildcard. A wildcard is automatically added
         * to the end of every commandString. A wildcard will will match
         * anything:
         * 
         * <pre>
         *    control*connect matches: 
         *        
         *        control    connect
         *        controlconnect
         *        control device 123 connect
         *        control disconnect
         *        control connect device 123
         * </pre>
         * 
         * Spaces in the commandString will match any number of spaces
         * (including 0):
         * 
         * <pre>
         *    control connect matches:
         *        
         *        control connect
         *        controlconnect
         *        control      connect
         *        control connect device 123
         * </pre>
         */
        private String commandString = null;
        private Permission permission = null;

        public String getCommandString() {
            return commandString;
        }

        public void setCommandString(String commandString) {
            this.commandString = commandString;
        }

        public Permission getPermission() {
            return permission;
        }

        public void setPermission(Permission permission) {
            this.permission = permission;
        }

        /**
         * Method to determine if the given command matches the command string
         * that this mapping represents.
         * @param command - Command to match
         * @return True if command matches
         */
        public boolean isMatchingCommand(String command) {

            String commandExp = commandString.replaceAll("\\*", ".*");
            commandExp = commandExp.replaceAll(" ", "\\\\s*");
            commandExp = commandExp + ".*";

            Pattern commandPattern = Pattern.compile(commandExp);
            Matcher commandMatcher = commandPattern.matcher(command.toLowerCase());
            boolean matches = commandMatcher.matches();
            return matches;

        }
    }
}
