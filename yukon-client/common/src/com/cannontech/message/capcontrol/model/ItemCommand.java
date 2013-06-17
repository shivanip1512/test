package com.cannontech.message.capcontrol.model;


public class ItemCommand extends CapControlCommand {
    
    private int itemId;
    
    public ItemCommand() {
        super();
    }
    
    public ItemCommand(int commandId, int itemId) {
        super();
        setCommandId(commandId);
        setItemId(itemId);
    }
    
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public int getItemId() {
        return itemId;
    }
    
}