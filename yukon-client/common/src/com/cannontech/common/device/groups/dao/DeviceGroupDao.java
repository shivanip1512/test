package com.cannontech.common.device.groups.dao;

import java.util.List;

import com.cannontech.common.device.groups.dao.impl.providers.DeviceGroupProvider;
import com.cannontech.common.device.groups.model.DeviceGroup;

public interface DeviceGroupDao extends DeviceGroupProvider {
    public DeviceGroup getRootGroup();
    public List<? extends DeviceGroup> getAllGroups();
}
