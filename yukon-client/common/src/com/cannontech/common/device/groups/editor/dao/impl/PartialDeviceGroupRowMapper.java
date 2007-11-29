package com.cannontech.common.device.groups.editor.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

/**
 * This is a cheater class so that we can map rows into an object that fully represents
 * their state without taken the extra step of retrieving the parent of the DeviceGroup.
 * 
 * To efficiently resolve the parents, it is more efficient to work on an entire collection
 * that can be processed in a non-linear manner.
 */
public class PartialDeviceGroupRowMapper implements ParameterizedRowMapper<PartialDeviceGroup> {
    
    public PartialDeviceGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
        PartialDeviceGroup partialDeviceGroup = new PartialDeviceGroup();
        StoredDeviceGroup group = new StoredDeviceGroup();
        partialDeviceGroup.setStoredDeviceGroup(group);
        
        int id = rs.getInt("devicegroupid");
        group.setId(id);
        
        String groupName = rs.getString("groupname");
        group.setName(groupName);
        
        int parentId = rs.getInt("parentdevicegroupid");
        if (rs.wasNull()) {
            partialDeviceGroup.setParentGroupId(null);
        } else {
            partialDeviceGroup.setParentGroupId(parentId);
        }
        
        String systemGroupStr = rs.getString("systemgroup");
        boolean systemGroup = systemGroupStr.equalsIgnoreCase("Y");
        group.setSystemGroup(systemGroup);
        
        String typeStr = rs.getString("type");
        DeviceGroupType type = DeviceGroupType.valueOf(typeStr);
        group.setType(type);
        
        return partialDeviceGroup;
    }

}
