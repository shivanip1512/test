package com.cannontech.common.device.groups.dao;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.NotFoundException;

public interface DeviceGroupDao {
    public List<Integer> getDeviceIds(DeviceGroup group);
    public List<YukonDevice> getDevices(DeviceGroup group);
    public String getDeviceGroupSqlInClause(DeviceGroup group);
    public List<? extends DeviceGroup> getChildGroups(DeviceGroup group);
    public List<YukonDevice> getChildDevices(DeviceGroup group);
    public DeviceGroup getGroup(DeviceGroup base, String groupName) throws NotFoundException;
    public DeviceGroup getRootGroup();
}
