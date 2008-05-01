package com.cannontech.common.device.commands;

/**
 * Abstract base class for command requests
 */
public abstract class CommandRequestBase {
    private String command;
    private boolean backgroundPriority = false;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isBackgroundPriority() {
        return backgroundPriority;
    }

    public void setBackgroundPriority(boolean backgroundPriority) {
        this.backgroundPriority = backgroundPriority;
    }

}
