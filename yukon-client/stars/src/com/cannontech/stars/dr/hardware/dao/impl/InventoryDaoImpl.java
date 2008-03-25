package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;

/**
 * Implementation class for InventoryDao
 */
public class InventoryDaoImpl implements InventoryDao {

    // Static list of thermostat device types
    private static List<Integer> THERMOSTAT_TYPES = new ArrayList<Integer>();
    static {
        THERMOSTAT_TYPES.add(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT);
        THERMOSTAT_TYPES.add(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT);
        THERMOSTAT_TYPES.add(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP);
    }

    private SimpleJdbcTemplate jdbcTemplate = null;

    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Thermostat> getThermostatsByAccount(int accountId) {

        String thermostatTypes = SqlStatementBuilder.convertToSqlLikeList(THERMOSTAT_TYPES);
        String sql = "SELECT ib.*, lmhb.* " + " FROM InventoryBase ib, LMHardwareBase lmhb " + " WHERE ib.accountid = ? " + " AND lmhb.inventoryid = ib.inventoryid " + " AND lmhb.LMHardwareTypeID IN " + " (SELECT entryid FROM YukonListEntry WHERE YukonDefinitionID IN (" + thermostatTypes + "))";

        List<Thermostat> thermostatList = jdbcTemplate.query(sql,
                                                             new ThermostatRowMapper(),
                                                             accountId);

        return thermostatList;
    }

    /**
     * Mapper class to map a resultset row into a thermostat
     */
    private class ThermostatRowMapper implements
            ParameterizedRowMapper<Thermostat> {

        @Override
        public Thermostat mapRow(ResultSet rs, int rowNum) throws SQLException {

            Thermostat thermostat = new Thermostat();

            int id = rs.getInt("InventoryID");
            thermostat.setId(id);

            String serialNumber = rs.getString("ManufacturerSerialNumber");
            thermostat.setSerialNumber(serialNumber);

            String deviceLabel = rs.getString("DeviceLabel");
            thermostat.setDeviceLabel(deviceLabel);

            return thermostat;
        }

    }

}
