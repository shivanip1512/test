package com.cannontech.common.device.groups.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;

public class DeviceGroupServiceImpl implements DeviceGroupService {
    private DeviceGroupDao deviceGroupDao;

    public String getDeviceGroupSqlInClause(Collection<? extends DeviceGroup> groups) {
        if (groups.isEmpty()) {
            return "NULL";
        } else if (groups.size() == 1) {
            return deviceGroupDao.getDeviceGroupSqlInClause(groups.iterator().next());
        } else {
            Set<Integer> deviceIds = getDeviceIds(groups);
            String inClause = SqlStatementBuilder.convertToSqlLikeList(deviceIds);
            return inClause;
        }
    }

    private Set<? extends DeviceGroup> removeDuplicates(Collection<? extends DeviceGroup> groups) {
        //TODO this needs to remove children of parents
        return new HashSet<DeviceGroup>(groups);
    }

    public Set<Integer> getDeviceIds(Collection<? extends DeviceGroup> groups) {
        if (groups.isEmpty()) {
            return Collections.emptySet();
//        } else if (groups.size() == 1) {
//            return deviceGroupDao.getDeviceIds(groups.iterator().next());
        } else {
            groups = removeDuplicates(groups); // doesn't touch passed in collection
            Set<Integer> deviceIds = new HashSet<Integer>();
            for (DeviceGroup group: groups) {
                List<Integer> groupDeviceIds = deviceGroupDao.getDeviceIds(group);
                deviceIds.addAll(groupDeviceIds);
            }
            return deviceIds;
        }        
    }

    public Set<YukonDevice> getDevices(Collection<? extends DeviceGroup> groups) {
        if (groups.isEmpty()) {
            return Collections.emptySet();
//        } else if (groups.size() == 1) {
//            return deviceGroupDao.getDeviceIds(groups.iterator().next());
        } else {
            groups = removeDuplicates(groups); // doesn't touch passed in collection
            Set<YukonDevice> devices = new HashSet<YukonDevice>();
            for (DeviceGroup group: groups) {
                List<YukonDevice> groupDevices = deviceGroupDao.getDevices(group);
                devices.addAll(groupDevices);
            }
            return devices;
        }        
    }

    public DeviceGroup resolveGroupName(String groupName) {
        Validate.isTrue(groupName.startsWith("/"), "Group name isn't valid, must start with '/': " + groupName);
        groupName = groupName.substring(1);
        String[] strings = groupName.split("/");
        List<String> names = new LinkedList<String>(Arrays.asList(strings));
        return getRelativeGroup(getRootGroup(), names);

    }
    
    public Set<? extends DeviceGroup> resolveGroupNames(Collection<String> groupNames) throws NotFoundException {
        Collection<DeviceGroup> result = new ArrayList<DeviceGroup>(groupNames.size());
        for (String groupName : groupNames) {
            DeviceGroup group = resolveGroupName(groupName);
            result.add(group);
        }
        return removeDuplicates(result);
    }
    
    private DeviceGroup getRelativeGroup(DeviceGroup rootGroup, List<String> names) {
        if (names.isEmpty()) {
            return rootGroup;
        }
        String string = names.remove(0);
        DeviceGroup childGroup = deviceGroupDao.getGroup(rootGroup, string);
        
        return getRelativeGroup(childGroup, names);
    }
    
    public DeviceGroup getRootGroup() {
        return deviceGroupDao.getRootGroup();
    }

    @Required
    public void setDeviceGroupDao(DeviceGroupDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }
    
}
