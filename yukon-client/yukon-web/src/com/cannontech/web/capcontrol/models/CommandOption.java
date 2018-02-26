package com.cannontech.web.capcontrol.models;

import com.cannontech.message.capcontrol.model.CommandType;

public class CommandOption {

    private CommandType commandName;
    private boolean isEnabled;
    private String disabledTextKey;

    public CommandOption() {/* Default constructor */}
    
    public CommandOption(CommandType commandName) {
        super();
        this.commandName = commandName;
        this.isEnabled = true;
    }

    public CommandOption(CommandType commandName, boolean isEnabled) {
        super();
        this.commandName = commandName;
        this.isEnabled = isEnabled;
    }
    
    public CommandOption(CommandType commandName, boolean isEnabled, String disableTextKey) {
        super();
        this.commandName = commandName;
        this.isEnabled = isEnabled;
        this.disabledTextKey = disableTextKey;
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

    public String getDisabledTextKey() {
        return disabledTextKey;
    }

    public void setDisabledTextKey(String disabledTextKey) {
        this.disabledTextKey = disabledTextKey;
    }
    
}
