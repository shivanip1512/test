package com.cannontech.common.device.groups.model;

import com.cannontech.common.device.groups.dao.DeviceGroupType;

/**
 * This is meant as a base class for a groups that are "mutable".
 * Currently this is every group. The true purpose of this class
 * is to ensure that DeviceGroups returned are not accidentally edited.
 * This is especially important because they are often cached and
 * therefore shared.
 */
public abstract class MutableDeviceGroup extends DeviceGroup {
    private String name;
    private DeviceGroup parent;
    private DeviceGroupType type;
    
    
    public MutableDeviceGroup(MutableDeviceGroup group) {
        this.name = group.name;
        this.parent = group.parent;
        this.type = group.type;
    }
    
    public MutableDeviceGroup() {
    }

    public void setName(String name) {
        this.name = name;
        clearNameCache();
    }

    public void setParent(DeviceGroup parent) {
        this.parent = parent;
        clearNameCache();
    }

    public void setType(DeviceGroupType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public DeviceGroup getParent() {
        return parent;
    }

    public DeviceGroupType getType() {
        return type;
    }

}
