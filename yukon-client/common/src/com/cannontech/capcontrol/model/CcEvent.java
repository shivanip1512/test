package com.cannontech.capcontrol.model;

import org.joda.time.Instant;

public class CcEvent {
    
    private Instant dateTime;
    private String text;
    private String deviceName;
    
    public CcEvent() {
        
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
