package com.cannontech.database.data.lite;

/**
 * Lite class to represent a DeviceConfiguration
 */
public class LiteDeviceConfigurationCategory extends LiteBase {

    private String name = null;

    public LiteDeviceConfigurationCategory() {
        setLiteType(LiteTypes.DEVICE_CONFIGURATION_CATEGORY);
    }

    public LiteDeviceConfigurationCategory(int id, String name) {
        setLiteType(LiteTypes.DEVICE_CONFIGURATION_CATEGORY);
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
