package com.cannontech.messaging.message.capcontrol;

public class SystemStatusMessage extends CapControlMessage {

    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
