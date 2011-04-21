package com.cannontech.common.model;


import java.util.Date;

import org.joda.time.Duration;
import org.joda.time.Instant;

public class ZigbeeTextMessage {

    private int messageId;
    private String message;
    private int accountId;
    private int inventoryId;
    private int gatewayId;
    private boolean confirmationRequired;
    private Duration displayDuration = Duration.standardMinutes(2);
    private Instant startTime = new Instant();
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getAccountId() {
        return accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    public int getInventoryId() {
        return inventoryId;
    }
    
    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }
    
    public int getGatewayId() {
        return gatewayId;
    }
    
    public void setGatewayId(int gatewayId) {
        this.gatewayId = gatewayId;
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
    
    /* Thain told me to */
    public int getMessageId() {
        return (int)new Date().getTime();
    }
    
}