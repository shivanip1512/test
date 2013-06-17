package com.cannontech.message.capcontrol.model;

public class SystemStatus extends CapControlMessage {

    private boolean enabled;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
}