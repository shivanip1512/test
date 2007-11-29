package com.cannontech.common.device.groups.editor.dao.impl;

import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

/**
 * This is a holder class for a StoredDeviceGroup that does not have its
 * parent assigned yet. The ID of the parent is stored along side it so that
 * the actual StoredDeviceGroup of the parent can be looked up and used
 * to completed the storedDeviceGroup in a separate step.
 */
public class PartialDeviceGroup {
    private StoredDeviceGroup storedDeviceGroup;
    private Integer parentGroupId = null;
    
    public StoredDeviceGroup getStoredDeviceGroup() {
        return storedDeviceGroup;
    }
    
    public void setStoredDeviceGroup(StoredDeviceGroup storedDeviceGroup) {
        this.storedDeviceGroup = storedDeviceGroup;
    }
    
    public Integer getParentGroupId() {
        return parentGroupId;
    }
    
    public void setParentGroupId(Integer parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + storedDeviceGroup.getId();
        result = prime * result + ((parentGroupId == null) ? 0 : parentGroupId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        // we need to be careful not to call the equals method on the storedDeviceGroup
        // because it is defined on (parent, name) which won't be setup yet
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PartialDeviceGroup other = (PartialDeviceGroup) obj;
        if (storedDeviceGroup.getId() != other.storedDeviceGroup.getId())
            return false;
        if (parentGroupId == null) {
            if (other.parentGroupId != null)
                return false;
        } else if (!parentGroupId.equals(other.parentGroupId))
            return false;
        return true;
    }

}
