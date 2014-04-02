package com.cannontech.common.device.groups.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.NotFoundException;

/**
 * This class is designed to be used solely for the 3.5 release to modify the
 * existing UIs to use the new tables. Use of this class should be avoided.
 */
@Deprecated
public class FixedDeviceGroupingHack {
    private DeviceGroupService deviceGroupService;
    private DeviceGroupProviderDao deviceGroupDao;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private SimpleJdbcOperations jdbcTemplate;

    public Set<Integer> getDeviceIds(FixedDeviceGroups group, String groupName) {
        String fullName = group.getPrefix() + "/" + groupName;
        
        DeviceGroup resovledGroup = deviceGroupService.resolveGroupName(fullName);
        Set<Integer> devices = deviceGroupService.getDeviceIds(Collections.singleton(resovledGroup));
        
        return devices;
    }
    
    public List<String> getGroups(FixedDeviceGroups group) {
        DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(group.getPrefix());
        List<? extends DeviceGroup> childGroups = deviceGroupDao.getChildGroups(deviceGroup);
        
        ObjectMapper<DeviceGroup, String> mapper = new ObjectMapper<DeviceGroup, String>() {
            @Override
            public String map(DeviceGroup from) {
                return from.getName();
            }
        };
        List<String> result = new MappingList<DeviceGroup, String>(childGroups, mapper);
        return result;
    }
    
    public String setGroup(FixedDeviceGroups group, SimpleDevice device, String groupName) {
        StoredDeviceGroup parentGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(group.getPrefix());
        
        stripFromGroup(parentGroup, device);
        if (StringUtils.isEmpty(groupName)) {
            return null;
        }
        
        String fullName = group.getGroup(groupName);
        
        StoredDeviceGroup newGroup;
        try {
            newGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(fullName);
        } catch (NotFoundException e) {
            newGroup = deviceGroupEditorDao.addGroup(parentGroup, DeviceGroupType.STATIC, groupName);
        }
        
        
        deviceGroupMemberEditorDao.addDevices(newGroup, Collections.singletonList(device));
        
        return newGroup.getName(); // not the full name because this is the hacker!
        
    }
    
    /**
     * Returns the name (not full name) of the first group directly under fixedGroup
     * that contains device.
     * @param fixedGroup
     * @param device
     * @return
     */
    public String getGroupForDevice(FixedDeviceGroups fixedGroup, SimpleDevice device) {
        StoredDeviceGroup parentGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(fixedGroup.getPrefix());
        Set<StoredDeviceGroup> groups = deviceGroupMemberEditorDao.getGroups(parentGroup, device);
        for (StoredDeviceGroup aGroup : groups) {
            if (aGroup.isChildOf(parentGroup)) {
                return aGroup.getName();
            }
        }
        return null;
    }
    
    private void stripFromGroup(StoredDeviceGroup group, SimpleDevice device) {
        deviceGroupMemberEditorDao.removeDevices(group, Collections.singletonList(device));
        List<StoredDeviceGroup> childGroups = deviceGroupEditorDao.getChildGroups(group);
        for (StoredDeviceGroup group2 : childGroups) {
            stripFromGroup(group2, device);
        }
    }
    
    @Required
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }
    
    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Required
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Required
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
