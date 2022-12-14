package com.cannontech.common.device.groups.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.CopyDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;

public class CopyDeviceGroupServiceImpl implements CopyDeviceGroupService {
    
    private DeviceGroupProviderDao deviceGroupDao = null;
    private DeviceGroupEditorDao deviceGroupEditorDao = null;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    
    public void copyGroupAndDevicesToGroup(DeviceGroup fromGroup, StoredDeviceGroup toParent) {
        if (!toParent.isModifiable()) {
            throw new UnsupportedOperationException("Cannot add devices to a non-modifiable group.");
        }
        // copy devices in fromGroup to the the new parent
        Set<SimpleDevice> deviceList = deviceGroupDao.getChildDevices(fromGroup);
        deviceGroupMemberEditorDao.addDevices(toParent, deviceList);
        
        // loop over children groups of the fromGroup and recursively copy them to the newly copied group
        List<DeviceGroup> childGroups = deviceGroupDao.getChildGroups(fromGroup);
        
        for (DeviceGroup d : childGroups) {
            
            // copy child group into parent
            StoredDeviceGroup copiedGroup = deviceGroupEditorDao.addGroup(toParent, DeviceGroupType.STATIC, d.getName());
            
            // recurse, now the child group is the fromGroup, and the newly copied version of the child group 
            // is the parent to which to copy contents 
            copyGroupAndDevicesToGroup(d, copiedGroup);
        }
    }

    @Required
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }

    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }

    @Required
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
}
