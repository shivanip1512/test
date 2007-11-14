package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;

public class LMHardwareControlGroupDaoImpl implements LMHardwareControlGroupDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectById;
    private static final String selectByLMGroupId;
    private static final String selectByInventoryId;
    private static final String selectByAccountId;
    private static final String selectByEnrollStartRange;
    private static final String selectByEnrollStopRange;
    private static final String selectByOptOutStartRange;
    private static final String selectByOptOutStopRange;
    private static final String selectByInventoryIdAndGroupIdAndAccountId;
    private static final ParameterizedRowMapper<LMHardwareControlGroup> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    static {
        
        insertSql = "INSERT INTO LMHardwareControlGroup (ControlEntryId, InventoryId, LMGroupId, AccountId, GroupEnrollStart, " +
                "GroupEnrollStop, OptOutStart, OptOutStop) VALUES (?,?,?,?,?,?,?,?)";
        
        removeSql = "DELETE FROM LMHardwareControlGroup WHERE ControlEntryID = ?";
         
        updateSql = "UPDATE LMHardwareControlGroup SET InventoryID = ?, LMGroupID = ?, AccountID = ?, GroupEnrollStart = ?, " +
                "GroupEnrollStop = ?, OptOutStart = ?, OptOutStop = ? WHERE ControlEntryID = ?";
        
        selectAllSql = "SELECT ControlEntryId, InventoryId, LMGroupId, AccountId, GroupEnrollStart, GroupEnrollStop, OptOutStart, " +
                "OptOutStop from LMHardwareControlGroup";
    
        selectById = selectAllSql + " WHERE ControlEntryID = ?";
        
        selectByLMGroupId = selectAllSql + " WHERE LMGroupID = ?";
        
        selectByInventoryId = selectAllSql + " WHERE InventoryID = ?";
        
        selectByAccountId = selectAllSql + " WHERE AccountID = ?";
        
        selectByEnrollStartRange = selectAllSql + " WHERE GroupEnrollStart > ? AND GroupEnrollStart <= ?";
        
        selectByEnrollStopRange = selectAllSql + " WHERE GroupEnrollStop > ? AND GroupEnrollStop <= ?";
        
        selectByOptOutStartRange = selectAllSql + " WHERE OptOutStart > ? AND OptOutStart <= ?";
        
        selectByOptOutStopRange = selectAllSql + " WHERE OptOutStop > ? AND OptOutStop <= ?";
        
        selectByInventoryIdAndGroupIdAndAccountId = selectAllSql + " WHERE InventoryID = ? AND LMGroupID = ? AND AccountID = ?";
        
        rowMapper = LMHardwareControlGroupDaoImpl.createRowMapper();
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(final LMHardwareControlGroup hardwareControlGroup) {
        int nextId = nextValueHelper.getNextValue("LMHardwareControlGroup");
        hardwareControlGroup.setControlEntryId(nextId);
        
        int rowsAffected = simpleJdbcTemplate.update(insertSql, hardwareControlGroup.getControlEntryId(),
                                                                hardwareControlGroup.getInventoryId(),
                                                                hardwareControlGroup.getLMGroupId(),
                                                                hardwareControlGroup.getAccountId(),
                                                                hardwareControlGroup.getGroupEnrollStart(),
                                                                hardwareControlGroup.getGroupEnrollStop(),
                                                                hardwareControlGroup.getOptOutStart(),
                                                                hardwareControlGroup.getOptOutStop());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final LMHardwareControlGroup hardwareControlGroup) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql, hardwareControlGroup.getInventoryId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(final LMHardwareControlGroup hardwareControlGroup) {
        int rowsAffected = simpleJdbcTemplate.update(updateSql, hardwareControlGroup.getInventoryId(),
                                                     hardwareControlGroup.getLMGroupId(),
                                                     hardwareControlGroup.getAccountId(),
                                                     hardwareControlGroup.getGroupEnrollStart(),
                                                     hardwareControlGroup.getGroupEnrollStop(),
                                                     hardwareControlGroup.getOptOutStart(),
                                                     hardwareControlGroup.getOptOutStop(),
                                                     hardwareControlGroup.getControlEntryId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LMHardwareControlGroup getById(final int inventoryId) throws DataAccessException {
        LMHardwareControlGroup hardwareControlGroup = simpleJdbcTemplate.queryForObject(selectById, rowMapper, inventoryId);
        return hardwareControlGroup;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByLMGroupId(final int groupId) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByLMGroupId, rowMapper, groupId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByInventoryId(final int inventoryId) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByInventoryId, rowMapper, inventoryId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }       
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByAccountId(final int accountId) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByAccountId, rowMapper, accountId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByEnrollmentStartDateRange(Date first, Date second) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByEnrollStartRange, rowMapper, first, second);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByEnrollmentStopDateRange(Date first, Date second) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByEnrollStopRange, rowMapper, first, second);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByOptOutStartDateRange(Date first, Date second) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByOptOutStartRange, rowMapper, first, second);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByOptOutStopDateRange(Date first, Date second) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByOptOutStopRange, rowMapper, first, second);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LMHardwareControlGroup getByInventoryIdAndGroupIdAndAccountId(int inventoryId, int lmGroupId, int accountId) {
        LMHardwareControlGroup hardwareControlGroup = simpleJdbcTemplate.queryForObject(selectByInventoryIdAndGroupIdAndAccountId, rowMapper, inventoryId, lmGroupId, accountId);
        return hardwareControlGroup;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getAll() {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectAllSql, rowMapper, new Object[]{});
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    private static final ParameterizedRowMapper<LMHardwareControlGroup> createRowMapper() {
        final ParameterizedRowMapper<LMHardwareControlGroup> rowMapper = new ParameterizedRowMapper<LMHardwareControlGroup>() {
            public LMHardwareControlGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                LMHardwareControlGroup hardwareControlGroup = new LMHardwareControlGroup();
                hardwareControlGroup.setControlEntryId(rs.getInt("ControlEntryID"));
                hardwareControlGroup.setInventoryId(rs.getInt("InventoryID"));
                hardwareControlGroup.setLMGroupId(rs.getInt("LMGroupID"));
                hardwareControlGroup.setAccountId(rs.getInt("AccountID"));
                hardwareControlGroup.setGroupEnrollStart(rs.getDate("GroupEnrollStart"));
                hardwareControlGroup.setGroupEnrollStop(rs.getDate("GroupEnrollStop"));
                hardwareControlGroup.setOptOutStart(rs.getDate("OptOutStart"));
                hardwareControlGroup.setOptOutStop(rs.getDate("OptOutStop"));
                return hardwareControlGroup;
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
