package com.cannontech.common.device.groups.service;

import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupComposed;
import com.cannontech.common.device.groups.model.DeviceGroupComposedGroup;

public interface DeviceGroupComposedService {

    public DeviceGroupComposed getDeviceGroupComposed(DeviceGroup group) throws IllegalArgumentException;
    
    public List<DeviceGroupComposedGroup> getCompositionGroups(DeviceGroup group) throws IllegalArgumentException;
}
