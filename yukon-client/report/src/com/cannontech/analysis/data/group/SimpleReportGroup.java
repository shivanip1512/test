package com.cannontech.analysis.data.group;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class SimpleReportGroup {
    public static String NON_UNIQUE_IDENTIFIER = "*";
    private DeviceGroup deviceGroup;
    private Boolean unique;
    
    public DeviceGroup getDeviceGroup() {
        return deviceGroup;
    }
    public void setDeviceGroup(DeviceGroup deviceGroup) {
        this.deviceGroup = deviceGroup;
    }
    public Boolean getUnique() {
        return unique;
    }
    public void setUnique(Boolean unique) {
        this.unique = unique;
    }
}
