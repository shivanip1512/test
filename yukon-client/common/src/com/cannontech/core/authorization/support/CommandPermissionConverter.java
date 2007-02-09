package com.cannontech.core.authorization.support;

import java.util.List;

/**
 * Class used to convert a command string to a permission
 */
public class CommandPermissionConverter {

    private List<CommandPermissionMapping> commandPermissionList = null;

    public void setCommandPermissionList(List<CommandPermissionMapping> commandPermissionList) {
        this.commandPermissionList = commandPermissionList;
    }

    public PaoPermission getPermission(String command) {
        for (CommandPermissionMapping mapping : commandPermissionList) {
            if (mapping.isMatchingCommand(command)) {
                return mapping.getPermission();
            }
        }

        return PaoPermission.OTHER_COMMAND;
    }

    /**
     * Class which represents a mapping from a command string to a permission
     */
    public static class CommandPermissionMapping {

        private String commandStringStart = null;
        private PaoPermission permission = null;

        public boolean isMatchingCommand(String command) {
            if (command.startsWith(commandStringStart)) {
                return true;
            }
            return false;
        }

        public String getCommandStringStart() {
            return commandStringStart;
        }

        public void setCommandStringStart(String commandStringStart) {
            this.commandStringStart = commandStringStart;
        }

        public PaoPermission getPermission() {
            return permission;
        }

        public void setPermission(PaoPermission permission) {
            this.permission = permission;
        }
    }
}
