package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class DeviceBaseModelRowMapper implements YukonRowMapper<DeviceBaseModel> {

    @Override
    public DeviceBaseModel mapRow(YukonResultSet rs) throws SQLException {

        Integer deviceId = rs.getInt("deviceId");
        PaoType type = rs.getEnum("type", PaoType.class);
        String deviceName = rs.getString("paoName");
        Boolean enable = !rs.getBoolean("disableFlag");

        return new DeviceBaseModel(deviceId, type, deviceName, enable);
    }
}
