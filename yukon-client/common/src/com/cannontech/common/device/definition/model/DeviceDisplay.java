package com.cannontech.common.device.definition.model;

/**
 * Class which contains device display data and type
 */
public class DeviceDisplay {

    private int type = -1;
    private String displayName = null;

    public DeviceDisplay(int type, String displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String toString() {
        return this.displayName;
    }
}
