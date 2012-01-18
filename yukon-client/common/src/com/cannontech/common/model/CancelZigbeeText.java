package com.cannontech.common.model;

import java.io.Serializable;
import java.util.Set;

public class CancelZigbeeText implements Serializable {
    private static final long serialVersionUID = 3L;
    
    private int messageId;
    private Set<Integer> inventoryIds;
    
    
    public int getMessageId() {
        return messageId;
    }
    
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    
    public Set<Integer> getInventoryIds() {
        return inventoryIds;
    }
    
    public void setInventoryIds(Set<Integer> inventoryIds) {
        this.inventoryIds = inventoryIds;
    }
}
