package com.cannontech.web.capcontrol.models;

import com.cannontech.message.capcontrol.model.CommandType;

public class CommandOption {

    private CommandType commandName;
    private boolean isEnabled;

    public CommandOption() {/* Default constructor */}

    public CommandOption(CommandType commandName, boolean isEnabled) {
        super();
        this.commandName = commandName;
        this.isEnabled = isEnabled;
    }

    public CommandType getCommandName() {
        return commandName;
    }

    public void setCommandName(CommandType commandName) {
        this.commandName = commandName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
}
