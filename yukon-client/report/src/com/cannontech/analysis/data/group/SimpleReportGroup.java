package com.cannontech.analysis.data.group;

import org.apache.commons.lang3.Validate;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class SimpleReportGroup {
    public static String NON_UNIQUE_IDENTIFIER = "*";
    public static String SELF_IDENTIFIER = "SELF";
    private DeviceGroup baseDeviceGroup;
    private DeviceGroup deviceGroup;
    private Boolean unique;
    
    public SimpleReportGroup(DeviceGroup baseDeviceGroup,
            DeviceGroup deviceGroup, Boolean unique) {
        Validate.isTrue(deviceGroup.isEqualToOrDescendantOf(baseDeviceGroup), "deviceGroup must be a child of baseDeviceGroup");
        this.baseDeviceGroup = baseDeviceGroup;
        this.deviceGroup = deviceGroup;
        this.unique = unique;
    }
    
    public SimpleReportGroup(DeviceGroup baseDeviceGroup) {
        this.baseDeviceGroup = baseDeviceGroup;
        this.deviceGroup = null;
        this.unique = true;
    }
    
    public DeviceGroup getDeviceGroup() {
        return deviceGroup;
    }
    public Boolean getUnique() {
        return unique;
    }
    public void setUnique(Boolean unique) {
        this.unique = unique;
    }
    
    public String getDisplayName() {
        String partialName;
        if (deviceGroup == null) {
            // not in any groups under base
            return "";
        }
        if (deviceGroup.equals(baseDeviceGroup)) {
            partialName = SELF_IDENTIFIER;
        } else {
            partialName = deviceGroup.getFullName().replaceFirst(baseDeviceGroup.getFullName(), "");
        }
        return partialName + (unique ? "": SimpleReportGroup.NON_UNIQUE_IDENTIFIER);
    }
}
