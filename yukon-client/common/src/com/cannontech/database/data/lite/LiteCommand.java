package com.cannontech.database.data.lite;

import com.cannontech.database.data.command.YukonCommand;

public class LiteCommand extends LiteBase implements YukonCommand {
    
    private String command;
    private String label;
    private String category;
    
    public LiteCommand(int commandId) {
        super();
        setLiteID(commandId);
        setLiteType(LiteTypes.COMMAND);
    }
    
    public LiteCommand(int commandId, String command, String label, String category) {
        this(commandId);
        setCommand(command);
        setLabel(label);
        setCategory(category);
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    @Override
    public int getCommandId() {
        return getLiteID();
    }
    
    public void setCommandId(int commandId) {
        setLiteID(commandId);
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public String toString() {
        return getLabel() + " : " + getCommand();
    }
    
}