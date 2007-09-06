package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;

public class InventoryBaseDaoImpl implements InventoryBaseDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    private static final String selectByAccountIdSql;
    private static final String selectByInstallationCompanyIdSql;
    private static final String selectByCategoryIdSql;
    private static final ParameterizedRowMapper<InventoryBase> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    static {
        
        insertSql = "INSERT INTO InventoryBase (InventoryID,AccountID,InstallationCompanyID,CategoryID,ReceiveDate," +
                    "InstallDate,RemoveDate,AlternateTrackingNumber,VoltageID,Notes,DeviceID," +
                    "DeviceLabel,CurrentStateID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        
        removeSql = "DELETE FROM InventoryBase WHERE InventoryID = ?";
        
        updateSql = "UPDATE InventoryBase SET AccountID = ?, InstallationCompanyID = ?, CategoryID = ?, ReceiveDate = ?, " +
                    "InstallDate = ?, RemoveDate = ?, AlternateTrackingNumber = ?, VoltageID = ?, Notes = ?, " +
                    "DeviceID = ?, DeviceLabel = ?, CurrentStateID = ? WHERE InventoryID = ?";
        
        selectAllSql = "SELECT InventoryID,AccountID,InstallationCompanyID,CategoryID,ReceiveDate," +
                       "InstallDate,RemoveDate,AlternateTrackingNumber,VoltageID,Notes,DeviceID," +
                       "DeviceLabel,CurrentStateID FROM InventoryBase";
        
        selectByIdSql = selectAllSql + " WHERE InventoryID = ?";
        
        selectByAccountIdSql = selectAllSql + " WHERE AccountID + ?";
        
        selectByInstallationCompanyIdSql = selectAllSql + " WHERE InstallationCompanyID = ?";
        
        selectByCategoryIdSql = selectAllSql + " WHERE CategoryID = ?";
        
        rowMapper = InventoryBaseDaoImpl.createRowMapper();
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(final InventoryBase inventoryBase) {
        int nextValue = nextValueHelper.getNextValue("InventoryBase");
        inventoryBase.setInventoryId(nextValue);
        
        int rowsAffected = simpleJdbcTemplate.update(insertSql, inventoryBase.getInventoryId(),
                                                                inventoryBase.getAccountId(),
                                                                inventoryBase.getInstallationCompanyId(),
                                                                inventoryBase.getCategoryId(),
                                                                inventoryBase.getReceiveDate(),
                                                                inventoryBase.getInstallDate(),
                                                                inventoryBase.getRemoveDate(),
                                                                inventoryBase.getAlternateTrackingNumber(),
                                                                inventoryBase.getVoltageId(),
                                                                inventoryBase.getNotes(),
                                                                inventoryBase.getDeviceId(),
                                                                inventoryBase.getDeviceLabel(),
                                                                inventoryBase.getCurrentStateId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final InventoryBase inventoryBase) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql, inventoryBase.getInventoryId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(final InventoryBase inventoryBase) {
        int rowsAffected = simpleJdbcTemplate.update(updateSql, inventoryBase.getAccountId(),
                                                                inventoryBase.getInstallationCompanyId(),
                                                                inventoryBase.getCategoryId(),
                                                                inventoryBase.getReceiveDate(),
                                                                inventoryBase.getInstallDate(),
                                                                inventoryBase.getRemoveDate(),
                                                                inventoryBase.getAlternateTrackingNumber(),
                                                                inventoryBase.getVoltageId(),
                                                                inventoryBase.getNotes(),
                                                                inventoryBase.getDeviceId(),
                                                                inventoryBase.getDeviceLabel(),
                                                                inventoryBase.getCurrentStateId(),
                                                                inventoryBase.getInventoryId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public InventoryBase getById(final int inventoryId) throws DataAccessException {
        InventoryBase inventoryBase = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, inventoryId);
        return inventoryBase;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<InventoryBase> getByAccountId(final int accountId) {
        try {
            List<InventoryBase> list = simpleJdbcTemplate.query(selectByAccountIdSql, rowMapper, accountId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<InventoryBase> getByInstallationCompanyId(final int companyId) {
        try {
            List<InventoryBase> list = simpleJdbcTemplate.query(selectByInstallationCompanyIdSql, rowMapper, companyId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<InventoryBase> getByCategoryId(final int categoryId) {
        try {
            List<InventoryBase> list = simpleJdbcTemplate.query(selectByCategoryIdSql, rowMapper, categoryId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<InventoryBase> getAll() {
        try {
            List<InventoryBase> list = simpleJdbcTemplate.query(selectAllSql, rowMapper, new Object[]{});
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    private static final ParameterizedRowMapper<InventoryBase> createRowMapper() {
        ParameterizedRowMapper<InventoryBase> rowMapper = new ParameterizedRowMapper<InventoryBase>() {
            public InventoryBase mapRow(ResultSet rs, int rowNum) throws SQLException {
                InventoryBase inventoryBase = new InventoryBase();
                inventoryBase.setAccountId(rs.getInt("AccountID"));
                inventoryBase.setAlternateTrackingNumber(rs.getString("AlternateTrackingNumber"));
                inventoryBase.setCategoryId(rs.getInt("CategoryID"));
                inventoryBase.setCurrentStateId(rs.getInt("CurrentStateID"));
                inventoryBase.setDeviceId(rs.getInt("DeviceID"));
                inventoryBase.setDeviceLabel(rs.getString("DeviceLabel"));
                inventoryBase.setInstallationCompanyId(rs.getInt("InstallationCompanyID"));
                inventoryBase.setInstallDate(rs.getTimestamp("InstallDate"));
                inventoryBase.setInventoryId(rs.getInt("InventoryID"));
                inventoryBase.setNotes(rs.getString("Notes"));
                inventoryBase.setReceiveDate(rs.getTimestamp("ReceiveDate"));
                inventoryBase.setRemoveDate(rs.getTimestamp("RemoveDate"));
                inventoryBase.setVoltageId(rs.getInt("VoltageID"));
                return inventoryBase;
            }
        };
        return rowMapper;
    }
    
    public void setNextValueHelper(final NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}
