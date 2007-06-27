package com.cannontech.common.device.groups.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
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
    private DeviceGroupDao deviceGroupDao;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private SimpleJdbcOperations jdbcTemplate;
    
    public Set<YukonDevice> getDevices(FixedDeviceGroups group, String groupName) {
        String fullName = group.getPrefix() + "/" + groupName;
        
        DeviceGroup resovledGroup = deviceGroupService.resolveGroupName(fullName);
        Set<YukonDevice> devices = deviceGroupService.getDevices(Collections.singleton(resovledGroup));
        
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
        StoredDeviceGroup parentGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(group.getPrefix());
        
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
        
        StoredDeviceGroup newGroup;
        try {
            newGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(fullName);
        } catch (NotFoundException e) {
            newGroup = deviceGroupEditorDao.addGroup(parentGroup, DeviceGroupType.STATIC, groupName);
        }
        
        
        deviceGroupMemberEditorDao.addDevices(newGroup, Collections.singletonList(device));
        
    }
    
    private void stripFromGroup(StoredDeviceGroup group, YukonDevice device) {
        deviceGroupMemberEditorDao.removeDevices(group, Collections.singletonList(device));
        List<StoredDeviceGroup> childGroups = deviceGroupEditorDao.getChildGroups(group);
        for (StoredDeviceGroup group2 : childGroups) {
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
