package com.cannontech.stars.dr.hardware.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.dr.hardware.dao.HoneywellWifiThermostatDao;

public class HoneywellWifiThermostatDaoImpl implements HoneywellWifiThermostatDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public String getMacAddressByDeviceId(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT MacAddress");
        sql.append("FROM HoneywellWifiThermostat ");
        sql.append("WHERE DeviceId").eq(deviceId);

        return jdbcTemplate.queryForString(sql);
    }
}
