package com.cannontech.common.device.groups.editor.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.Validate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class StoredDeviceGroupRowMapper implements ParameterizedRowMapper<StoredDeviceGroup> {
    private final StoredDeviceGroup parent;
    
    public StoredDeviceGroupRowMapper(StoredDeviceGroup parent) {
        this.parent = parent;
    }

    public StoredDeviceGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
        StoredDeviceGroup group = new StoredDeviceGroup();
        
        int id = rs.getInt("devicegroupid");
        group.setId(id);
        
        String groupName = rs.getString("groupname");
        group.setName(groupName);
        
        int parentId = rs.getInt("parentdevicegroupid");
        boolean matchingParent = false;
        if (parent == null) {
            matchingParent = rs.wasNull();
        } else {
            matchingParent = parentId == parent.getId();
        }
        Validate.isTrue(matchingParent, "ParentDeviceGroupId of row does not match parent " + parent);
        group.setParent(parent);
        
        String systemGroupStr = rs.getString("systemgroup");
        boolean systemGroup = systemGroupStr.equalsIgnoreCase("Y");
        group.setSystemGroup(systemGroup);
        
        String typeStr = rs.getString("type");
        DeviceGroupType type = DeviceGroupType.valueOf(typeStr);
        group.setType(type);
        
        return group;
    }

}
