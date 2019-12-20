package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.OpenInterval;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.appliance.model.AssignedProgramName;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.HardwareConfigAction;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class LMHardwareControlGroupDaoImpl implements LMHardwareControlGroupDao {
    private static final String removeSql;
    private static final YukonRowMapper<LMHardwareControlGroup> rowMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<LMHardwareControlGroup> template;
    
    private static final String TABLE_NAME = "LMHardwareControlGroup";
    private static final String selectOldInventoryLoadGroupConfigInfo; 
    private static final String selectOldInventoryConfigInfo; 
    private static final RowMapper<LMHardwareConfiguration> oldControlInfoRowMapper;
    
    private static final String selectDistinctGroupIdByAccountId;
    private static final RowMapper<Integer> groupIdRowMapper;
    
    private SqlStatementBuilder selectSql = new SqlStatementBuilder();
    {
        selectSql.append("SELECT LMHCG.ControlEntryId, LMHCG.InventoryId, LMHCG.LMGroupId, LMHCG.AccountId,");
        selectSql.append(       "LMHCG.GroupEnrollStart, LMHCG.GroupEnrollStop, LMHCG.OptOutStart, LMHCG.OptOutStop, LMHCG.Type,");
        selectSql.append(       "LMHCG.Relay, LMHCG.UserIdFirstAction, LMHCG.UserIdSecondAction, LMHCG.ProgramId");
        selectSql.append("FROM " + TABLE_NAME + " LMHCG");
    }
    
    static {
        
        removeSql = "DELETE from " + TABLE_NAME + " WHERE ControlEntryId = ?";
    
        selectDistinctGroupIdByAccountId = "SELECT DISTINCT LMGroupId FROM " + TABLE_NAME + " WHERE AccountId = ?";
        
        selectOldInventoryConfigInfo = "SELECT InventoryId, ApplianceId, AddressingGroupId, LoadNumber FROM LMHardwareConfiguration WHERE InventoryId = ?";
        
        selectOldInventoryLoadGroupConfigInfo = "SELECT InventoryId, ApplianceId, AddressingGroupId, LoadNumber FROM LMHardwareConfiguration WHERE InventoryId = ? and AddressingGroupId = ?";
        
        rowMapper = LMHardwareControlGroupDaoImpl.createRowMapper();
        
        oldControlInfoRowMapper = LMHardwareControlGroupDaoImpl.createOldConfigInfoRowMapper();
        
        groupIdRowMapper = LMHardwareControlGroupDaoImpl.createGroupIdRowMapper();
        
    }
    
    public class DistinctEnrollment {
        private int accountId;
        private int inventoryId;
        private int groupId;
        private int programId;
        
        public int getAccountId() {
            return accountId;
        }
        public void setAccountId(int accountId) {
            this.accountId = accountId;
        }
        public int getInventoryId() {
            return inventoryId;
        }
        public void setInventoryId(int inventoryId) {
            this.inventoryId = inventoryId;
        }
        public int getGroupId() {
            return groupId;
        }
        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }
        public int getProgramId() {
            return programId;
        }
        public void setProgramId(int programId) {
            this.programId = programId;
        }
    }
    
    @Override
    public List<DistinctEnrollment> getDistinctEnrollments(int accountId, boolean past) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT InventoryId, LMGroupId, AccountId, ProgramId");
        sql.append("FROM LMHardwareControlGroup");
        sql.append("WHERE AccountId").eq(accountId);
        sql.append("  AND Type").eq(LMHardwareControlGroup.ENROLLMENT_ENTRY);
        
        if(!past) {
            sql.append("AND GroupEnrollStop IS NULL");
        } else {
            sql.append("AND GroupEnrollStop IS NOT NULL");
        }
        
        return yukonJdbcTemplate.query(sql, new RowMapper<DistinctEnrollment> () {
            @Override
            public DistinctEnrollment mapRow(ResultSet rs, int rowNum) throws SQLException {
                DistinctEnrollment enrollment = new DistinctEnrollment();
                enrollment.setAccountId(rs.getInt("AccountId"));
                enrollment.setInventoryId(rs.getInt("InventoryId"));
                enrollment.setGroupId(rs.getInt("LMGroupId"));
                enrollment.setProgramId(rs.getInt("ProgramId"));
                return enrollment;
            }
        });
    }
    
    @Override
    public List<Integer> getPastEnrollmentProgramIds(int accountId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ProgramId");
        sql.append("FROM LMHardwareControlGroup");
        sql.append("WHERE AccountId").eq(accountId);
        sql.append("AND Type").eq(1);
        sql.append("AND GroupEnrollStop IS NOT NULL");
        
        return yukonJdbcTemplate.query(sql, new IntegerRowMapper());
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void add(final LMHardwareControlGroup hardwareControlGroup) {
        template.insert(hardwareControlGroup);
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final LMHardwareControlGroup hardwareControlGroup) {
        int rowsAffected = 
            yukonJdbcTemplate.update(removeSql, hardwareControlGroup.getControlEntryId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void update(final LMHardwareControlGroup hardwareControlGroup){
        template.update(hardwareControlGroup);
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void unenrollHardware(int inventoryId, Instant groupEnrollStop) {
        
        SqlStatementBuilder unenrollHardwareSQL = new SqlStatementBuilder();
        unenrollHardwareSQL.append("UPDATE LMHardwareControlGroup");
        unenrollHardwareSQL.append("SET groupEnrollStop = ?");
        unenrollHardwareSQL.append("WHERE InventoryId = ?");
        unenrollHardwareSQL.append("AND NOT groupEnrollStart IS NULL");
        unenrollHardwareSQL.append("AND groupEnrollStop IS NULL");
        yukonJdbcTemplate.update(unenrollHardwareSQL.toString(), groupEnrollStop.toDate(), inventoryId);
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void resetEntriesForProgram(int programId, LiteYukonUser user) {
     
        //Reset historic Enrollment and OptOut entries
        SqlStatementBuilder histEnrollOptOutSql = new SqlStatementBuilder();
        histEnrollOptOutSql.append("UPDATE LMHardwareControlGroup ");
        histEnrollOptOutSql.append("SET ProgramId =").appendArgument(0);
        histEnrollOptOutSql.append(", UserIDSecondAction =").appendArgument(user.getUserID());
        histEnrollOptOutSql.append("WHERE ProgramId =").appendArgument(programId);
        histEnrollOptOutSql.append("AND (GroupEnrollStop IS NOT NULL OR OptOutStop IS NOT NULL)");
        yukonJdbcTemplate.update(histEnrollOptOutSql.getSql(), histEnrollOptOutSql.getArguments());        

        //Reset current OptOut entries        
        SqlStatementBuilder currentOptOutSql = new SqlStatementBuilder();
        currentOptOutSql.append("UPDATE LMHardwareControlGroup ");
        currentOptOutSql.append("SET ProgramId =").appendArgument(0);
        currentOptOutSql.append(", UserIDSecondAction =").appendArgument(user.getUserID());
        currentOptOutSql.append(", OptOutStop =").appendArgument(new Date());
        currentOptOutSql.append("WHERE ProgramId =").appendArgument(programId);
        currentOptOutSql.append("AND OptOutStart IS NOT NULL AND OptOutStop IS NULL");
        yukonJdbcTemplate.update(currentOptOutSql.getSql(), currentOptOutSql.getArguments());
        
        //Reset current Enrollment entries        
        SqlStatementBuilder currentEnrollSql = new SqlStatementBuilder();
        currentEnrollSql.append("UPDATE LMHardwareControlGroup ");
        currentEnrollSql.append("SET ProgramId =").appendArgument(0);
        currentEnrollSql.append(", UserIDSecondAction =").appendArgument(user.getUserID());
        currentEnrollSql.append(", GroupEnrollStop =").appendArgument(new Date());
        currentEnrollSql.append("WHERE ProgramId =").appendArgument(programId);
        currentEnrollSql.append("AND GroupEnrollStart IS NOT NULL AND GroupEnrollStop IS NULL");
        yukonJdbcTemplate.update(currentEnrollSql.getSql(), currentEnrollSql.getArguments());        
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void stopOptOut(int inventoryId, LiteYukonUser currentUser, ReadableInstant stopDate) {
        stopOptOut(inventoryId, -1, currentUser, stopDate);
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void stopOptOut(int inventoryId, int assignedProgram, LiteYukonUser currentUser, ReadableInstant stopDate) {
        
        SqlStatementBuilder optOutSQL = new SqlStatementBuilder();
        optOutSQL.append("UPDATE LMHardwareControlGroup");
        optOutSQL.append("SET OptOutStop").eq(stopDate);
        optOutSQL.append(",UserIdSecondAction").eq(currentUser.getUserID());
        optOutSQL.append("WHERE InventoryId").eq(inventoryId);
        optOutSQL.append("  AND NOT OptOutStart IS NULL");
        optOutSQL.append("  AND OptOutStop IS NULL");
        
        if (assignedProgram != -1) {
            optOutSQL.append("AND ProgramId").eq(assignedProgram);
        }

        yukonJdbcTemplate.update(optOutSQL);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LMHardwareControlGroup getById(final int controlEntryId) {
        SqlStatementBuilder selectById = new SqlStatementBuilder();
        selectById.appendFragment(selectSql);
        selectById.append("WHERE ControlEntryId").eq(controlEntryId);

        return yukonJdbcTemplate.queryForObject(selectById, rowMapper);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByLMGroupId(final int groupId) {
        SqlStatementBuilder selectByLMGroupId = new SqlStatementBuilder();
        selectByLMGroupId.appendFragment(selectSql);
        selectByLMGroupId.append("WHERE LMGroupId").eq(groupId);

        return yukonJdbcTemplate.query(selectByLMGroupId, rowMapper);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByInventoryId(final int inventoryId) {
        SqlStatementBuilder selectByInventoryId = new SqlStatementBuilder();
        selectByInventoryId.appendFragment(selectSql);
        selectByInventoryId.append("WHERE InventoryId").eq(inventoryId);
        
        return yukonJdbcTemplate.query(selectByInventoryId, rowMapper);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByAccountId(final int accountId) {
        SqlStatementBuilder selectByAccountId = new SqlStatementBuilder();
        selectByAccountId.appendFragment(selectSql);
        selectByAccountId.append("WHERE AccountId").eq(accountId);

        return yukonJdbcTemplate.query(selectByAccountId, rowMapper);
    }
    
    @Override
    public List<LMHardwareControlGroup> getForPastEnrollments(int accountId) {
        SqlStatementBuilder selectByAccountId = new SqlStatementBuilder();
        selectByAccountId.appendFragment(selectSql);
        selectByAccountId.append("WHERE AccountId").eq(accountId);
        selectByAccountId.append("AND GroupEnrollStop IS NOT NULL");
        
        return yukonJdbcTemplate.query(selectByAccountId, rowMapper);
    }

    @Override
    public List<LMHardwareControlGroup> getForActiveEnrollments(int accountId, int inventoryId, int lmGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectSql);
        sql.append("WHERE AccountId").eq(accountId);
        sql.append("AND InventoryId").eq(inventoryId);
        sql.append("AND LmGroupId").eq(lmGroupId);
        sql.append("AND GroupEnrollStart IS NOT NULL");
        sql.append("AND GroupEnrollStop IS NULL");
        
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<LMHardwareControlGroup> getForPastEnrollments(int accountId, int inventoryId, int lmGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectSql);
        sql.append("WHERE AccountId").eq(accountId);
        sql.append("AND InventoryId").eq(inventoryId);
        sql.append("AND LmGroupId").eq(lmGroupId);
        sql.append("AND GroupEnrollStart IS NOT NULL");
        sql.append("AND GroupEnrollStop IS NOT NULL");
        
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getDistinctGroupIdsByAccountId(final int accountId) {
        List<Integer> list = 
            yukonJdbcTemplate.query(selectDistinctGroupIdByAccountId, groupIdRowMapper, accountId);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByEnrollmentStartDateRange(Date first, Date second) {
        SqlStatementBuilder selectByEnrollStartRange = new SqlStatementBuilder();
        selectByEnrollStartRange.appendFragment(selectSql);
        selectByEnrollStartRange.append("WHERE GroupEnrollStart > ").appendArgument(first);
        selectByEnrollStartRange.append("AND GroupEnrollStart <= ").appendArgument(second);

        return yukonJdbcTemplate.query(selectByEnrollStartRange, rowMapper);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByEnrollmentStopDateRange(Date first, Date second) {
        SqlStatementBuilder selectByEnrollStopRange = new SqlStatementBuilder();
        selectByEnrollStopRange.appendFragment(selectSql);
        selectByEnrollStopRange.append("WHERE GroupEnrollStop > ").appendArgument(first);
        selectByEnrollStopRange.append("AND GroupEnrollStop <= ").appendArgument(second);

        return yukonJdbcTemplate.query(selectByEnrollStopRange, rowMapper);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByOptOutStartDateRange(Date first, Date second) {
        SqlStatementBuilder selectByOptOutStartRange = new SqlStatementBuilder();
        selectByOptOutStartRange.appendFragment(selectSql);
        selectByOptOutStartRange.append("WHERE OptOutStart > ").appendArgument(first);
        selectByOptOutStartRange.append("AND OptOutStart <= ").appendArgument(second);
        
        return yukonJdbcTemplate.query(selectByOptOutStartRange, rowMapper);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByOptOutStopDateRange(Date first, Date second) {
        SqlStatementBuilder selectByOptOutStopRange = new SqlStatementBuilder();
        selectByOptOutStopRange.appendFragment(selectSql);
        selectByOptOutStopRange.append("WHERE OptOutStop > ").appendArgument(first);
        selectByOptOutStopRange.append("AND OptOutStop <= ").appendArgument(second);
        
        return yukonJdbcTemplate.query(selectByOptOutStopRange, rowMapper);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByLMGroupIdAndAccountIdAndType(int lmGroupId, 
                                                                           int accountId, 
                                                                           int type) {
        SqlStatementBuilder selectByLMGroupIdAndAccountIdAndType = new SqlStatementBuilder();
        selectByLMGroupIdAndAccountIdAndType.appendFragment(selectSql);
        selectByLMGroupIdAndAccountIdAndType.append("WHERE LMGroupId").eq(lmGroupId);
        selectByLMGroupIdAndAccountIdAndType.append("AND AccountId").eq(accountId);
        selectByLMGroupIdAndAccountIdAndType.append("AND Type").eq(type);
        selectByLMGroupIdAndAccountIdAndType.append("ORDER BY GroupEnrollStart");

        return yukonJdbcTemplate.query(selectByLMGroupIdAndAccountIdAndType, rowMapper);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByInventoryIdAndAccountIdAndType(int inventoryId, 
                                                                             int accountId, 
                                                                             int type) {
        SqlStatementBuilder selectByInventoryIdAndAccountIdAndType = new SqlStatementBuilder();
        selectByInventoryIdAndAccountIdAndType.appendFragment(selectSql);
        selectByInventoryIdAndAccountIdAndType.append("WHERE InventoryId").eq(inventoryId);
        selectByInventoryIdAndAccountIdAndType.append("AND AccountId").eq(accountId);
        selectByInventoryIdAndAccountIdAndType.append("AND Type").eq(type);

        return yukonJdbcTemplate.query(selectByInventoryIdAndAccountIdAndType, rowMapper);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountId(int inventoryId, 
                                                                                int lmGroupId, 
                                                                                int accountId) {

        SqlStatementBuilder selectByInventoryIdAndGroupIdAndAccountId = new SqlStatementBuilder();
        selectByInventoryIdAndGroupIdAndAccountId.appendFragment(selectSql);
        selectByInventoryIdAndGroupIdAndAccountId.append("WHERE InventoryId").eq(inventoryId);
        selectByInventoryIdAndGroupIdAndAccountId.append("AND LMGroupId").eq(lmGroupId);
        selectByInventoryIdAndGroupIdAndAccountId.append("AND AccountId").eq(accountId);
        
        return yukonJdbcTemplate.query(selectByInventoryIdAndGroupIdAndAccountId, rowMapper);
    }
    
    @Override
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountIdAndType(int inventoryId, int lmGroupId, int accountId, int type) {

        SqlStatementBuilder selectByInventoryIdAndGroupIdAndAccountIdAndType = new SqlStatementBuilder();
        selectByInventoryIdAndGroupIdAndAccountIdAndType.appendFragment(selectSql);
        selectByInventoryIdAndGroupIdAndAccountIdAndType.append("WHERE InventoryId").eq(inventoryId);
        selectByInventoryIdAndGroupIdAndAccountIdAndType.append("AND LMGroupId").eq(lmGroupId);
        selectByInventoryIdAndGroupIdAndAccountIdAndType.append("AND AccountId").eq(accountId);
        selectByInventoryIdAndGroupIdAndAccountIdAndType.append("AND Type").eq(type);
        
        return yukonJdbcTemplate.query(selectByInventoryIdAndGroupIdAndAccountIdAndType, rowMapper);
    }
    
    @Override
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndType(int inventoryId, int lmGroupId, int type) {

        SqlStatementBuilder selectByInventoryIdAndGroupIdAndAccountIdAndType = new SqlStatementBuilder();
        selectByInventoryIdAndGroupIdAndAccountIdAndType.appendFragment(selectSql);
        selectByInventoryIdAndGroupIdAndAccountIdAndType.append("WHERE InventoryId").eq(inventoryId);
        selectByInventoryIdAndGroupIdAndAccountIdAndType.append("AND LMGroupId").eq(lmGroupId);
        selectByInventoryIdAndGroupIdAndAccountIdAndType.append("AND Type").eq(type);
        
        return yukonJdbcTemplate.query(selectByInventoryIdAndGroupIdAndAccountIdAndType, rowMapper);
    }

    @Override
    public List<LMHardwareControlGroup> getCurrentEnrollmentByAccountId(int accountId) {
        SqlStatementBuilder selectCurrentEnrollmentByAccountId = new SqlStatementBuilder();
        selectCurrentEnrollmentByAccountId.appendFragment(selectSql);
        selectCurrentEnrollmentByAccountId.append("WHERE AccountId").eq(accountId);
        selectCurrentEnrollmentByAccountId.append("AND GroupEnrollStop IS NULL");
        selectCurrentEnrollmentByAccountId.append("AND NOT GroupEnrollStart IS NULL");
        
        return yukonJdbcTemplate.query(selectCurrentEnrollmentByAccountId, rowMapper);
    }    

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> 
                getCurrentEnrollmentByInventoryIdAndAccountId(int inventoryId, int accountId) {
        SqlStatementBuilder selectCurrentEnrollmentByInventoryIdAndAccountId = new SqlStatementBuilder();
        selectCurrentEnrollmentByInventoryIdAndAccountId.appendFragment(selectSql);
        selectCurrentEnrollmentByInventoryIdAndAccountId.append("WHERE InventoryId").eq(inventoryId);
        selectCurrentEnrollmentByInventoryIdAndAccountId.append("AND AccountId").eq(accountId);
        selectCurrentEnrollmentByInventoryIdAndAccountId.append("AND GroupEnrollStop IS NULL");
        selectCurrentEnrollmentByInventoryIdAndAccountId.append("AND NOT GroupEnrollStart IS NULL");
        
        return yukonJdbcTemplate.query(selectCurrentEnrollmentByInventoryIdAndAccountId, rowMapper);
    }

    @Override
    public Multimap<Integer, LMHardwareControlGroup> getCurrentEnrollmentByInventoryIds(
            final Iterable<Integer> inventoryIds) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.appendFragment(selectSql);
                sql.append("WHERE InventoryId").in(inventoryIds);
                sql.append(    "AND GroupEnrollStop IS NULL");
                sql.append(    "AND NOT GroupEnrollStart IS NULL");
                return sql;
            }
        };
        Function<Integer, Integer> typeMapper = Functions.identity();
        YukonRowMapper<Map.Entry<Integer, LMHardwareControlGroup>> mappingRowMapper =
            new YukonRowMapper<Map.Entry<Integer, LMHardwareControlGroup>>() {
                @Override
                public Entry<Integer, LMHardwareControlGroup> mapRow(
                        YukonResultSet rs) throws SQLException {
                    LMHardwareControlGroup hwControlGroup = rowMapper.mapRow(rs);
                    return Maps.immutableEntry(hwControlGroup.getInventoryId(), hwControlGroup);
                }};

        Multimap<Integer, LMHardwareControlGroup> retVal =
            template.multimappedQuery(sqlGenerator, inventoryIds, mappingRowMapper,
                                      typeMapper);

        return retVal;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> 
                getCurrentEnrollmentByProgramIdAndAccountId(int programId, int accountId) {
        SqlStatementBuilder selectCurrentEnrollmentByProgramIdAndAccountId = new SqlStatementBuilder();
        selectCurrentEnrollmentByProgramIdAndAccountId.appendFragment(selectSql);
        selectCurrentEnrollmentByProgramIdAndAccountId.append("WHERE ProgramId").eq(programId);
        selectCurrentEnrollmentByProgramIdAndAccountId.append("AND AccountId").eq(accountId);
        selectCurrentEnrollmentByProgramIdAndAccountId.append("AND GroupEnrollStop IS NULL");
        selectCurrentEnrollmentByProgramIdAndAccountId.append("AND NOT GroupEnrollStart IS NULL");
        
        return yukonJdbcTemplate.query(selectCurrentEnrollmentByProgramIdAndAccountId, rowMapper);
    }      

    @Override
    public LMHardwareControlGroup 
                findCurrentEnrollmentByInventoryIdAndProgramIdAndAccountId(int inventoryId, int programId, int accountId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectSql);
        sql.append("WHERE InventoryId").eq(inventoryId);
        sql.append("AND ProgramId").eq(programId);
        sql.append("AND AccountId").eq(accountId);
        sql.append("AND GroupEnrollStop IS NULL");
        sql.append("AND NOT GroupEnrollStart IS NULL");

        try {
            return yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }    
    
    @Override
    public List<LMHardwareControlGroup> getCurrentOptOutByProgramIdAndAccountId(int programId, 
                                                                                 int accountId) {
        SqlStatementBuilder selectCurrentOptOutsByProgramIdAndAccountId = new SqlStatementBuilder();
        selectCurrentOptOutsByProgramIdAndAccountId.appendFragment(selectSql);
        selectCurrentOptOutsByProgramIdAndAccountId.append("WHERE ProgramId").eq(programId);
        selectCurrentOptOutsByProgramIdAndAccountId.append("AND AccountId").eq(accountId);
        selectCurrentOptOutsByProgramIdAndAccountId.append("AND OptOutStop IS NULL");
        selectCurrentOptOutsByProgramIdAndAccountId.append("AND NOT OptOutStart IS NULL");

        return yukonJdbcTemplate.query(selectCurrentOptOutsByProgramIdAndAccountId, rowMapper);
    }
    
    @Override
    public List<LMHardwareControlGroup> 
                getCurrentOptOutByInventoryIdProgramIdAndAccountId(int inventoryId, int programId, 
                                                                   int accountId) {
        SqlStatementBuilder selectCurrentOptOutsByInventoryIdProgramIdAndAccountId = new SqlStatementBuilder();
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId.appendFragment(selectSql);;
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId.append("WHERE InventoryId").eq(inventoryId);
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId.append("AND ProgramId").eq(programId);
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId.append("AND AccountId").eq(accountId);
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId.append("AND OptOutStop IS NULL");
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId.append("AND NOT OptOutStart IS NULL");

        return yukonJdbcTemplate.query(selectCurrentOptOutsByInventoryIdProgramIdAndAccountId, rowMapper);
    }    
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getAll() {
        return yukonJdbcTemplate.query(selectSql, rowMapper);
    }

    @Override
    public List<HardwareConfigAction> getHardwareConfigActions(int accountId) {
        List<HardwareConfigAction> retVal = Lists.newArrayList();

        retVal.addAll(getHardwareConfigActions(accountId, EnrollmentEnum.ENROLL));
        retVal.addAll(getHardwareConfigActions(accountId, EnrollmentEnum.UNENROLL));

        Collections.sort(retVal, new Comparator<HardwareConfigAction>() {
            @Override
            public int compare(HardwareConfigAction hca1, HardwareConfigAction hca2) {
                return -hca1.getDate().compareTo(hca2.getDate());
            }});

        return retVal;
    }

    private List<HardwareConfigAction> getHardwareConfigActions(int accountId,
            final EnrollmentEnum actionType) {
        final String dateColumn =
            actionType == EnrollmentEnum.ENROLL ? "groupEnrollStart" : "groupEnrollStop";
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT hb.manufacturerSerialNumber, hcg.relay, pao.paoName,");
        sql.append(    "wc.alternateDisplayName,");
        sql.append(    "hcg.lmGroupId, lgPao.paoName as loadGroupName,");
        sql.append(    "hcg.").append(dateColumn);
        sql.append("FROM lmHardwareControlGroup hcg");
        sql.append(    "LEFT JOIN lmHardwareBase hb on hb.inventoryId = hcg.inventoryId");
        sql.append(    "JOIN lmProgramWebPublishing wp on hcg.programId = wp.programId");
        sql.append(    "JOIN yukonPAObject pao on wp.deviceId = pao.paobjectId");
        sql.append(    "JOIN yukonPAObject lgPao on lgPao.paobjectId = hcg.lmGroupId");
        sql.append(    "JOIN yukonWebConfiguration wc on wc.ConfigurationId = wp.websettingsId");
        sql.append("WHERE hcg.accountId").eq(accountId);
        sql.append(    "AND hcg.type").eq(LMHardwareControlGroup.ENROLLMENT_ENTRY);
        sql.append(    "AND hcg.").append(dateColumn).append("IS NOT NULL");
        RowMapper<HardwareConfigAction> rowMapper =
            new RowMapper<HardwareConfigAction>() {
                @Override
                public HardwareConfigAction mapRow(ResultSet rs, int index)
                        throws SQLException {
                    Date date = rs.getTimestamp(dateColumn);

                    AssignedProgramName name =
                        new AssignedProgramName(rs.getString("paoName"),
                                                 rs.getString("alternateDisplayName"));

                    String loadGroupName = "";
                    if (rs.getInt("lmGroupId") != 0) {
                        loadGroupName = rs.getString("loadGroupName");
                    }

                    String hardwareSerialNumber = rs.getString("manufacturerSerialNumber");
                    if (rs.wasNull()) {
                        hardwareSerialNumber = null;
                    }
                    int relay = rs.getInt("relay");

                    HardwareConfigAction retVal =
                        new HardwareConfigAction(date, actionType,
                                                 name.getDisplayName(),
                                                 loadGroupName,
                                                 hardwareSerialNumber, relay);
                    return retVal;
                }
        };
        List<HardwareConfigAction> retVal =
            yukonJdbcTemplate.query(sql.getSql(), rowMapper, sql.getArguments());
        return retVal;
    }

    private static final YukonRowMapper<LMHardwareControlGroup> createRowMapper() {
        final YukonRowMapper<LMHardwareControlGroup> rowMapper = new YukonRowMapper<LMHardwareControlGroup>() {
            @Override
            public LMHardwareControlGroup mapRow(YukonResultSet rs) throws SQLException {
                LMHardwareControlGroup hardwareControlGroup = new LMHardwareControlGroup();
                hardwareControlGroup.setControlEntryId(rs.getInt("ControlEntryId"));
                hardwareControlGroup.setInventoryId(rs.getInt("InventoryId"));
                hardwareControlGroup.setLmGroupId(rs.getInt("LMGroupId"));
                hardwareControlGroup.setAccountId(rs.getInt("AccountId"));
                hardwareControlGroup.setGroupEnrollStart(rs.getInstant("GroupEnrollStart"));
                hardwareControlGroup.setGroupEnrollStop(rs.getInstant("GroupEnrollStop"));
                hardwareControlGroup.setOptOutStart(rs.getInstant("OptOutStart"));
                hardwareControlGroup.setOptOutStop(rs.getInstant("OptOutStop"));
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
    
    private static final RowMapper<LMHardwareConfiguration> createOldConfigInfoRowMapper() {
        final RowMapper<LMHardwareConfiguration> oldConfigInfoRowMapper = new RowMapper<LMHardwareConfiguration>() {
            @Override
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
    
    private static final RowMapper<Integer> createGroupIdRowMapper() {
        RowMapper<Integer> rowMapper = new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                int id = rs.getInt("LmGroupId");
                return Integer.valueOf(id);
            }
        };
        return rowMapper;
    }
    
    private FieldMapper<LMHardwareControlGroup> controlGroupFieldMapper = new FieldMapper<LMHardwareControlGroup>() {
        @Override
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
        @Override
        public Number getPrimaryKey(LMHardwareControlGroup controlInfo) {
            return controlInfo.getControlEntryId();
        }
        @Override
        public void setPrimaryKey(LMHardwareControlGroup controlInfo, int value) {
            controlInfo.setControlEntryId(value);
        }
    };

    public void setNextValueHelper(final NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareConfiguration> getOldConfigDataByInventoryId(int inventoryId) {
        List<LMHardwareConfiguration> list = yukonJdbcTemplate.query(selectOldInventoryConfigInfo, oldControlInfoRowMapper, inventoryId);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareConfiguration> getOldConfigDataByInventoryIdAndGroupId(int inventoryId, int lmGroupId) {
        List<LMHardwareConfiguration> list = yukonJdbcTemplate.query(selectOldInventoryLoadGroupConfigInfo, oldControlInfoRowMapper, inventoryId, lmGroupId);
        return list;
    }

    @PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<LMHardwareControlGroup>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName(TABLE_NAME);
        template.setPrimaryKeyField("controlEntryId");
        template.setFieldMapper(controlGroupFieldMapper); 
    }

    @Override
    public List<LMHardwareControlGroup> getIntersectingEnrollments(Collection<Integer> energyCompanyIds, List<Integer> loadGroupIds, OpenInterval enrollmentInterval) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectSql);
        sql.append("  JOIN ECToAccountMapping ECTAM ON ECTAM.AccountId = LMHCG.AccountId");
        sql.append("WHERE LMHCG.LmGroupId").in(loadGroupIds);
        sql.append("  AND ECTAM.EnergyCompanyId").in(energyCompanyIds);
        if (!enrollmentInterval.isOpenEnd()) {
            sql.append("  AND LMHCG.GroupEnrollStart").lt(enrollmentInterval.getEnd());
        }
        sql.append("  AND (LMHCG.GroupEnrollStop IS NULL");
        if (!enrollmentInterval.isOpenStart()) {
            sql.append("       OR LMHCG.GroupEnrollStop").gte(enrollmentInterval.getStart());
        }
        sql.append(")");
        sql.append("  AND LMHCG.Type").eq_k(LMHardwareControlGroup.ENROLLMENT_ENTRY);
        
        List<LMHardwareControlGroup> enrollments = yukonJdbcTemplate.query(sql, createRowMapper());
        return enrollments;
    }
    
    @Override
    public List<LMHardwareControlGroup> getIntersectingEnrollments(int accountId,
                                                                   int inventoryId,
                                                                   int loadGroupId,
                                                                   OpenInterval enrollmentInterval) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectSql);
        sql.append("WHERE AccountId").eq(accountId);
        sql.append("  AND InventoryId").eq(inventoryId);
        sql.append("  AND LmGroupId").eq(loadGroupId);
        if (!enrollmentInterval.isOpenEnd()) {
            sql.append("  AND GroupEnrollStart").lt(enrollmentInterval.getEnd());
        }
        sql.append("  AND (GroupEnrollStop IS NULL");
        if (!enrollmentInterval.isOpenStart()) {
            sql.append("       OR GroupEnrollStop").gte(enrollmentInterval.getStart());
        }
        sql.append(")");
        sql.append("  AND Type").eq(LMHardwareControlGroup.ENROLLMENT_ENTRY);
        
        List<LMHardwareControlGroup> enrollments = yukonJdbcTemplate.query(sql, createRowMapper());
        return enrollments;
    }

    @Override
    public List<LMHardwareControlGroup> getIntersectingOptOuts(int accountId,
                                                               int inventoryId,
                                                               int loadGroupId,
                                                               OpenInterval optOutInterval) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectSql);
        sql.append("WHERE AccountId").eq(accountId);
        sql.append("  AND InventoryId").eq(inventoryId);
        sql.append("  AND LmGroupId").eq(loadGroupId);
        if (!optOutInterval.isOpenEnd()) {
            sql.append("  AND OptOutStart").lt(optOutInterval.getEnd());
        }
        sql.append("  AND (OptOutStop IS NULL");
        if (!optOutInterval.isOpenStart()) {
            sql.append("       OR OptOutStop").gte(optOutInterval.getStart()).append(")");
        }
        sql.append("  AND Type").eq(LMHardwareControlGroup.OPT_OUT_ENTRY);
        
        List<LMHardwareControlGroup> enrollments = yukonJdbcTemplate.query(sql, createRowMapper());
        return enrollments;
    }
    
    @Override
    public LMHardwareControlGroup findCurrentEnrollmentByInventoryIdAndRelayAndAccountId(int inventoryId,
            int accountId, int relay) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectSql);
        sql.append("WHERE InventoryId").eq(inventoryId);
        sql.append("AND Relay").eq(relay);
        sql.append("AND AccountId").eq(accountId);
        sql.append("AND GroupEnrollStop IS NULL");
        sql.append("AND NOT GroupEnrollStart IS NULL");

        try {
            return yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
