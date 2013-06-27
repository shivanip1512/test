package com.cannontech.common.device.groups.composed.dao;

import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroupComposedGroup;

public interface DeviceGroupComposedGroupDao {

    public void saveOrUpdate(DeviceGroupComposedGroup deviceGroupComposedGroup);
    
    public List<DeviceGroupComposedGroup> getComposedGroupsForId(int deviceGroupComposedId);
    
    public void removeAllGroups(int deviceGroupComposedId);

    /**
     *  Method to get DeviceGroupComposedGroup by group names.
     */
   
    public List<DeviceGroupComposedGroup> findByGroupNames(List<String> groupNames);
}
