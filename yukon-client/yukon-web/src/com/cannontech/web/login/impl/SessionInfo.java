package com.cannontech.web.login.impl;

import org.joda.time.Instant;

public class SessionInfo {
    
    private Instant lastActivityTime = new Instant();
    private String ipAddress;
    
    public SessionInfo(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public Instant getLastActivityTime() {
        return lastActivityTime;
    }
    
    public void setLastActivityTime(Instant lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
}