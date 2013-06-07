package com.cannontech.messaging.message.capcontrol;

public class DeleteItemMessage extends CapControlMessage {

    private int itemId;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

}
