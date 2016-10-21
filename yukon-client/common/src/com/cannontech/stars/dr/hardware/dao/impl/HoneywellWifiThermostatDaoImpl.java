package com.cannontech.stars.dr.hardware.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TypeRowMapper;
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
