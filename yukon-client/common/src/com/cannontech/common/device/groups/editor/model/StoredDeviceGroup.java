package com.cannontech.common.device.groups.editor.model;

import com.cannontech.common.device.groups.dao.DeviceGroupPermission;
import com.cannontech.common.device.groups.model.DeviceGroup;

public class StoredDeviceGroup extends DeviceGroup {
    private int id;
    private DeviceGroupPermission permission;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public boolean isEditable() {
        return permission.equals(DeviceGroupPermission.EDIT_MOD) || permission.equals(DeviceGroupPermission.EDIT_NOMOD);
    }

    @Override
    public boolean isModifiable() {
        return permission.equals(DeviceGroupPermission.EDIT_MOD) || permission.equals(DeviceGroupPermission.NOEDIT_MOD);
    }

    public DeviceGroupPermission getPermission() {
        return permission;
    }

    public void setPermission(DeviceGroupPermission permission) {
        this.permission = permission;
    }

}
