package com.cannontech.common.device.groups.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.dao.impl.providers.DeviceGroupProvider;
import com.cannontech.common.device.groups.dao.impl.providers.StaticDeviceGroupProvider;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.predicate.Predicate;
import com.google.common.collect.MapMaker;

/**
 * This class serves as a delegation point between the DeviceGroupProviderDao
 * and the associated DeviceGroupProvider implementations. When a method
 * is invoked on this class (with the exception of a few special cases), the 
 * actual provider implementation is looked up based on the type of the device
 * and then the same method is invoked on the provider.
 */
public class DeviceGroupProviderDaoMain implements DeviceGroupProviderDao {
    private Map<DeviceGroupType, DeviceGroupProvider> providers;
    private StaticDeviceGroupProvider staticProvider;
    private ConcurrentMap<String, DeviceGroup> systemGroupCache = new MapMaker().concurrencyLevel(10).makeMap();


    @Override
    public Set<SimpleDevice> getChildDevices(DeviceGroup group) {
        return getProvider(group).getChildDevices(group);
    }
    
    @Override
    public Set<SimpleDevice> getChildDevices(DeviceGroup group, int maxSize) {
    	return getProvider(group).getChildDevices(group, maxSize);
    }
    
    @Override
    public int getChildDeviceCount(DeviceGroup group) {
        return getProvider(group).getChildDeviceCount(group);
    }

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        return getProvider(group).getChildGroups(group);
    }

    @Override
    public SqlFragmentSource getDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        return getProvider(group).getDeviceGroupSqlWhereClause(group, identifier);
    }

    @Override
    public SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        return getProvider(group).getChildDeviceGroupSqlWhereClause(group, identifier);
    }

    @Override
    public Set<Integer> getDeviceIds(DeviceGroup group) {
        return getProvider(group).getDeviceIds(group);
    }

    @Override
    public Set<SimpleDevice> getDevices(DeviceGroup group) {
        return getProvider(group).getDevices(group);
    }
    
    @Override
    public boolean doesGroupDefinitelyContainAllDevices(DeviceGroup group) {
        return getProvider(group).doesGroupDefinitelyContainAllDevices(group);
    }
    
    @Override
    public void collectDevices(DeviceGroup group, Set<SimpleDevice> deviceSet, int maxSize) {
        getProvider(group).collectDevices(group, deviceSet, maxSize);
    }

    @Override
    public int getDeviceCount(DeviceGroup group) {
        return getProvider(group).getDeviceCount(group);
    }
    
    @Override
    public DeviceGroup getGroup(DeviceGroup base, String groupName) {
        // check cache
        String presumedName = getGroupFullName(base, groupName);
        DeviceGroup deviceGroup = systemGroupCache.get(presumedName);
        if (deviceGroup != null) {
            return deviceGroup;
        }
        
        // not in system cache, go look it up
        DeviceGroup group = getProvider(base).getGroup(base, groupName);
        
        // if this is a non-editable group, let's cache it
        if (group instanceof StoredDeviceGroup) {
            StoredDeviceGroup storedDeviceGroup = (StoredDeviceGroup) group;
            if (!storedDeviceGroup.isEditable()) {
                // cache using the "presumed" name instead of the actual name
                // so that subsequent calls result in cache hits even if 
                // the case of the actual group name is different
                systemGroupCache.putIfAbsent(presumedName, storedDeviceGroup);
            }
        }
        return group;
    }

    private String getGroupFullName(DeviceGroup base, String groupName) {
        if (base.getParent() == null) {
            return "/" + groupName;
        } else {
            return base.getFullName() + "/" + groupName;
        }
    }
    
    @Override
    public DeviceGroup getRootGroup() {
        // the root is a special case, we always know that it is static
        return staticProvider.getRootGroup();
    }
    
    @Override
    public List<DeviceGroup> getAllGroups() {
        List<DeviceGroup> result = new ArrayList<DeviceGroup>(10);
        DeviceGroup rootGroup = getRootGroup();
        List<DeviceGroup> allGroups = getGroups(rootGroup);
        result.addAll(allGroups);
        return result;
    }

    @Override
    public List<DeviceGroup> getGroups(DeviceGroup group) {
        return getProvider(group).getGroups(group);
    }
    
    @Override
    public Set<DeviceGroup> getGroupMembership(YukonDevice device) {
        DeviceGroup rootGroup = getRootGroup();
        return getGroupMembership(rootGroup, device);
    }
    
    @Override
    public Set<DeviceGroup> getGroupMembership(DeviceGroup base, YukonDevice device) {
        DeviceGroupProvider provider = getProvider(base);
        Set<DeviceGroup> groups = provider.getGroupMembership(base, device);
        
        return groups;
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

    @Override
    public boolean isDeviceInGroup(DeviceGroup group, YukonDevice device) {
        return getProvider(group).isDeviceInGroup(group,device);
    }
    
    @Override
    public boolean isGroupCanMoveUnderGroup(DeviceGroup groupToMove, DeviceGroup proposedParent) {
        return getProvider(groupToMove).isGroupCanMoveUnderGroup(groupToMove, proposedParent);
    }
    
    @Override
    public Predicate<DeviceGroup> getGroupCanMovePredicate(DeviceGroup groupToMove) {
        return getProvider(groupToMove).getGroupCanMovePredicate(groupToMove);
    }
    
    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        return getProvider(group).isChildDevice(group,device);
    }

}
