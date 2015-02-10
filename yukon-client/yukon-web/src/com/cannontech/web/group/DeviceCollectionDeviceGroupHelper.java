package com.cannontech.web.group;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class DeviceCollectionDeviceGroupHelper {
    
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    
    // add to group
    public void addCollectionToGroup(String groupName, DeviceCollection deviceCollection) {
        
        StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(groupName, false);
        doAddToGroup(group, deviceCollection);
    }
    
    public void addCollectionToGroup(Integer groupId, DeviceCollection deviceCollection) {
        
        StoredDeviceGroup group = deviceGroupEditorDao.getGroupById(groupId);
        doAddToGroup(group, deviceCollection);
    }
    
    private void doAddToGroup(StoredDeviceGroup group, DeviceCollection deviceCollection) {
        
        if (!group.isModifiable()) {
            throw new RuntimeException("Cannot add devices to " + group.getFullName());
        }

        deviceGroupMemberEditorDao.addDevices(group, deviceCollection.getDeviceList());
    }
    
    // remove from group
    public void removeCollectionFromGroup(String groupName, DeviceCollection deviceCollection) {
        
        StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(groupName, false);
        doRemoveFromGroup(group, deviceCollection);
    }
    
    public void removeCollectionFromGroup(Integer groupId, DeviceCollection deviceCollection) {
        
        StoredDeviceGroup group = deviceGroupEditorDao.getGroupById(groupId);
        doRemoveFromGroup(group, deviceCollection);
    }
    
    private void doRemoveFromGroup(StoredDeviceGroup group, DeviceCollection deviceCollection) {

        deviceGroupMemberEditorDao.removeDevices(group, deviceCollection.getDeviceList());
    }
    
    @Required
    public void setDeviceGroupEditorDao(
            DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Required
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
}
