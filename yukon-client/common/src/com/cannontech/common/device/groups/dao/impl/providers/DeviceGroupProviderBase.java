package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.MappingList;
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
    
    public abstract List<YukonDevice> getChildDevices(DeviceGroup group);
    
    public abstract String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier);

    public abstract List<DeviceGroup> getChildGroups(DeviceGroup group);
    
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
            if (group.getName().equals(groupName)) {
                return group;
            }
        }
        throw new NotFoundException("Group " + groupName + " wasn't found under " + base);
    }
    
    public int getDeviceCount(DeviceGroup group) {
        return getDevices(group).size();
    }

    public List<Integer> getDeviceIds(DeviceGroup group) {
        List<YukonDevice> devices = getDevices(group);
        List<Integer> idList = new MappingList<YukonDevice, Integer>(devices, new ObjectMapper<YukonDevice, Integer>() {
            public Integer map(YukonDevice from) {
                return from.getDeviceId();
            }
        });
        return idList;
    }

    public List<YukonDevice> getDevices(DeviceGroup group) {
        List<YukonDevice> deviceList = new ArrayList<YukonDevice>();

        // Get child devices
        List<YukonDevice> childDeviceList = getChildDevices(group);
        deviceList.addAll(childDeviceList);
        
        // Get child group's devices
        List<? extends DeviceGroup> childGroups = getChildGroups(group);
        for (DeviceGroup childGroup : childGroups) {
            List<YukonDevice> devices = mainDelegator.getDevices(childGroup);
            deviceList.addAll(devices);
        }
        return deviceList;
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
    
    @Required
    public void setMainDelegator(DeviceGroupProviderDao mainDelegator) {
        this.mainDelegator = mainDelegator;
    }

	public DeviceGroupProviderDao getMainDelegator() {
		return mainDelegator;
	}
}
