package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;
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
    private static final String selectCurrentEnrollmentByAccountId;
    private static final String selectCurrentEnrollmentByInventoryIdAndAccountId;
    private static final String selectCurrentEnrollmentByProgramIdAndAccountId;
    private static final String selectCurrentOptOutsByProgramIdAndAccountId;
    private static final String selectCurrentOptOutsByInventoryIdProgramIdAndAccountId;
    private static final String selectByInventoryIdAndAccountIdAndType;
    private static final String selectByInventoryIdAndGroupIdAndAccountId;
    private static final String selectByInventoryIdAndGroupIdAndAccountIdAndType;
    private static final String selectCurrentEnrollmentByInventoryIdProgramIdAndAccountId;
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
                "OptOutStop, Type, Relay, UserIdFirstAction, UserIdSecondAction, ProgramId FROM " + TABLE_NAME;
    
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
        
        selectCurrentEnrollmentByAccountId = selectAllSql + " WHERE AccountId = ? AND GroupEnrollStop IS NULL AND NOT GroupEnrollStart IS NULL";
        
        selectCurrentEnrollmentByInventoryIdAndAccountId = selectAllSql + " WHERE InventoryId = ? AND AccountId = ? AND GroupEnrollStop IS NULL AND NOT GroupEnrollStart IS NULL";
        
        selectCurrentEnrollmentByProgramIdAndAccountId = selectAllSql + " WHERE ProgramId = ? AND AccountId = ? AND GroupEnrollStop IS NULL AND NOT GroupEnrollStart IS NULL";        
        
        selectCurrentEnrollmentByInventoryIdProgramIdAndAccountId = selectAllSql + " WHERE InventoryId = ? AND ProgramId = ? AND AccountId = ? AND GroupEnrollStop IS NULL AND NOT GroupEnrollStart IS NULL";
        
        selectCurrentOptOutsByProgramIdAndAccountId = selectAllSql + " WHERE ProgramId = ? AND AccountId = ? AND OptOutStop IS NULL AND NOT OptOutStart IS NULL";
        
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId = selectAllSql + " WHERE InventoryId = ? AND ProgramId = ? AND AccountId = ? AND OptOutStop IS NULL AND NOT OptOutStart IS NULL";
        
        selectByInventoryIdAndAccountIdAndType = selectAllSql + " WHERE InventoryId = ? AND AccountId = ? AND Type = ?";
        
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
    public void add(final LMHardwareControlGroup hardwareControlGroup) {
        template.insert(hardwareControlGroup);
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final LMHardwareControlGroup hardwareControlGroup) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql, hardwareControlGroup.getControlEntryId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void update(final LMHardwareControlGroup hardwareControlGroup){
        template.update(hardwareControlGroup);
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void unenrollHardware(int inventoryId) {
        
        SqlStatementBuilder unenrollHardwareSQL = new SqlStatementBuilder();
        unenrollHardwareSQL.append("UPDATE LMHardwareControlGroup");
        unenrollHardwareSQL.append("SET groupEnrollStop = ?");
        unenrollHardwareSQL.append("WHERE InventoryId = ?");
        unenrollHardwareSQL.append("AND NOT groupEnrollStart IS NULL");
        unenrollHardwareSQL.append("AND groupEnrollStop IS NULL");
        Date now = new Date();
        simpleJdbcTemplate.update(unenrollHardwareSQL.toString(), now, inventoryId);
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void resetEntriesForProgram(int programId, LiteYukonUser user) {
     
        //Reset historic Enrollment and OptOut entries
        SqlStatementBuilder histEnrollOptOutSql = new SqlStatementBuilder();
        histEnrollOptOutSql.append("UPDATE LMHardwareControlGroup ");
        histEnrollOptOutSql.append("SET ProgramId =").appendArgument(0);
        histEnrollOptOutSql.append(", UserIDSecondAction =").appendArgument(user.getUserID());
        histEnrollOptOutSql.append("WHERE ProgramId =").appendArgument(programId);
        histEnrollOptOutSql.append("AND (GroupEnrollStop IS NOT NULL OR OptOutStop IS NOT NULL)");
        simpleJdbcTemplate.update(histEnrollOptOutSql.toString(), histEnrollOptOutSql.getArguments());        

        //Reset current OptOut entries        
        SqlStatementBuilder currentOptOutSql = new SqlStatementBuilder();
        currentOptOutSql.append("UPDATE LMHardwareControlGroup ");
        currentOptOutSql.append("SET ProgramId =").appendArgument(0);
        currentOptOutSql.append(", UserIDSecondAction =").appendArgument(user.getUserID());
        currentOptOutSql.append(", OptOutStop =").appendArgument(new Date());
        currentOptOutSql.append("WHERE ProgramId =").appendArgument(programId);
        currentOptOutSql.append("AND OptOutStart IS NOT NULL AND OptOutStop IS NULL");
        simpleJdbcTemplate.update(currentOptOutSql.toString(), currentOptOutSql.getArguments());
        
        //Reset current Enrollment entries        
        SqlStatementBuilder currentEnrollSql = new SqlStatementBuilder();
        currentEnrollSql.append("UPDATE LMHardwareControlGroup ");
        currentEnrollSql.append("SET ProgramId =").appendArgument(0);
        currentEnrollSql.append(", UserIDSecondAction =").appendArgument(user.getUserID());
        currentEnrollSql.append(", GroupEnrollStop =").appendArgument(new Date());
        currentEnrollSql.append("WHERE ProgramId =").appendArgument(programId);
        currentEnrollSql.append("AND GroupEnrollStart IS NOT NULL AND GroupEnrollStop IS NULL");
        simpleJdbcTemplate.update(currentEnrollSql.toString(), currentEnrollSql.getArguments());        
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void stopOptOut(int inventoryId, int accountId, LiteYukonUser currentUser, Date stopDate) {
        
        SqlStatementBuilder optOutSQL = new SqlStatementBuilder();
        optOutSQL.append("UPDATE LMHardwareControlGroup");
        optOutSQL.append("SET OptOutStop = ?, UserIdSecondAction = ?");
        optOutSQL.append("WHERE InventoryId = ? AND AccountId = ?");
        optOutSQL.append("AND NOT OptOutStart IS NULL");
        optOutSQL.append("AND OptOutStop IS NULL");
        simpleJdbcTemplate.update(optOutSQL.toString(), stopDate, currentUser.getUserID(), inventoryId, accountId);
    }    
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LMHardwareControlGroup getById(final int controlEntryId) {
        LMHardwareControlGroup hardwareControlGroup = simpleJdbcTemplate.queryForObject(selectById, rowMapper, controlEntryId);
        return hardwareControlGroup;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByLMGroupId(final int groupId) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByLMGroupId, rowMapper, groupId);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByInventoryId(final int inventoryId) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByInventoryId, rowMapper, inventoryId);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByAccountId(final int accountId) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByAccountId, rowMapper, accountId);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getDistinctGroupIdsByAccountId(final int accountId) {
        List<Integer> list = simpleJdbcTemplate.query(selectDistinctGroupIdByAccountId, groupIdRowMapper, accountId);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByEnrollmentStartDateRange(Date first, Date second) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByEnrollStartRange, rowMapper, first, second);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByEnrollmentStopDateRange(Date first, Date second) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByEnrollStopRange, rowMapper, first, second);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByOptOutStartDateRange(Date first, Date second) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByOptOutStartRange, rowMapper, first, second);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByOptOutStopDateRange(Date first, Date second) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByOptOutStopRange, rowMapper, first, second);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByLMGroupIdAndAccountIdAndType(int lmGroupId, int accountId, int type) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByLMGroupIdAndAccountIdAndType, rowMapper, lmGroupId, accountId, type);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByInventoryIdAndAccountIdAndType(int inventoryId, int accountId, int type) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByInventoryIdAndAccountIdAndType, rowMapper, inventoryId, accountId, type);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountId(int inventoryId, int lmGroupId, int accountId) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByInventoryIdAndGroupIdAndAccountId, rowMapper, inventoryId, lmGroupId, accountId);
        return list;
    }
    
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountIdAndType(int inventoryId, int lmGroupId, int accountId, int type) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectByInventoryIdAndGroupIdAndAccountIdAndType, rowMapper, inventoryId, lmGroupId, accountId, type);
        return list;
    }

    public List<LMHardwareControlGroup> getCurrentEnrollmentByAccountId(int accountId) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectCurrentEnrollmentByAccountId, rowMapper, accountId);
        return list;
    }    
    
    public List<LMHardwareControlGroup> getCurrentEnrollmentByInventoryIdAndAccountId(int inventoryId, int accountId) {
            List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectCurrentEnrollmentByInventoryIdAndAccountId, rowMapper, inventoryId, accountId);
            return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getCurrentEnrollmentByProgramIdAndAccountId(int programId, int accountId) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectCurrentEnrollmentByProgramIdAndAccountId, rowMapper, programId, accountId);
        return list;
    }      

    public List<LMHardwareControlGroup> getCurrentEnrollmentByInventoryIdAndProgramIdAndAccountId(int inventoryId, int programId, int accountId) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectCurrentEnrollmentByInventoryIdProgramIdAndAccountId, rowMapper, inventoryId, programId, accountId);
        return list;
    }    
    
    public List<LMHardwareControlGroup> getCurrentOptOutByProgramIdAndAccountId(int programId, int accountId) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectCurrentOptOutsByProgramIdAndAccountId, rowMapper, programId, accountId);
        return list;
    }
    
    public List<LMHardwareControlGroup> getCurrentOptOutByInventoryIdProgramIdAndAccountId(int inventoryId, int programId, int accountId) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectCurrentOptOutsByInventoryIdProgramIdAndAccountId, rowMapper, inventoryId, programId, accountId);
        return list;
    }    
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getAll() {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectAllSql, rowMapper, new Object[]{});
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getAllByEnergyCompanyId(int energyCompanyId) {
        List<LMHardwareControlGroup> list = simpleJdbcTemplate.query(selectAllByEnergyCompanyId, rowMapper, energyCompanyId);
        return list;
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
                hardwareControlGroup.setProgramId(rs.getInt("ProgramId"));
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
            p.addValue("programId", controlInfo.getProgramId());
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
        List<LMHardwareConfiguration> list = simpleJdbcTemplate.query(selectOldInventoryConfigInfo, oldControlInfoRowMapper, inventoryId);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareConfiguration> getOldConfigDataByInventoryIdAndGroupId(int inventoryId, int lmGroupId) {
        List<LMHardwareConfiguration> list = simpleJdbcTemplate.query(selectOldInventoryLoadGroupConfigInfo, oldControlInfoRowMapper, inventoryId, lmGroupId);
        return list;
    }

    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<LMHardwareControlGroup>(simpleJdbcTemplate, nextValueHelper);
        template.withTableName(TABLE_NAME);
        template.withPrimaryKeyField("controlEntryId");
        template.withFieldMapper(controlGroupFieldMapper); 
    }
}
