package com.cannontech.common.device.groups.model;

import org.apache.commons.lang.Validate;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.groups.dao.DeviceGroupType;

public abstract class DeviceGroup implements Comparable<DeviceGroup> {
    private DeviceGroupType type;
    private String name;
    private DeviceGroup parent;

    /**
     * Method to determine if this group can have its name changed, parent
     * changed, or be deleted
     * @return True if editable
     */
    public abstract boolean isEditable();

    /**
     * Method to determine if this group can have sub groups added or removed
     * and have devices added or removed
     * @return True if modifiable
     */
    public abstract boolean isModifiable();

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

    public int compareTo(DeviceGroup dg) {
        return this.getFullName().compareTo(dg.getFullName());
    }
}
