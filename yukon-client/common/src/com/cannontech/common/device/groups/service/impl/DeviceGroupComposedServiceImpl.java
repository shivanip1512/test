package com.cannontech.common.device.groups.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.dao.DeviceGroupComposedDao;
import com.cannontech.common.device.groups.dao.DeviceGroupComposedGroupDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupComposed;
import com.cannontech.common.device.groups.model.DeviceGroupComposedGroup;
import com.cannontech.common.device.groups.service.DeviceGroupComposedService;
import com.cannontech.core.dao.NotFoundException;

public class DeviceGroupComposedServiceImpl implements DeviceGroupComposedService {

    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupComposedDao deviceGroupComposedDao;
    private DeviceGroupComposedGroupDao deviceGroupComposedGroupDao;
    
    @Override
    public DeviceGroupComposed getDeviceGroupComposed(DeviceGroup group) throws IllegalArgumentException {
        
        StoredDeviceGroup storedGroup;
        try {
            storedGroup= deviceGroupEditorDao.getStoredGroup(group);
        } catch (NotFoundException e) {
            throw new IllegalArgumentException(group.getFullName() + " is not a stored group, it cannot be a composed group.", e);
        }
        
        int deviceGroupId = storedGroup.getId();
        DeviceGroupComposed deviceGroupComposed = deviceGroupComposedDao.findForDeviceGroupId(deviceGroupId);
        if (deviceGroupComposed == null) {
            throw new IllegalArgumentException(group.getFullName() + " does not exist in the DeviceGroupComposed table, it cannot be a composed group.");
        }
        
        return deviceGroupComposed;
    }
    
    @Override
    public List<DeviceGroupComposedGroup> getCompositionGroups(DeviceGroup group) throws IllegalArgumentException {
        
        DeviceGroupComposed deviceGroupComposed = getDeviceGroupComposed(group);
        return deviceGroupComposedGroupDao.getComposedGroupsForId(deviceGroupComposed.getDeviceGroupComposedId());
    }
    
    @Autowired
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Autowired
    public void setDeviceGroupComposedDao(DeviceGroupComposedDao deviceGroupComposedDao) {
        this.deviceGroupComposedDao = deviceGroupComposedDao;
    }
    
    @Autowired
    public void setDeviceGroupComposedGroupDao(DeviceGroupComposedGroupDao deviceGroupComposedGroupDao) {
        this.deviceGroupComposedGroupDao = deviceGroupComposedGroupDao;
    }
}
