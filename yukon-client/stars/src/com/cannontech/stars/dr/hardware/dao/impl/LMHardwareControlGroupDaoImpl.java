package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;

public class LMHardwareControlGroupDaoImpl implements LMHardwareControlGroupDao, InitializingBean {
    private static final String removeSql;
    private static final String selectAllSql;
    private static final String selectById;
    private static final String selectByLMGroupId;
    private static final String selectByInventoryId;
    private static final String selectByAccountId;
    private static final String selectByLMGroupIdAndAccountIdAndType; 
    private static final String selectByEnrollStartRange;
    private static final String selectByEnrollStopRange;
    private static final String selectByOptOutStartRange;
    private static final String selectByOptOutStopRange;
    private static final String selectCurrentOptOutsByInventoryIdAndGroupIdAndAccountId;
    private static final String selectByInventoryIdAndGroupIdAndAccountId;
    private static final String selectByInventoryIdAndGroupIdAndAccountIdAndType;
    private static final String selectAllByEnergyCompanyId;
    private static final ParameterizedRowMapper<LMHardwareControlGroup> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<LMHardwareControlGroup> template;
    
    private static final String TABLE_NAME = "LMHardwareControlGroup";
    private static final String selectOldInventoryLoadGroupConfigInfo; 
    private static final String selectOldInventoryConfigInfo; 
    private static final ParameterizedRowMapper<LMHardwareConfiguration> oldControlInfoRowMapper;
    
    private static final String selectDistinctGroupIdByAccountId;
    private static final ParameterizedRowMapper<Integer> groupIdRowMapper;
    
