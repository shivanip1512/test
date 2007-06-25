package com.cannontech.common.device.groups.editor.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class YukonDeviceRowMapper implements ParameterizedRowMapper<YukonDevice> {
    private PaoGroupsWrapper paoGroupsWrapper;
    
    public YukonDeviceRowMapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }

    public YukonDevice mapRow(ResultSet rs, int rowNum) throws SQLException {
        int deviceId = rs.getInt("paobjectid");
        String typeStr = rs.getString("type");
        int type = paoGroupsWrapper.getDeviceType(typeStr);
        return new YukonDevice(deviceId, type);
    }

}
