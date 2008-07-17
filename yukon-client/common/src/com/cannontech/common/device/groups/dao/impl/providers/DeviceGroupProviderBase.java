package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.MappingSet;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.NotFoundException;

/**
 * Provides a base implementation of DeviceGroupProvider. For the most part, each of 
 * the "child" methods is abstract. This class then implements the recursive part (e.g. 
 * getDevices() is implemented on top of getChildGroups() and getChildDevices()). One
 * important aspect of this class is that whenever it calls getChildDevices(), it uses
 * the mainDelegator process the result. This is because classes that extends this class
 * will be written for a specific DeviceGroupType, but they may contain sub groups
 * of another type. For this reason, the mainDelegator must be used to ensure that 
 * each group is processed by the correct provider.
 */
public abstract class DeviceGroupProviderBase implements DeviceGroupProvider {
    
    private DeviceGroupProviderDao mainDelegator;
    
    public abstract Set<YukonDevice> getChildDevices(DeviceGroup group);
    
    public abstract String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier);

    public abstract List<DeviceGroup> getChildGroups(DeviceGroup group);
    
    public abstract boolean isChildDevice(DeviceGroup group, YukonDevice device);
    
    public Set<DeviceGroup> getGroupMembership(DeviceGroup base,
            YukonDevice device) {
        Set<DeviceGroup> result = new HashSet<DeviceGroup>();
        
        // is device a direct child of base
        if (isChildDevice(base, device)) {
            result.add(base);
        }
        
        // check child groups
        List<? extends DeviceGroup> childGroups = getChildGroups(base);
        for (DeviceGroup childGroup : childGroups) {
             Set<DeviceGroup> childGroupMembership = mainDelegator.getGroupMembership(childGroup, device);
             result.addAll(childGroupMembership);
        }
        
        return result;
    }
    
    public int getChildDeviceCount(DeviceGroup group) {
        return getChildDevices(group).size();
    }
    
    public String getDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        List<String> whereClauseList = new ArrayList<String>();
        whereClauseList.add(getChildDeviceGroupSqlWhereClause(group, identifier));
        
        List<? extends DeviceGroup> childGroups = getChildGroups(group);
        for (DeviceGroup childGroup : childGroups) {
            whereClauseList.add(mainDelegator.getDeviceGroupSqlWhereClause(childGroup, identifier));
        }
        String whereClause = StringUtils.join(whereClauseList, " OR ");
        return whereClause;
    }
    
    public DeviceGroup getGroup(DeviceGroup base, String groupName) {
        List<? extends DeviceGroup> childGroups = getChildGroups(base);
        for (DeviceGroup group : childGroups) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return group;
            }
        }
        throw new NotFoundException("Group " + groupName + " wasn't found under " + base);
    }
    
    public int getDeviceCount(DeviceGroup group) {
        // correct, but slow
        return getDevices(group).size();
    }

    public Set<Integer> getDeviceIds(DeviceGroup group) {
        Set<YukonDevice> devices = getDevices(group);
        Set<Integer> idList = new MappingSet<YukonDevice, Integer>(devices, new ObjectMapper<YukonDevice, Integer>() {
            public Integer map(YukonDevice from) {
                return from.getDeviceId();
            }
        });
        return idList;
    }

    public Set<YukonDevice> getDevices(DeviceGroup group) {
        // we should consider rewriting this using getDeviceGroupSqlWhereClause()
        Set<YukonDevice> deviceSet = new HashSet<YukonDevice>();

        // Get child devices
        Set<YukonDevice> childDeviceList = getChildDevices(group);
        deviceSet.addAll(childDeviceList);
        
        // Get child group's devices
        List<? extends DeviceGroup> childGroups = getChildGroups(group);
        for (DeviceGroup childGroup : childGroups) {
            Set<YukonDevice> devices = mainDelegator.getDevices(childGroup);
            deviceSet.addAll(devices);
        }
        return deviceSet;
    }
    
    public List<DeviceGroup> getGroups(DeviceGroup group) {
        List<DeviceGroup> result = new ArrayList<DeviceGroup>(20);
        List<DeviceGroup> groups = getChildGroups(group);
        result.addAll(groups);
        for (DeviceGroup childGroup : groups) {
            List<DeviceGroup> grandChildren = mainDelegator.getGroups(childGroup);
            result.addAll(grandChildren);
        }
        return Collections.unmodifiableList(result);
    }
    
    @Override
    public boolean isDeviceInGroup(DeviceGroup base, YukonDevice device) {
        // is device a direct child of base
        if (isChildDevice(base, device)) {
            return true;
        }
        
        // check child groups
        List<? extends DeviceGroup> childGroups = getChildGroups(base);
        for (DeviceGroup childGroup : childGroups) {
             if (mainDelegator.isDeviceInGroup(childGroup, device)) {
                 return true;
             }
        }
        
        return false;
    }
    
    @Required
    public void setMainDelegator(DeviceGroupProviderDao mainDelegator) {
        this.mainDelegator = mainDelegator;
    }

	public DeviceGroupProviderDao getMainDelegator() {
		return mainDelegator;
	}
}
