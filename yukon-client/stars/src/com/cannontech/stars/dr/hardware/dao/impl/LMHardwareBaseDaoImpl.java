package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.dao.LMThermostatDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public class LMHardwareBaseDaoImpl implements LMHardwareBaseDao {
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectById;
    private static final String selectBySerialNumber;
    private static final String selectByLMHwardTypeId;
    private static final String selectByRouteId;
    private static final String selectByConfigurationId;
    private static final ParameterizedRowMapper<LMHardwareBase> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    private LMThermostatDao lmThermostatDao;
    private LMHardwareEventDao lmHardwareEventDao;
    private InventoryBaseDao inventoryBaseDao;
    
    static {
        updateSql = "UPDATE LMHardwareBase SET ManufacturerSerialNumber = ?, LMHardwareTypeID = ?, RouteID = ?, ConfigurationID = ? WHERE InventoryID = ?";
        
        selectAllSql = "SELECT InventoryID,ManufacturerSerialNumber,LMHardwareTypeID,RouteID,ConfigurationID from LMHardwareBase";
    
        selectById = selectAllSql + " WHERE InventoryID = ?";
        
        selectBySerialNumber = selectAllSql + " WHERE ManufacturerSerialNumber = ?";
        
        selectByLMHwardTypeId = selectAllSql + " WHERE LMHardwareTypeID = ?";
        
        selectByRouteId = selectAllSql + " WHERE RouteID = ?";
        
        selectByConfigurationId = selectAllSql + " WHERE ConfigurationID = ?";
        
        rowMapper = LMHardwareBaseDaoImpl.createRowMapper();
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(final LMHardwareBase hardwareBase) {
        int rowsAffected = simpleJdbcTemplate.update(updateSql, hardwareBase.getManufacturerSerialNumber(),
                                                                hardwareBase.getLMHarewareTypeId(),
                                                                hardwareBase.getRouteId(),
                                                                hardwareBase.getConfigurationId(),
                                                                hardwareBase.getInventoryId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LMHardwareBase getById(final int inventoryId) throws DataAccessException {
        LMHardwareBase hardwareBase = simpleJdbcTemplate.queryForObject(selectById, rowMapper, inventoryId);
        return hardwareBase;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LMHardwareBase getBySerialNumber(final String serialNumber) throws DataAccessException {
        try {
            return simpleJdbcTemplate.queryForObject(selectBySerialNumber, rowMapper, serialNumber);
        } catch(EmptyResultDataAccessException ex) {
            throw new NotFoundException("The serial number supplied does not exist.");
        }
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareBase> getByLMHardwareTypeId(final int typeId) {
        try {
            List<LMHardwareBase> list = simpleJdbcTemplate.query(selectByLMHwardTypeId, rowMapper, typeId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareBase> getByRouteId(final int routeId) {
        try {
            List<LMHardwareBase> list = simpleJdbcTemplate.query(selectByRouteId, rowMapper, routeId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }       
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareBase> getByConfigurationId(final int configurationId) {
        try {
            List<LMHardwareBase> list = simpleJdbcTemplate.query(selectByConfigurationId, rowMapper, configurationId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareBase> getAll() {
        try {
            List<LMHardwareBase> list = simpleJdbcTemplate.query(selectAllSql, rowMapper, new Object[]{});
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    @Transactional
    public void clearLMHardwareInfo(Integer inventoryId) {
        lmHardwareConfigurationDao.delete(inventoryId);
        lmThermostatDao.deleteSchedulesForInventory(inventoryId);
        lmThermostatDao.deleteManualEvents(inventoryId);
        lmHardwareEventDao.deleteHardwareToMeterMapping(inventoryId);
        inventoryBaseDao.uninstallInventory(inventoryId);
    }
    
    private static final ParameterizedRowMapper<LMHardwareBase> createRowMapper() {
        final ParameterizedRowMapper<LMHardwareBase> rowMapper = new ParameterizedRowMapper<LMHardwareBase>() {
            public LMHardwareBase mapRow(ResultSet rs, int rowNum) throws SQLException {
                LMHardwareBase hardwareBase = new LMHardwareBase();
                hardwareBase.setConfigurationId(rs.getInt("ConfigurationID"));
                hardwareBase.setInventoryId(rs.getInt("InventoryID"));
                hardwareBase.setLMHarewareTypeId(rs.getInt("LMHardwareTypeID"));
                hardwareBase.setManufactoruerSerialNumber(rs.getString("ManufacturerSerialNumber"));
                hardwareBase.setRouteId(rs.getInt("RouteID"));
                return hardwareBase;
            }
        };
        return rowMapper;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setLMHardwareConfigurationDao(LMHardwareConfigurationDao lmHardwareConfigurationDao) {
        this.lmHardwareConfigurationDao = lmHardwareConfigurationDao;
    }

    @Autowired
    public void setLMThermostatDao(LMThermostatDao lmThermostatDao) {
        this.lmThermostatDao = lmThermostatDao;
    }

    @Autowired
    public void setLMHardwareEventDao(LMHardwareEventDao lmHardwareEventDao) {
        this.lmHardwareEventDao = lmHardwareEventDao;
    }
    
    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }
    
}
