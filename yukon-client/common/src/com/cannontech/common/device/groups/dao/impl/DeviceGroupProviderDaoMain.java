package com.cannontech.common.device.groups.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.dao.impl.providers.DeviceGroupProvider;
import com.cannontech.common.device.groups.dao.impl.providers.StaticDeviceGroupProvider;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.CaseInsensitiveMap;

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
    private Map<String, DeviceGroup> systemGroupCache = new CaseInsensitiveMap<DeviceGroup>();


    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        return getProvider(group).getChildDevices(group);
    }
    
    public int getChildDeviceCount(DeviceGroup group) {
        return getProvider(group).getChildDeviceCount(group);
    }

    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
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
    
    public synchronized DeviceGroup getGroup(DeviceGroup base, String groupName) {
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
                systemGroupCache.put(storedDeviceGroup.getFullName(), storedDeviceGroup);
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
    
    public DeviceGroup getRootGroup() {
        // the root is a special case, we always know that it is static
        return staticProvider.getRootGroup();
    }
    
    public List<DeviceGroup> getAllGroups() {
        List<DeviceGroup> result = new ArrayList<DeviceGroup>(10);
        DeviceGroup rootGroup = getRootGroup();
        result.add(rootGroup);
        List<DeviceGroup> allButRoot = getGroups(rootGroup);
        result.addAll(allButRoot);
        
        return result;
    }

    public List<DeviceGroup> getGroups(DeviceGroup group) {
        return getProvider(group).getGroups(group);
    }
    
    public Set<DeviceGroup> getGroupMembership(YukonDevice device) {
        DeviceGroup rootGroup = getRootGroup();
        return getGroupMembership(rootGroup, device);
    }
    
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

    public boolean isDeviceInGroup(DeviceGroup group, YukonDevice device) {
        return getProvider(group).isDeviceInGroup(group,device);
    }
    
    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        return getProvider(group).isChildDevice(group,device);
    }

}
