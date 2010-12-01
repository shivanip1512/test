package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public class InventoryBaseDaoImpl implements InventoryBaseDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    private static final String selectByAccountIdSql;
    private static final String selectByInstallationCompanyIdSql;
    private static final String selectByCategoryIdSql;
    private static final String selectByDeviceIdSql;
    
    private static final ParameterizedRowMapper<InventoryBase> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private LMHardwareBaseDao lmHardwareBaseDao;
    
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
        
        selectByAccountIdSql = selectAllSql + " WHERE AccountID = ?";
        
        selectByInstallationCompanyIdSql = selectAllSql + " WHERE InstallationCompanyID = ?";
        
        selectByCategoryIdSql = selectAllSql + " WHERE CategoryID = ?";
        
        selectByDeviceIdSql = selectAllSql + " WHERE DeviceId = ?";
        
        rowMapper = InventoryBaseDaoImpl.createRowMapper();
    }
    
    @Override
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
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final InventoryBase inventoryBase) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql, inventoryBase.getInventoryId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Override
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
    
    @Override
    public void uninstallInventory(Integer inventoryId) {
        Date now = new Date();
        String sql = "UPDATE InventoryBase SET AccountId = 0, RemoveDate = ? WHERE InventoryId = ?";
        simpleJdbcTemplate.update(sql, now, inventoryId);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public InventoryBase getById(final int inventoryId) {
        InventoryBase inventoryBase = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, inventoryId);
        return inventoryBase;
    }
    
    @Override
    public InventoryBase findById(int inventoryId) {
        try {
            return getById(inventoryId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Map<Integer, InventoryBase> getByIds(List<Integer> inventoryIdList) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(simpleJdbcTemplate);
        
        List<InventoryBase> list = template.query(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append(selectAllSql);
                sqlBuilder.append("WHERE InventoryID IN (");
                sqlBuilder.appendArgumentList(subList);
                sqlBuilder.append(")");
                sqlBuilder.append("ORDER BY InventoryID");
                return sqlBuilder;
            }
        }, inventoryIdList, rowMapper);
        
        final Map<Integer, InventoryBase> resultMap = new HashMap<Integer, InventoryBase>(list.size());
        for (final InventoryBase inventoryBase : list) {
            Integer key = inventoryBase.getInventoryId();
            resultMap.put(key, inventoryBase);
        }
        return resultMap;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<InventoryBase> getByAccountId(final int accountId) {
        List<InventoryBase> list = simpleJdbcTemplate.query(selectByAccountIdSql, rowMapper, accountId);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getInventoryIdsByAccountId(int accountId) {
        final String sql = "SELECT InventoryID FROM InventoryBase WHERE AccountID = ?";
        final ParameterizedRowMapper<Integer> idRowMapper = new ParameterizedRowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("InventoryID");
            }
        };
        
        List<Integer> inventoryIdList = simpleJdbcTemplate.query(sql, idRowMapper, accountId);
        return inventoryIdList;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<InventoryBase> getDRInventoryByAccountId(int accountId) {
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ib.InventoryID,AccountID,InstallationCompanyID,CategoryID,ReceiveDate,");
        sqlBuilder.append("       InstallDate,RemoveDate,AlternateTrackingNumber,VoltageID,Notes,DeviceID,");
        sqlBuilder.append("       DeviceLabel,CurrentStateID");
        sqlBuilder.append("FROM InventoryBase ib,LMHardwareBase lmhb");
        sqlBuilder.append("WHERE ib.InventoryID = lmhb.InventoryID");
        sqlBuilder.append("AND AccountID = ?");
        String sql = sqlBuilder.toString();
        
        List<InventoryBase> list = simpleJdbcTemplate.query(sql, rowMapper, accountId);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<InventoryBase> getByInstallationCompanyId(final int companyId) {
        List<InventoryBase> list = simpleJdbcTemplate.query(selectByInstallationCompanyIdSql, rowMapper, companyId);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<InventoryBase> getByCategoryId(final int categoryId) {
        List<InventoryBase> list = simpleJdbcTemplate.query(selectByCategoryIdSql, rowMapper, categoryId);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<InventoryBase> getAll() {
        List<InventoryBase> list = simpleJdbcTemplate.query(selectAllSql, rowMapper);
        return list;
    }
    
    @Override
    public String getDisplayName(InventoryBase inventory) {
        String displayName;
        String deviceLabel = inventory.getDeviceLabel();
        if (!StringUtils.isBlank(deviceLabel)) {
            displayName = deviceLabel;
        } else {
            LMHardwareBase hardware = lmHardwareBaseDao.getById(inventory.getInventoryId());
            displayName = hardware.getManufacturerSerialNumber();
        }
        return displayName;
    }
    
    @Override
    public List<InventoryBase> getByDeviceId(int deviceId) {
    	
        List<InventoryBase> list = simpleJdbcTemplate.query(selectByDeviceIdSql, rowMapper, deviceId);
        return list;
    }
    
    private static final ParameterizedRowMapper<InventoryBase> createRowMapper() {
        ParameterizedRowMapper<InventoryBase> rowMapper = new ParameterizedRowMapper<InventoryBase>() {
            public InventoryBase mapRow(ResultSet rs, int rowNum) throws SQLException {
                InventoryBase inventoryBase = new InventoryBase();
                inventoryBase.setAccountId(rs.getInt("AccountID"));
                inventoryBase.setAlternateTrackingNumber(SqlUtils.convertDbValueToString(rs.getString("AlternateTrackingNumber")));
                inventoryBase.setCategoryId(rs.getInt("CategoryID"));
                inventoryBase.setCurrentStateId(rs.getInt("CurrentStateID"));
                inventoryBase.setDeviceId(rs.getInt("DeviceID"));
                inventoryBase.setDeviceLabel(SqlUtils.convertDbValueToString(rs.getString("DeviceLabel")));
                inventoryBase.setInstallationCompanyId(rs.getInt("InstallationCompanyID"));
                inventoryBase.setInstallDate(rs.getTimestamp("InstallDate"));
                inventoryBase.setInventoryId(rs.getInt("InventoryID"));
                inventoryBase.setNotes(SqlUtils.convertDbValueToString(rs.getString("Notes")));
                inventoryBase.setReceiveDate(rs.getTimestamp("ReceiveDate"));
                inventoryBase.setRemoveDate(rs.getTimestamp("RemoveDate"));
                inventoryBase.setVoltageId(rs.getInt("VoltageID"));
                return inventoryBase;
            }
        };
        return rowMapper;
    }
    
    @Autowired
    public void setNextValueHelper(final NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
}
