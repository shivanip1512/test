package com.cannontech.common.device.groups.composed.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupComposedGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.database.RowAndFieldMapper;

public class DeviceGroupComposedGroupRowAndFieldMapper implements RowAndFieldMapper<DeviceGroupComposedGroup> {

    private DeviceGroupService deviceGroupService;
    
    public Number getPrimaryKey(DeviceGroupComposedGroup deviceGroupComposedGroup) {
        return deviceGroupComposedGroup.getDeviceGroupComposedGroupId();
    }
    
    public void setPrimaryKey(DeviceGroupComposedGroup deviceGroupComposedGroup, int value) {
        deviceGroupComposedGroup.setDeviceGroupComposedGroupId(value);
    }
    
    public void extractValues(MapSqlParameterSource p, DeviceGroupComposedGroup deviceGroupComposedGroup) {
        p.addValue("DeviceGroupComposedId", deviceGroupComposedGroup.getDeviceGroupComposedId());
        p.addValue("GroupName", deviceGroupComposedGroup.getDeviceGroup().getFullName());
        p.addValue("IsNot", deviceGroupComposedGroup.isNot() ? "Y" : "N");
    }
    
    public DeviceGroupComposedGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
        DeviceGroupComposedGroup deviceGroupComposedGroup = new DeviceGroupComposedGroup();
        deviceGroupComposedGroup.setDeviceGroupComposedGroupId(rs.getInt("DeviceGroupComposedGroupId"));
        deviceGroupComposedGroup.setDeviceGroupComposedId(rs.getInt("DeviceGroupComposedId"));

        DeviceGroup deviceGroup = deviceGroupService.findGroupName(rs.getString("GroupName"));
        deviceGroupComposedGroup.setDeviceGroup(deviceGroup); // will be set to null if group is not found
        
        deviceGroupComposedGroup.setNot(BooleanUtils.toBoolean(rs.getString("IsNot"), "Y", "N"));
        return deviceGroupComposedGroup;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
}
