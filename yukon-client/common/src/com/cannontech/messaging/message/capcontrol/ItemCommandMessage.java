package com.cannontech.messaging.message.capcontrol;

public class ItemCommandMessage extends CommandMessage {

    private int itemId;

    public ItemCommandMessage() {
        super();
    }

    public ItemCommandMessage(int commandId, int itemId) {
        super(commandId);
        setItemId(itemId);
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }
}
