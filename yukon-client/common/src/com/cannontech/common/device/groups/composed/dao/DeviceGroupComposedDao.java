package com.cannontech.common.device.groups.composed.dao;

import com.cannontech.common.device.groups.model.DeviceGroupComposed;

public interface DeviceGroupComposedDao {
    
    public void saveOrUpdate(DeviceGroupComposed deviceGroupComposed);
    
    public DeviceGroupComposed findForDeviceGroupId(int deviceGroupId);

    /**
     * Method to get DeviceGroupComposed by composedId
     */
    public DeviceGroupComposed getByComposedId(int composedId);
    
}
