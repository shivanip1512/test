package com.cannontech.common.device.groups.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.impl.providers.DeviceGroupProvider;
import com.cannontech.common.device.groups.model.DeviceGroup;

public interface DeviceGroupProviderDao extends DeviceGroupProvider {
    public DeviceGroup getRootGroup();
    public List<? extends DeviceGroup> getAllGroups();
    public Set<? extends DeviceGroup> getGroups(YukonDevice device);
    public boolean isDeviceInGroup(DeviceGroup base, YukonDevice device);
}
