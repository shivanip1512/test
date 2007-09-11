package com.cannontech.common.device.groups.model;

import java.util.Comparator;

import org.apache.commons.lang.Validate;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.groups.dao.DeviceGroupType;

public class DeviceGroup implements Comparator<DeviceGroup>{
    private DeviceGroupType type;
    private String name;
    private DeviceGroup parent;
    private boolean isRemovable;

    public void setName(String name) {
        this.name = name;
    }
    public void setParent(DeviceGroup parent) {
        this.parent = parent;
    }
    public void setType(DeviceGroupType type) {
        this.type = type;
        this.isRemovable = this.isGroupEditable();
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
            return "/";
        } else {
            return getFullNameInternal();
        }
    }
    
    private String getFullNameInternal() {
        if (parent == null) {
            return "";
        } else {
            return getParent().getFullNameInternal() + "/" + getName();
        }
    }
    
    public boolean isRemovable() {
        return isRemovable;
    }

    public void setRemovable(boolean isRemovable) {
        this.isRemovable = isRemovable;
    }
    
    
    public boolean isDescendantOf(DeviceGroup possibleParent) {
        if (isChildOf(possibleParent)) {
            return true;
        } else if (parent == null) {
            return false;
        } else {
            return parent.isDescendantOf(possibleParent);
        }
    }
    
    public boolean isChildOf(DeviceGroup possibleParent) {
        Validate.notNull(possibleParent);
        boolean equals = possibleParent.equals(parent);
        return equals;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("fullName", getFullName());
        tsc.append("type", getType());
        tsc.append("isRemovable", isRemovable());
        return tsc.toString();    
    }
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        result = PRIME * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        final DeviceGroup other = (DeviceGroup) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (parent == null) {
            if (other.parent != null)
                return false;
        } else if (!parent.equals(other.parent))
            return false;
        return true;
    }
    
    public boolean isGroupEditable(){
        return type.equals(DeviceGroupType.STATIC);
    }
    
    public int compare(DeviceGroup dg1, DeviceGroup dg2) {
        String dg1Name = dg1.getFullName();
        String dg2Name = dg2.getFullName();

        return dg1Name.compareTo(dg2Name);
    }
}
