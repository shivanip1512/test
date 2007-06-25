package com.cannontech.common.device.groups.model;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.groups.dao.DeviceGroupType;

public class DeviceGroup {
    private DeviceGroupType type;
    private String name;
    private DeviceGroup parent;

    public void setName(String name) {
        this.name = name;
    }
    public void setParent(DeviceGroup parent) {
        this.parent = parent;
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

    public String getFullName() {
        if (parent == null) {
            return getName();
        } else {
            return getParent().getFullName() + "/" + getName();
        }
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("fullName", getFullName());
        tsc.append("type", getType());
        return tsc.toString();    
    }
}
