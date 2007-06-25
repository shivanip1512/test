package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;

public abstract class DeviceGroupDaoBase implements DeviceGroupDao {
    private DeviceGroupDao mainDelegator;

    public abstract List<YukonDevice> getChildDevices(DeviceGroup group);

    public abstract List<? extends DeviceGroup> getChildGroups(DeviceGroup group);
    
    public DeviceGroup getGroup(DeviceGroup base, String groupName) {
        List<? extends DeviceGroup> childGroups = getChildGroups(base);
        for (DeviceGroup group : childGroups) {
            if (group.getName().equals(groupName)) {
                return group;
            }
        }
        throw new NotFoundException("Group " + groupName + " wasn't found under " + base);
    }

    public String getDeviceGroupSqlInClause(DeviceGroup group) {
        List<Integer> devices = getDeviceIds(group);
        String idString = SqlStatementBuilder.convertToSqlLikeList(devices);
        return idString;
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
        List<YukonDevice> deviceList = getChildDevices(group);
        List<? extends DeviceGroup> childGroups = getChildGroups(group);
        for (DeviceGroup childGroup : childGroups) {
            List<YukonDevice> devices = mainDelegator.getDevices(childGroup);
            deviceList.addAll(devices);
        }
        return deviceList;
    }
    
    public DeviceGroup getRootGroup() {
        throw new UnsupportedOperationException();
    }

}
