package com.cannontech.message.capcontrol.model;

public class DeleteItem extends CapControlMessage {

    private int itemId;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
}