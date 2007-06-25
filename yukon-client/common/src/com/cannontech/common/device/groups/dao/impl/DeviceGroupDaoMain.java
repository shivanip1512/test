package com.cannontech.common.device.groups.dao.impl;

import java.util.List;
import java.util.Map;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.model.DeviceGroup;

public class DeviceGroupDaoMain implements DeviceGroupDao {
    private Map<DeviceGroupType, DeviceGroupDao> providers;

    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        return getProvider(group).getChildDevices(group);
    }

    public List<? extends DeviceGroup> getChildGroups(DeviceGroup group) {
        return getProvider(group).getChildGroups(group);
    }

    public String getDeviceGroupSqlInClause(DeviceGroup group) {
        return getProvider(group).getDeviceGroupSqlInClause(group);
    }

    public List<Integer> getDeviceIds(DeviceGroup group) {
        return getProvider(group).getDeviceIds(group);
    }

    public List<YukonDevice> getDevices(DeviceGroup group) {
        return getProvider(group).getDevices(group);
    }
    
    public DeviceGroup getGroup(DeviceGroup base, String groupName) {
        return getProvider(base).getGroup(base, groupName);
    }
    
    public DeviceGroup getRootGroup() {
        return providers.get(DeviceGroupType.STATIC).getRootGroup();
    }
    
    protected DeviceGroupDao getProvider(DeviceGroup group) {
        return providers.get(group.getType());
    }
    
    public void setProviders(Map<DeviceGroupType, DeviceGroupDao> providers) {
        this.providers = providers;
    }
    
}
