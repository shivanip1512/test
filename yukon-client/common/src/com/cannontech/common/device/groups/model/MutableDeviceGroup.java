package com.cannontech.common.device.groups.model;

import org.apache.commons.lang.Validate;
import org.joda.time.Instant;

import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.user.YukonUserContext;

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
    private Instant createdDate;
    
    public MutableDeviceGroup(MutableDeviceGroup group) {
        this.name = group.name;
        this.parent = group.parent;
        this.type = group.type;
        this.createdDate = group.createdDate;
    }
    
    public MutableDeviceGroup() {
    }

    public void setName(String name) {
        this.name = name;
        clearNameCache();
    }

    public void setParent(DeviceGroup parent) {
        
        if (parent != null) {
            Validate.isTrue(!parent.isDescendantOf(this),"Cannot set parent to descendant group.");
        }
        
        this.parent = parent;
        clearNameCache();
    }

    public void setType(DeviceGroupType type) {
        this.type = type;
    }
    
    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String getName() {
        return name;
    }

    // Intentionally calling getName(), not using context or default
    @Override
    public String getName(YukonUserContext context, String defaultName) {
        return getName();
    }

    @Override
    public DeviceGroup getParent() {
        return parent;
    }

    @Override
    public DeviceGroupType getType() {
        return type;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }
}
