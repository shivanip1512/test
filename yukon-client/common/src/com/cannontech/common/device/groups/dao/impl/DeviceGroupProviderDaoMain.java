package com.cannontech.common.device.groups.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.dao.impl.providers.DeviceGroupProvider;
import com.cannontech.common.device.groups.dao.impl.providers.StaticDeviceGroupProvider;
import com.cannontech.common.device.groups.model.DeviceGroup;

public class DeviceGroupProviderDaoMain implements DeviceGroupProviderDao {
    private Map<DeviceGroupType, DeviceGroupProvider> providers;
    private StaticDeviceGroupProvider staticProvider;

    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        return getProvider(group).getChildDevices(group);
    }
    
    public int getChildDeviceCount(DeviceGroup group) {
        return getProvider(group).getChildDeviceCount(group);
    }

    public List<? extends DeviceGroup> getChildGroups(DeviceGroup group) {
        return getProvider(group).getChildGroups(group);
    }

    public String getDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        return getProvider(group).getDeviceGroupSqlWhereClause(group, identifier);
    }

    public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        return getProvider(group).getChildDeviceGroupSqlWhereClause(group, identifier);
    }

    public List<Integer> getDeviceIds(DeviceGroup group) {
        return getProvider(group).getDeviceIds(group);
    }

    public List<YukonDevice> getDevices(DeviceGroup group) {
        return getProvider(group).getDevices(group);
    }

    public int getDeviceCount(DeviceGroup group) {
        return getProvider(group).getDeviceCount(group);
    }
    
    public DeviceGroup getGroup(DeviceGroup base, String groupName) {
        return getProvider(base).getGroup(base, groupName);
    }
    
    public DeviceGroup getRootGroup() {
        return staticProvider.getRootGroup();
    }
    
    public List<? extends DeviceGroup> getAllGroups() {
        List<DeviceGroup> result = new ArrayList<DeviceGroup>(10);
        result.add(getRootGroup());
        collectChildGroups(result, getRootGroup());
        
        return result;
    }
    
    public Set<? extends DeviceGroup> getGroups(YukonDevice device) {
        DeviceGroup rootGroup = getRootGroup();
        return getGroups(rootGroup, device);
    }
    
    public Set<? extends DeviceGroup> getGroups(DeviceGroup base, YukonDevice device) {
        DeviceGroupProvider provider = getProvider(base);
        Set<? extends DeviceGroup> groups = provider.getGroups(base, device);
        
        return groups;
    }
    
    private void collectChildGroups(Collection<DeviceGroup> result, DeviceGroup rootGroup) {
        List<? extends DeviceGroup> childGroups = getChildGroups(rootGroup);
        for (DeviceGroup group : childGroups) {
            result.add(group);
            collectChildGroups(result, group);
        }
    }

    protected DeviceGroupProvider getProvider(DeviceGroup group) {
        return providers.get(group.getType());
    }
    
    @Required
    public void setProviders(Map<DeviceGroupType, DeviceGroupProvider> providers) {
        this.providers = providers;
    }
    
    @Required
    public void setStaticProvider(StaticDeviceGroupProvider staticProvider) {
        this.staticProvider = staticProvider;
    }

    public boolean isDeviceInGroup(DeviceGroup group, YukonDevice device) {
        return getProvider(group).isDeviceInGroup(group,device);
    }

    public void removeGroupDependancies(DeviceGroup group) {
        getProvider(group).removeGroupDependancies(group);
    }
    
}
