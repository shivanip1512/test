package com.cannontech.common.device.groups.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.model.StaticDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;

@Deprecated
public class FixedDeviceGroupingHack {
    private DeviceGroupService deviceGroupService;
    private DeviceGroupDao deviceGroupDao;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private SimpleJdbcOperations jdbcTemplate;
    
    public List<YukonDevice> getDevices(FixedDeviceGroups group, String groupName) {
        String fullName = group.getPrefix() + "/" + groupName;
        
        List<YukonDevice> devices = deviceGroupService.getDevices(fullName);
        
        return devices;
    }
    
    public List<String> getGroups(FixedDeviceGroups group) {
        DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(group.getPrefix());
        List<? extends DeviceGroup> childGroups = deviceGroupDao.getChildGroups(deviceGroup);
        
        ObjectMapper<DeviceGroup, String> mapper = new ObjectMapper<DeviceGroup, String>() {
            public String map(DeviceGroup from) {
                return from.getName();
            }
        };
        List<String> result = new MappingList<DeviceGroup, String>(childGroups, mapper);
        return result;
    }
    
    public void setGroup(FixedDeviceGroups group, YukonDevice device, String groupName) {
        StaticDeviceGroup parentGroup = (StaticDeviceGroup) deviceGroupService.resolveGroupName(group.getPrefix());
        
        stripFromGroup(parentGroup, device);
        if (groupName == null) {
            //TODO check if group is now empty
//            SqlStatementBuilder sql = new SqlStatementBuilder();
//            sql.append("delete from DeviceGroup");
//            sql.append("where DeviceGroupId in (");
//            sql.append("select dg.devicegroupid from DeviceGroup dg");
//            sql.append("join DeviceGroupMember dgm on dg.DeviceGroupId = dgm.DeviceGroupId");
//            sql.append("where dg.ParentDeviceGroupId = ?");
//            sql.append("group by dg.DeviceGroupId");
//            sql.append("having count(*) = 0");
//            sql.append(")");
//            
//            jdbcTemplate.update(sql.toString(), parentGroup.getId());
            return;
        }
        
        String fullName = group.getPrefix() + "/" + groupName;
        
        StaticDeviceGroup newGroup;
        try {
            newGroup = (StaticDeviceGroup) deviceGroupService.resolveGroupName(fullName);
        } catch (NotFoundException e) {
            newGroup = deviceGroupEditorDao.addGroup(parentGroup, groupName);
        }
        
        
        deviceGroupEditorDao.addDevices(newGroup, Collections.singletonList(device));
        
    }
    
    private void stripFromGroup(StaticDeviceGroup group, YukonDevice device) {
        deviceGroupEditorDao.removeDevices(group, Collections.singletonList(device));
        List<StaticDeviceGroup> childGroups = deviceGroupEditorDao.getChildGroups(group);
        for (StaticDeviceGroup group2 : childGroups) {
            stripFromGroup(group2, device);
        }
    }
    
    @Required
    public void setDeviceGroupDao(DeviceGroupDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }
    
    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
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
