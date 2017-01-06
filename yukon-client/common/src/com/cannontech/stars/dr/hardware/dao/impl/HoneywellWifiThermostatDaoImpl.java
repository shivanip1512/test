package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.List;

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
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;

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
    
    @Override
    public int getHoneywellGroupId(int groupId) throws NotFoundException {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT HoneywellGroupId");
        sql.append("FROM LMGroupHoneywellWiFi");
        sql.append("WHERE deviceId").eq(groupId);
                
        try {
            int honeywellGroupId = jdbcTemplate.queryForInt(sql);
            return honeywellGroupId;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Group Id " + groupId + " cannot be found.");
        }
    }
    
    @Override
    public List<Integer> getPastEnrolledHoneywellGroupsByInventoryId(final Integer inventoryId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT hg.HoneywellGroupId OldEnrolledGroupId ");
        sql.append(" FROM LMHardwareControlGroup hcg JOIN LMGroupHoneywellWifi hg on hg.DeviceId=hcg.LMGroupId ");
        sql.append(" WHERE hcg.InventoryId").eq(inventoryId);
        sql.append(" AND hcg.Type").eq_k(LMHardwareControlGroup.ENROLLMENT_ENTRY);
        sql.append(" AND hcg.GroupEnrollStop IS NOT NULL ");
       
        return jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }
}
