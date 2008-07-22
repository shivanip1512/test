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
        
        return permission.isEditable();
    }

    @Override
    public boolean isModifiable() {
        
        return permission.isModifiable();
    }
    
    @Override
    public boolean isHidden() {
        
        if (getParent() != null) {
            return permission.isHidden() || getParent().isHidden();
        }
        
        return permission.isHidden();
    }

    public DeviceGroupPermission getPermission() {
        return permission;
    }

    public void setPermission(DeviceGroupPermission permission) {
        this.permission = permission;
    }

}
