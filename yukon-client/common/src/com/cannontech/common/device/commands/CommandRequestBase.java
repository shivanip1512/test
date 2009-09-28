package com.cannontech.common.device.commands;


/**
 * Abstract base class for command requests
 */
public abstract class CommandRequestBase {
    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
