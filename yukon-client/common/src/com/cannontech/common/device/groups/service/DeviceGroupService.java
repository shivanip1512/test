package com.cannontech.common.device.groups.service;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.NotFoundException;

public interface DeviceGroupService {
    public DeviceGroup resolveGroupName(String groupName) throws NotFoundException;
    public List<YukonDevice> getDevices(String groupName);
    
    public DeviceGroup getRootGroup();
    
    public List<Integer> getDeviceIds(DeviceGroup group);
    public List<YukonDevice> getDevices(DeviceGroup group);
    public String getDeviceGroupSqlInClause(DeviceGroup group);

}
