package com.cannontech.common.device.groups.model;

public class DeviceGroupComposedGroup {

    private Integer deviceGroupComposedGroupId;
    private int deviceGroupComposedId;
    private DeviceGroup deviceGroup;
    private boolean not;
    
    public void setDeviceGroupComposedGroupId(Integer deviceGroupComposedGroupId) {
        this.deviceGroupComposedGroupId = deviceGroupComposedGroupId;
    }
    public Integer getDeviceGroupComposedGroupId() {
        return deviceGroupComposedGroupId;
    }
    
    public void setDeviceGroupComposedId(int deviceGroupComposedId) {
        this.deviceGroupComposedId = deviceGroupComposedId;
    }
    public int getDeviceGroupComposedId() {
        return deviceGroupComposedId;
    }
    
    public void setDeviceGroup(DeviceGroup deviceGroup) {
        this.deviceGroup = deviceGroup;
    }
    public DeviceGroup getDeviceGroup() {
        return deviceGroup;
    }
    
    public void setNot(boolean not) {
        this.not = not;
    }
    public boolean isNot() {
        return not;
    }
}
