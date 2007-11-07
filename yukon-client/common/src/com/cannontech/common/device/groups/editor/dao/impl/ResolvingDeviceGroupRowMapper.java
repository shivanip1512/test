package com.cannontech.common.device.groups.editor.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

/**
 * NOTE: This class MUST be used in an '@Transactional' method. This
 * mapper does a recursive call which hits the db in each recursion. If it
 * is not transactional, each recursion will get it's own connection and we
 * will run out of connections.
 */
public class ResolvingDeviceGroupRowMapper implements ParameterizedRowMapper<StoredDeviceGroup> {
    private final DeviceGroupEditorDaoImpl groupEditorDao;
    private Map<Integer, StoredDeviceGroup> cache = new HashMap<Integer, StoredDeviceGroup>();
    
    /**
     * NOTE: This class MUST be used in an '@Transactional' method. This
     * mapper does a recursive call which hits the db in each recursion. If it
     * is not transactional, each recursion will get it's own connection and we
     * will run out of connections.
     */
    public ResolvingDeviceGroupRowMapper(DeviceGroupEditorDaoImpl groupEditorDao) {
        this.groupEditorDao = groupEditorDao;
    }

    public StoredDeviceGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
        StoredDeviceGroup group = new StoredDeviceGroup();
        
        int id = rs.getInt("devicegroupid");
        group.setId(id);
        
        String groupName = rs.getString("groupname");
        group.setName(groupName);
        
        int parentId = rs.getInt("parentdevicegroupid");
        StoredDeviceGroup parent;
        if (rs.wasNull()) {
            parent = null;
        } else if (cache.containsKey(parentId)) {
            parent = cache.get(parentId);
        } else {
            parent = groupEditorDao.getGroupById(parentId, this);
            cache.put(parentId, parent);
        }
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
