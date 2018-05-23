package com.cannontech.common.device.groups.model;

import java.io.Serializable;

import org.apache.commons.lang3.Validate;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.util.NaturalOrderComparator;

public abstract class DeviceGroup implements Comparable<DeviceGroup>, Serializable{
    private String cachedFullNameInternal = null;

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
    
    public abstract boolean isHidden();

    public abstract String getName();
    
    public abstract DeviceGroup getParent();

    public abstract DeviceGroupType getType();
    
    /**
     * For a group that is not a system group null will be returned otherwise SystemGroupEnum should be returned.
     */
    public abstract SystemGroupEnum getSystemGroupEnum();
    
    protected void clearNameCache() {
        cachedFullNameInternal = null;
    }

    public String getFullName() {
        if (getParent() == null) {
            return "/";
        } else {
            return getFullNameInternal();
        }
    }

    private String getFullNameInternal() {
        if (getParent() == null) {
            return "";
        }

        if (cachedFullNameInternal == null) {
            cachedFullNameInternal = getParent().getFullNameInternal() + "/" + getName();
        }
        return cachedFullNameInternal;
    }

    public boolean isDescendantOf(DeviceGroup possibleParent) {
        if (isChildOf(possibleParent)) {
            return true;
        } else if (getParent() == null) {
            return false;
        } else {
            return getParent().isDescendantOf(possibleParent);
        }
    }
    
    public boolean isEqualToOrDescendantOf(DeviceGroup otherGroup) {
        return equals(otherGroup) || isDescendantOf(otherGroup);
    }

    public boolean isChildOf(DeviceGroup possibleParent) {
        Validate.notNull(possibleParent);
        boolean equals = possibleParent.equals(getParent());
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
        result = PRIME * result + ((getName() == null) ? 0 : getName().hashCode());
        result = PRIME * result + ((getParent() == null) ? 0 : getParent().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final DeviceGroup other = (DeviceGroup) obj;
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }
        if (getParent() == null) {
            if (other.getParent() != null) {
                return false;
            }
        } else if (!getParent().equals(other.getParent())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(DeviceGroup dg) {
        NaturalOrderComparator naturalOrderComparator = new NaturalOrderComparator();
        return naturalOrderComparator.compare(this.getFullName().toLowerCase(),dg.getFullName().toLowerCase());
    }
}
