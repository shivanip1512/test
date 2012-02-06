package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Propagation;
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
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;

public class LMHardwareBaseDaoImpl implements LMHardwareBaseDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    private ThermostatScheduleDao thermostatScheduleDao;
    private LMHardwareEventDao lmHardwareEventDao;
    private InventoryBaseDao inventoryBaseDao;
    private AccountThermostatScheduleDao accountThermostatScheduleDao;
    
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(final LMHardwareBase hardwareBase) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE LMHardwareBase");
        sql.append("SET ManufacturerSerialNumber =").append(hardwareBase.getManufacturerSerialNumber());
        sql.append(  ", LMHardwareTypeID =").append(hardwareBase.getLMHarewareTypeId());
        sql.append(  ", RouteID =").append(hardwareBase.getRouteId());
        sql.append(  ", ConfigurationID =").append(hardwareBase.getConfigurationId());
        sql.append("WHERE InventoryID =").append(hardwareBase.getInventoryId());
        
        int rowsAffected = yukonJdbcTemplate.update(sql.toString());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
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
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
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
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareBase> getByLMHardwareTypeId(final int typeId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryID, ManufacturerSerialNumber, LMHardwareTypeID, RouteID, ConfigurationID");
        sql.append("FROM LMHardwareBase");
        sql.append("WHERE LMHardwareTypeID").eq(typeId);
        List<LMHardwareBase> list = yukonJdbcTemplate.query(sql, rowMapper);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareBase> getByRouteId(final int routeId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryID, ManufacturerSerialNumber, LMHardwareTypeID, RouteID, ConfigurationID");
        sql.append("FROM LMHardwareBase");
        sql.append("WHERE RouteID").eq(routeId);
        List<LMHardwareBase> list = yukonJdbcTemplate.query(sql, rowMapper);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareBase> getByConfigurationId(final int configurationId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryID, ManufacturerSerialNumber, LMHardwareTypeID, RouteID, ConfigurationID");
        sql.append("FROM LMHardwareBase");
        sql.append("WHERE ConfigurationID").eq(configurationId);
        List<LMHardwareBase> list = yukonJdbcTemplate.query(sql, rowMapper);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareBase> getAll() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryID, ManufacturerSerialNumber, LMHardwareTypeID, RouteID, ConfigurationID");
        sql.append("FROM LMHardwareBase");
        List<LMHardwareBase> list = yukonJdbcTemplate.query(sql, rowMapper);
        return list;
    }
    
    @Override
    @Transactional
    public void clearLMHardwareInfo(Integer inventoryId) {
        lmHardwareConfigurationDao.delete(inventoryId);
        accountThermostatScheduleDao.deleteByInventoryId(inventoryId);
        thermostatScheduleDao.deleteManualEvents(inventoryId);
        lmHardwareEventDao.deleteHardwareToMeterMapping(inventoryId);
        inventoryBaseDao.uninstallInventory(inventoryId);
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
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setLMHardwareConfigurationDao(LMHardwareConfigurationDao lmHardwareConfigurationDao) {
        this.lmHardwareConfigurationDao = lmHardwareConfigurationDao;
    }

    @Autowired
    public void setThermostatScheduleDao(ThermostatScheduleDao thermostatScheduleDao) {
        this.thermostatScheduleDao = thermostatScheduleDao;
    }

    @Autowired
    public void setLMHardwareEventDao(LMHardwareEventDao lmHardwareEventDao) {
        this.lmHardwareEventDao = lmHardwareEventDao;
    }
    
    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }
 
    @Autowired
    public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
		this.accountThermostatScheduleDao = accountThermostatScheduleDao;
	}
}