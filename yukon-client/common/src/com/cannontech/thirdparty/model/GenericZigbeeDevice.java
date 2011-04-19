package com.cannontech.thirdparty.model;

public class GenericZigbeeDevice implements ZigbeeDevice,ZigbeeGateway {

    private int zigbeeDeviceId;
    private String zigbeeMacAddress;
    
    public void setZigbeeDeviceId(int zigbeeDeviceId) {
        this.zigbeeDeviceId = zigbeeDeviceId;
    }

    public void setZigbeeMacAddress(String zigbeeMacAddress) {
        this.zigbeeMacAddress = zigbeeMacAddress;
    }

    @Override
    public int getZigbeeDeviceId() {
        return zigbeeDeviceId;
    }

    @Override
    public String getZigbeeMacAddress() {
        return zigbeeMacAddress;
    }

}
