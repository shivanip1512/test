package com.cannontech.common.device.groups.editor.model;

import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.model.DeviceGroup;

public class StoredDeviceGroup extends DeviceGroup {
    private int id;
    private boolean systemGroup;

    public int getId() {
        return id;
    }

    public boolean isSystemGroup() {
        return systemGroup;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSystemGroup(boolean systemGroup) {
        this.systemGroup = systemGroup;
    }

    @Override
    public boolean isEditable() {
        return !isSystemGroup();
    }

    @Override
    public boolean isModifiable() {
        return getType() == DeviceGroupType.STATIC;
    }

}
