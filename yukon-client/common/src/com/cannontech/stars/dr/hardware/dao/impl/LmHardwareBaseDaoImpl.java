package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;

public class LmHardwareBaseDaoImpl implements LmHardwareBaseDao {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    @Autowired private ThermostatScheduleDao thermostatScheduleDao;
    @Autowired private LMHardwareEventDao lmHardwareEventDao;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    
    private static final YukonRowMapper<LMHardwareBase> rowMapper = new YukonRowMapper<LMHardwareBase>() {
        public LMHardwareBase mapRow(YukonResultSet rs) throws SQLException {
            LMHardwareBase hardwareBase = new LMHardwareBase();
            hardwareBase.setConfigurationId(rs.getInt("ConfigurationID"));
            hardwareBase.setInventoryId(rs.getInt("InventoryID"));
            hardwareBase.setLMHarewareTypeId(rs.getInt("LMHardwareTypeID"));
            hardwareBase.setManufacturerSerialNumber(rs.getString("ManufacturerSerialNumber"));
            hardwareBase.setRouteId(rs.getInt("RouteID"));
            return hardwareBase;
        }
    };
    
    @Override
    public LMHardwareBase getById(final int inventoryId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT InventoryID, ManufacturerSerialNumber, LMHardwareTypeID, RouteID, ConfigurationID");
            sql.append("FROM LMHardwareBase");
            sql.append("WHERE InventoryID").eq(inventoryId);
            
            LMHardwareBase hardwareBase = yukonJdbcTemplate.queryForObject(sql, rowMapper);
            return hardwareBase;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No LMHardware found for id:" + inventoryId);
        }
    }
    
    @Override
    public LMHardwareBase getBySerialNumber(final String serialNumber) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT InventoryID, ManufacturerSerialNumber, LMHardwareTypeID, RouteID, ConfigurationID");
            sql.append("FROM LMHardwareBase");
            sql.append("WHERE ManufacturerSerialNumber").eq(serialNumber);
            return yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch(EmptyResultDataAccessException ex) {
            throw new NotFoundException("The serial number supplied does not exist: " + serialNumber);
        }
    }
    
    @Override
    @Transactional
    public void clearLMHardwareInfo(Integer inventoryId) {
        lmHardwareConfigurationDao.delete(inventoryId);
        accountThermostatScheduleDao.deleteByInventoryId(inventoryId);
        thermostatScheduleDao.deleteManualEvents(inventoryId);
        lmHardwareEventDao.deleteHardwareToMeterMapping(inventoryId);
        
        uninstallInventory(inventoryId);
    }
    
    private void uninstallInventory(int inventoryId) {
        Date now = new Date();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE InventoryBase SET AccountId = 0, RemoveDate = ").appendArgument(now).append("WHERE InventoryId").eq(inventoryId);
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public String getSerialNumberForDevice(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT HB.ManufacturerSerialNumber SerialNumber");
        sql.append("FROM InventoryBase IB");
        sql.append(  "JOIN LMHardwareBase HB on HB.InventoryId = IB.InventoryId");
        sql.append("WHERE IB.DeviceId").eq(deviceId);
        
        String serialNumber = yukonJdbcTemplate.queryForString(sql);
        return serialNumber;
    }
    
    @Override
    public String getSerialNumberForInventoryId(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT HB.ManufacturerSerialNumber SerialNumber");
        sql.append("FROM InventoryBase IB");
        sql.append(  "JOIN LMHardwareBase HB ON HB.InventoryId = IB.InventoryId");
        sql.append("WHERE IB.InventoryId").eq(inventoryId);
        
        String serialNumber = yukonJdbcTemplate.queryForString(sql);
        return serialNumber;
    }

    @Override
    public List<String> getSerialNumberForInventoryIds(final Collection<Integer> inventoryIds) {

        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
        
        List<String> serialNumbers = template.query(new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT HB.ManufacturerSerialNumber SerialNumber");
                sql.append("FROM InventoryBase IB");
                sql.append(  "JOIN LMHardwareBase HB ON HB.InventoryId = IB.InventoryId");
                sql.append("WHERE IB.InventoryId").in(subList);
                
                return sql;
            }
        }, inventoryIds, new StringRowMapper());
        
        return serialNumbers;
    }
    
}