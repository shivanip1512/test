package com.cannontech.dr.assetavailability;

/**
 * This object represents a single appliance, with the device and relay to which it is attached, and the 
 * applianceCategoryId of the category to which it belongs.
 */
public class DeviceRelayApplianceCategory {
    private final int deviceId;
    private final int relay;
    private final int applianceCategoryId;
    
    public DeviceRelayApplianceCategory(int deviceId, int relay, int applianceCategoryId) {
        this.deviceId = deviceId;
        this.relay = relay;
        this.applianceCategoryId = applianceCategoryId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public int getRelay() {
        return relay;
    }

    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }
}
