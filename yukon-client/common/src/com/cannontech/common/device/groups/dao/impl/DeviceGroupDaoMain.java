package com.cannontech.common.device.groups.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.dao.impl.providers.DeviceGroupProvider;
import com.cannontech.common.device.groups.dao.impl.providers.StaticDeviceGroupProvider;
import com.cannontech.common.device.groups.model.DeviceGroup;

public class DeviceGroupDaoMain implements DeviceGroupDao {
    private Map<DeviceGroupType, DeviceGroupProvider> providers;
    private StaticDeviceGroupProvider staticProvider;

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
        return staticProvider.getRootGroup();
    }
    
    public List<? extends DeviceGroup> getAllGroups() {
        List<DeviceGroup> result = new ArrayList<DeviceGroup>(10);
        collectChildGroups(result, getRootGroup());
        
        return result;
    }
    
    public Set<? extends DeviceGroup> getGroups(YukonDevice device) {
        Set<DeviceGroup> result = new HashSet<DeviceGroup>();
        
        for (DeviceGroupProvider provider : providers.values()) {
            Set<? extends DeviceGroup> groups = provider.getGroups(device);
            result.addAll(groups);
        }
        return result;
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
    
}
