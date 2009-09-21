package com.cannontech.common.device.groups.dao;

import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroupComposedGroup;

public interface DeviceGroupComposedGroupDao {

    public void saveOrUpdate(DeviceGroupComposedGroup deviceGroupComposedGroup);
    
    public List<DeviceGroupComposedGroup> getComposedGroupsForId(int deviceGroupComposedId);
    
    public void removeAllGroups(int deviceGroupComposedId);
}
