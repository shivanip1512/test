package com.cannontech.yukon.api.stars.model;

import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;

public class DeviceTextMessage{
    private long messageId;
    private Set<String> serialNumbers;
    private String message;
    private boolean confirmationRequired;
    private Duration displayDuration;
    private Instant startTime;
    
    public long getMessageId() {
        return messageId;
    }
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
    
    public Set<String> getSerialNumbers() {
        return serialNumbers;
    }
    public void setSerialNumbers(Set<String> serialNumbers) {
        this.serialNumbers = serialNumbers;
    }
    
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
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
}