package com.cannontech.common.device.commands;


/**
 * Abstract base class for command requests
 */
public abstract class CommandRequestBase {
    private CommandCallback commandCallback;

    public CommandCallback getCommandCallback() {
        return commandCallback;
    }
    public void setCommandCallback(CommandCallback commandCallback) {
        this.commandCallback = commandCallback;
    }
}
