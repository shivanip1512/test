package com.cannontech.database.data.lite;

/**
 * Lite class to represent a DeviceConfiguration
 */
public class LiteDeviceConfiguration extends LiteBase {

    private String name = null;

    public LiteDeviceConfiguration() {
        setLiteType(LiteTypes.DEVICE_CONFIGURATION);
    }

    public LiteDeviceConfiguration(int id, String name) {
        setLiteType(LiteTypes.DEVICE_CONFIGURATION);
        setLiteID(id);
        setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

}
