package com.cannontech.common.device.groups.composed.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.device.groups.model.DeviceGroupComposed;
import com.cannontech.common.device.groups.model.DeviceGroupComposedCompositionType;
import com.cannontech.database.RowAndFieldMapper;

public class DeviceGroupComposedRowAndFieldMapper implements RowAndFieldMapper<DeviceGroupComposed> {
    
    public Number getPrimaryKey(DeviceGroupComposed deviceGroupComposed) {
        return deviceGroupComposed.getDeviceGroupComposedId();
    }
    
    public void setPrimaryKey(DeviceGroupComposed deviceGroupComposed, int value) {
        deviceGroupComposed.setDeviceGroupComposedId(value);
    }
    
    public void extractValues(MapSqlParameterSource p, DeviceGroupComposed deviceGroupComposed) {
        p.addValue("DeviceGroupId", deviceGroupComposed.getDeviceGroupId());
        p.addValue("CompositionType", deviceGroupComposed.getDeviceGroupComposedCompositionType().name());
    }
    
    public DeviceGroupComposed mapRow(ResultSet rs, int rowNum) throws SQLException {
        DeviceGroupComposed deviceGroupComposed = new DeviceGroupComposed();
        deviceGroupComposed.setDeviceGroupComposedId(rs.getInt("DeviceGroupComposedId"));
        deviceGroupComposed.setDeviceGroupId(rs.getInt("DeviceGroupId"));
        deviceGroupComposed.setDeviceGroupComposedCompositionType(DeviceGroupComposedCompositionType.valueOf(rs.getString("CompositionType")));
        return deviceGroupComposed;
    }
}
