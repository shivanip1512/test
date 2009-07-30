package com.cannontech.common.device.groups.editor.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class YukonDeviceRowMapper implements ParameterizedRowMapper<SimpleDevice> {
    private PaoGroupsWrapper paoGroupsWrapper;
    
    public YukonDeviceRowMapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }

    public SimpleDevice mapRow(ResultSet rs, int rowNum) throws SQLException {
        int deviceId = rs.getInt("paobjectid");
        String typeStr = rs.getString("type");
        int type = paoGroupsWrapper.getDeviceType(typeStr);
        return new SimpleDevice(deviceId, type);
    }

}
