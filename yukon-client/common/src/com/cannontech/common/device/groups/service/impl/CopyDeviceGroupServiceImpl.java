package com.cannontech.common.device.groups.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.CopyDeviceGroupService;
import com.cannontech.common.device.groups.util.YukonDeviceToIdMapper;
import com.cannontech.common.util.MappingSet;

public class CopyDeviceGroupServiceImpl implements CopyDeviceGroupService {
    
    private DeviceGroupProviderDao deviceGroupDao = null;
    private DeviceGroupEditorDao deviceGroupEditorDao = null;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    
    public void copyGroupAndDevicesToGroup(DeviceGroup fromGroup, StoredDeviceGroup toParent) {
     
        // copy devices in fromGroup to the the new parent
        Set<Integer> deviceIdsToAdd = new HashSet<Integer>();
        Set<YukonDevice> deviceList = deviceGroupDao.getChildDevices(fromGroup);
        Set<Integer> deviceIdsInGroup = new MappingSet<YukonDevice, Integer>(deviceList, new YukonDeviceToIdMapper());
        for (Integer deviceId : deviceIdsInGroup) {
            deviceIdsToAdd.add(deviceId);
        }
        deviceGroupMemberEditorDao.addDevicesById(toParent, deviceIdsToAdd.iterator());
        
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