    static {
        
        removeSql = "DELETE from " + TABLE_NAME + " WHERE ControlEntryId = ?";
         
        selectAllSql = "SELECT ControlEntryId, InventoryId, LMGroupId, AccountId, GroupEnrollStart, GroupEnrollStop, OptOutStart, " +
                "OptOutStop, Type, Relay, UserIdFirstAction, UserIdSecondAction FROM " + TABLE_NAME;
    
        selectById = selectAllSql + " WHERE ControlEntryId = ?";
        
        selectByLMGroupId = selectAllSql + " WHERE LMGroupId = ?";
        
        selectByInventoryId = selectAllSql + " WHERE InventoryId = ?";
        
        selectByAccountId = selectAllSql + " WHERE AccountId = ?";
        
        selectDistinctGroupIdByAccountId = "SELECT DISTINCT LMGroupId FROM " + TABLE_NAME + " WHERE AccountId = ?";
        
        selectByLMGroupIdAndAccountIdAndType = selectAllSql + " WHERE LMGroupId = ? AND AccountId = ? AND Type = ? ORDER BY GroupEnrollStart";
        
        selectByEnrollStartRange = selectAllSql + " WHERE GroupEnrollStart > ? AND GroupEnrollStart <= ?";
        
        selectByEnrollStopRange = selectAllSql + " WHERE GroupEnrollStop > ? AND GroupEnrollStop <= ?";
        
        selectByOptOutStartRange = selectAllSql + " WHERE OptOutStart > ? AND OptOutStart <= ?";
        
        selectByOptOutStopRange = selectAllSql + " WHERE OptOutStop > ? AND OptOutStop <= ?";
        
        selectCurrentOptOutsByInventoryIdAndGroupIdAndAccountId = selectAllSql + " WHERE LMGroupId = ? AND AccountId = ? AND OptOutStop IS NULL AND NOT OptOutStart IS NULL";
        
        selectByInventoryIdAndGroupIdAndAccountId = selectAllSql + " WHERE InventoryId = ? AND LMGroupId = ? AND AccountId = ?";
        
        selectByInventoryIdAndGroupIdAndAccountIdAndType = selectAllSql + " WHERE InventoryId = ? AND LMGroupId = ? AND AccountId = ? AND Type = ?";
        
        selectOldInventoryConfigInfo = "SELECT InventoryId, ApplianceId, AddressingGroupId, LoadNumber FROM LMHardwareConfiguration WHERE InventoryId = ?";
        
        selectOldInventoryLoadGroupConfigInfo = "SELECT InventoryId, ApplianceId, AddressingGroupId, LoadNumber FROM LMHardwareConfiguration WHERE InventoryId = ? and AddressingGroupId = ?";
        
        selectAllByEnergyCompanyId = selectAllSql + " WHERE AccountId IN (SELECT AccountId FROM ECToGenericMapping WHERE EnergyCompanyId = ?) ORDER BY AccountId";
        
        rowMapper = LMHardwareControlGroupDaoImpl.createRowMapper();
        
        oldControlInfoRowMapper = LMHardwareControlGroupDaoImpl.createOldConfigInfoRowMapper();
        
        groupIdRowMapper = LMHardwareControlGroupDaoImpl.createGroupIdRowMapper();
        
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void add(final LMHardwareControlGroup hardwareControlGroup) throws Exception {
        template.insert(hardwareControlGroup);
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final LMHardwareControlGroup hardwareControlGroup) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql, hardwareControlGroup.getInventoryId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void update(final LMHardwareControlGroup hardwareControlGroup) throws Exception {
        template.update(hardwareControlGroup);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LMHardwareControlGroup getById(final int controlEntryId) throws DataAccessException {
        LMHardwareControlGroup hardwareControlGroup = simpleJdbcTemplate.queryForObject(selectById, rowMapper, controlEntryId);
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
    public List<Integer> getDistinctGroupIdsByAccountId(final int accountId) {
        try {
            List<Integer> list = simpleJdbcTemplate.query(selectDistinctGroupIdByAccountId, groupIdRowMapper, accountId);
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
    public List<LMHardwareControlGroup> getByLMGroupIdAndAccountIdAndType(int lmGroupId, int accountId, int type) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByLMGroupIdAndAccountIdAndType, rowMapper, lmGroupId, accountId, type);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountId(int inventoryId, int lmGroupId, int accountId) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByInventoryIdAndGroupIdAndAccountId, rowMapper, inventoryId, lmGroupId, accountId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }
    
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountIdAndType(int inventoryId, int lmGroupId, int accountId, int type) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByInventoryIdAndGroupIdAndAccountIdAndType, rowMapper, inventoryId, lmGroupId, accountId, type);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }
    
    public List<LMHardwareControlGroup> getCurrentOptOutByGroupIdAndAccountId(int lmGroupId, int accountId) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectCurrentOptOutsByInventoryIdAndGroupIdAndAccountId, rowMapper, lmGroupId, accountId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
            
        }
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
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getAllByEnergyCompanyId(int energyCompanyId) {
        try {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectAllByEnergyCompanyId, rowMapper, energyCompanyId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    private static final ParameterizedRowMapper<LMHardwareControlGroup> createRowMapper() {
        final ParameterizedRowMapper<LMHardwareControlGroup> rowMapper = new ParameterizedRowMapper<LMHardwareControlGroup>() {
            public LMHardwareControlGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                LMHardwareControlGroup hardwareControlGroup = new LMHardwareControlGroup();
                hardwareControlGroup.setControlEntryId(rs.getInt("ControlEntryId"));
                hardwareControlGroup.setInventoryId(rs.getInt("InventoryId"));
                hardwareControlGroup.setLmGroupId(rs.getInt("LMGroupId"));
                hardwareControlGroup.setAccountId(rs.getInt("AccountId"));
                hardwareControlGroup.setGroupEnrollStart(rs.getTimestamp("GroupEnrollStart"));
                hardwareControlGroup.setGroupEnrollStop(rs.getTimestamp("GroupEnrollStop"));
                hardwareControlGroup.setOptOutStart(rs.getTimestamp("OptOutStart"));
                hardwareControlGroup.setOptOutStop(rs.getTimestamp("OptOutStop"));
                hardwareControlGroup.setType(rs.getInt("Type"));
                hardwareControlGroup.setRelay(rs.getInt("Relay"));
                hardwareControlGroup.setUserIdFirstAction(rs.getInt("UserIdFirstAction"));
                hardwareControlGroup.setUserIdSecondAction(rs.getInt("UserIdSecondAction"));
                return hardwareControlGroup;
            }
        };
        return rowMapper;
    }
    
    private static final ParameterizedRowMapper<LMHardwareConfiguration> createOldConfigInfoRowMapper() {
        final ParameterizedRowMapper<LMHardwareConfiguration> oldConfigInfoRowMapper = new ParameterizedRowMapper<LMHardwareConfiguration>() {
            public LMHardwareConfiguration mapRow(ResultSet rs, int rowNum) throws SQLException {
                LMHardwareConfiguration hardwareConfiguration = new LMHardwareConfiguration();
                hardwareConfiguration.setInventoryId(rs.getInt("InventoryId"));
                hardwareConfiguration.setApplianceId(rs.getInt("ApplianceId"));
                hardwareConfiguration.setAddressingGroupId(rs.getInt("AddressingGroupId"));
                hardwareConfiguration.setLoadNumber(rs.getInt("LoadNumber"));
                return hardwareConfiguration;
            }
        };
        return oldConfigInfoRowMapper;
    }
    
    private static final ParameterizedRowMapper<Integer> createGroupIdRowMapper() {
        ParameterizedRowMapper<Integer> rowMapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                int id = rs.getInt("LmGroupId");
                return Integer.valueOf(id);
            }
        };
        return rowMapper;
    }
    
    private FieldMapper<LMHardwareControlGroup> controlGroupFieldMapper = new FieldMapper<LMHardwareControlGroup>() {
        public void extractValues(MapSqlParameterSource p, LMHardwareControlGroup controlInfo) {
            p.addValue("inventoryId", controlInfo.getInventoryId());
            p.addValue("lmGroupId", controlInfo.getLmGroupId());
            p.addValue("accountId", controlInfo.getAccountId());
            p.addValue("groupEnrollStart", controlInfo.getGroupEnrollStart(), Types.TIMESTAMP);
            p.addValue("groupEnrollStop", controlInfo.getGroupEnrollStop(), Types.TIMESTAMP);
            p.addValue("optOutStart", controlInfo.getOptOutStart(), Types.TIMESTAMP);
            p.addValue("optOutStop", controlInfo.getOptOutStop(), Types.TIMESTAMP);
            p.addValue("type", controlInfo.getType());
            p.addValue("relay", controlInfo.getRelay());
            p.addValue("userIdFirstAction", controlInfo.getUserIdFirstAction());
            p.addValue("userIdSecondAction", controlInfo.getUserIdSecondAction());
        }
        public Number getPrimaryKey(LMHardwareControlGroup controlInfo) {
            return controlInfo.getControlEntryId();
        }
        public void setPrimaryKey(LMHardwareControlGroup controlInfo, int value) {
            controlInfo.setControlEntryId(value);
        }
    };

    public void setNextValueHelper(final NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareConfiguration> getOldConfigDataByInventoryId(int inventoryId) {
        try {
            List<LMHardwareConfiguration> list = simpleJdbcTemplate.query(selectOldInventoryConfigInfo, oldControlInfoRowMapper, inventoryId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareConfiguration> getOldConfigDataByInventoryIdAndGroupId(int inventoryId, int lmGroupId) {
        try {
            List<LMHardwareConfiguration> list = simpleJdbcTemplate.query(selectOldInventoryLoadGroupConfigInfo, oldControlInfoRowMapper, inventoryId, lmGroupId);
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<LMHardwareControlGroup>(simpleJdbcTemplate, nextValueHelper);
        template.withTableName(TABLE_NAME);
        template.withPrimaryKeyField("controlEntryId");
        template.withFieldMapper(controlGroupFieldMapper); 
    }
}
