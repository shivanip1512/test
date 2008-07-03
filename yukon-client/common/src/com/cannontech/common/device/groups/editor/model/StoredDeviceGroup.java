package com.cannontech.common.device.groups.editor.model;

import com.cannontech.common.device.groups.dao.DeviceGroupPermission;
import com.cannontech.common.device.groups.model.MutableDeviceGroup;

public class StoredDeviceGroup extends MutableDeviceGroup {
    private int id;
    private DeviceGroupPermission permission;
    
    public StoredDeviceGroup(StoredDeviceGroup group) {
        super(group);
        this.id = group.id;
        this.permission = group.permission;
    }
    
    public StoredDeviceGroup() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public boolean isEditable() {
        
        if (this.isHidden()) {
            return false;
        }
        
        return permission.equals(DeviceGroupPermission.EDIT_MOD) || permission.equals(DeviceGroupPermission.EDIT_NOMOD);
    }

    @Override
    public boolean isModifiable() {
        
        if (this.isHidden()) {
            return true;
        }
        
        return permission.equals(DeviceGroupPermission.EDIT_MOD) || permission.equals(DeviceGroupPermission.NOEDIT_MOD);
    }
    
    @Override
    public boolean isHidden() {
        return permission.equals(DeviceGroupPermission.HIDDEN);
    }

    public DeviceGroupPermission getPermission() {
        return permission;
    }

    public void setPermission(DeviceGroupPermission permission) {
        this.permission = permission;
    }

}
