package com.cannontech.common.device.groups.service.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;

public class DeviceGroupServiceImpl implements DeviceGroupService {
    private DeviceGroupDao deviceGroupDao;

    public String getDeviceGroupSqlInClause(DeviceGroup group) {
        return deviceGroupDao.getDeviceGroupSqlInClause(group);
    }

    public List<Integer> getDeviceIds(DeviceGroup group) {
        return deviceGroupDao.getDeviceIds(group);
    }

    public List<YukonDevice> getDevices(String groupName) {
        DeviceGroup group = resolveGroupName(groupName);
        return deviceGroupDao.getDevices(group);
    }

    public List<YukonDevice> getDevices(DeviceGroup group) {
        return deviceGroupDao.getDevices(group);
    }

    public DeviceGroup resolveGroupName(String groupName) {
        Validate.isTrue(groupName.startsWith("/"), "Group name isn't valid, must start with '/': " + groupName);
        groupName = groupName.substring(1);
        String[] strings = groupName.split("/");
        List<String> names = new LinkedList<String>(Arrays.asList(strings));
        return getRelativeGroup(getRootGroup(), names);

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
