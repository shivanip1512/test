package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.dr.honeywellWifi.model.HoneywellWifiThermostat;
import com.cannontech.stars.dr.hardware.dao.HoneywellWifiThermostatDao;

public class HoneywellWifiThermostatDaoImpl implements HoneywellWifiThermostatDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private static final YukonRowMapper<HoneywellWifiThermostat> honeywellWifiThermostatRowMapper =
        new YukonRowMapper<HoneywellWifiThermostat>() {
            @Override
            public HoneywellWifiThermostat mapRow(YukonResultSet rs) throws SQLException {
                HoneywellWifiThermostat honeywellWifiThermostat = new HoneywellWifiThermostat();
                honeywellWifiThermostat.setMacAddress(rs.getString("MacAddress"));
                honeywellWifiThermostat.setDeviceVendorUserId(rs.getInt("UserId"));

                return honeywellWifiThermostat;
            }
        };

    @Override
    public HoneywellWifiThermostat getHoneywellWifiThermostat(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT MacAddress, UserId");
        sql.append("FROM HoneywellWifiThermostat ");
        sql.append("WHERE DeviceId").eq(deviceId);
        HoneywellWifiThermostat honeywellWifiThermostat =
            jdbcTemplate.queryForObject(sql, honeywellWifiThermostatRowMapper);
        return honeywellWifiThermostat;
    }

    @Override
    public PaoIdentifier getPaoIdentifierByMacId(String macId) throws NotFoundException {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PaObjectId, Type");
        sql.append("FROM YukonPaObject ypo");
        sql.append("JOIN HoneywellWifiThermostat hwt ON hwt.DeviceId = ypo.PaObjectId");
        sql.append("WHERE hwt.MacAddress").eq(macId);
        
        try {
            PaoIdentifier paoIdentifier = jdbcTemplate.queryForObject(sql, TypeRowMapper.PAO_IDENTIFIER);
            return paoIdentifier;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A pao with MAC ID " + macId + " cannot be found.");
        }
    }
}
