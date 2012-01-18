package com.cannontech.common.model;


import java.io.Serializable;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;

public class ZigbeeTextMessage implements Serializable {
    private static final long serialVersionUID = 2L;

    //Addressing
    private Set<Integer> inventoryIds;

    //Message Data
    private int messageId;
    private String message;
    private boolean confirmationRequired;
    private Duration displayDuration;
    private Instant startTime;
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Set<Integer> getInventoryIds() {
        return inventoryIds;
    }
    
    public void setInventoryIds(Set<Integer> inventoryIds) {
        this.inventoryIds = inventoryIds;
    }
    
    public boolean isConfirmationRequired() {
        return confirmationRequired;
    }
    
    public void setConfirmationRequired(boolean confirmationRequired) {
        this.confirmationRequired = confirmationRequired;
    }
    
    public Duration getDisplayDuration() {
        return displayDuration;
    }
    
    public void setDisplayDuration(Duration displayDuration) {
        this.displayDuration = displayDuration;
    }
    
    public Instant getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }
    
    public int getMessageId() {
        return messageId;
    }
    
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}