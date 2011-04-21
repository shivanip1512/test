package com.cannontech.common.device.groups.editor.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;

public class YukonDeviceRowMapper implements ParameterizedRowMapper<SimpleDevice> {

    public SimpleDevice mapRow(ResultSet rs, int rowNum) throws SQLException {
        int deviceId = rs.getInt("paobjectid");
        String typeStr = rs.getString("type");
        PaoType paoType = PaoType.getForDbString(typeStr);
        return new SimpleDevice(deviceId, paoType);
    }

}
