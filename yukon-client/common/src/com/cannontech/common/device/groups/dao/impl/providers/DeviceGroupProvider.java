package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.NotFoundException;

public interface DeviceGroupProvider {
    public List<Integer> getDeviceIds(DeviceGroup group);
    public List<YukonDevice> getDevices(DeviceGroup group);
    public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier);    
    public String getDeviceGroupSqlWhereClause(DeviceGroup group, String identifier);
    public List<? extends DeviceGroup> getChildGroups(DeviceGroup group);
    public List<YukonDevice> getChildDevices(DeviceGroup group);
    public DeviceGroup getGroup(DeviceGroup base, String groupName) throws NotFoundException;
    public boolean isDeviceInGroup(DeviceGroup deviceGroup, YukonDevice device);
    public Set<? extends DeviceGroup> getGroups(DeviceGroup base, YukonDevice device);
}
