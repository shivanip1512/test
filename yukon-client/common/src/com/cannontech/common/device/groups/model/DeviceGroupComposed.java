package com.cannontech.common.device.groups.model;

public class DeviceGroupComposed {

    private Integer deviceGroupComposedId;
    private int deviceGroupId;
    private DeviceGroupComposedCompositionType deviceGroupComposedCompositionType;
    
    public DeviceGroupComposed() {
    }
 
    public DeviceGroupComposed(int deviceGroupId) {
        this.deviceGroupId = deviceGroupId;
        this.deviceGroupComposedCompositionType = DeviceGroupComposedCompositionType.UNION;
    }
    
    public void setDeviceGroupComposedId(Integer deviceGroupComposedId) {
        this.deviceGroupComposedId = deviceGroupComposedId;
    }
    public Integer getDeviceGroupComposedId() {
        return deviceGroupComposedId;
    }
    
    public void setDeviceGroupId(int deviceGroupId) {
        this.deviceGroupId = deviceGroupId;
    }
    public int getDeviceGroupId() {
        return deviceGroupId;
    }
    
    public void setDeviceGroupComposedCompositionType(DeviceGroupComposedCompositionType deviceGroupComposedCompositionType) {
        this.deviceGroupComposedCompositionType = deviceGroupComposedCompositionType;
    }
    
    public DeviceGroupComposedCompositionType getDeviceGroupComposedCompositionType() {
        return deviceGroupComposedCompositionType;
    }
}
