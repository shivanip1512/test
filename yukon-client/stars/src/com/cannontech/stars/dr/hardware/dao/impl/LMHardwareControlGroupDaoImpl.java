package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import com.google.common.collect.Lists;

public class LMHardwareControlGroupDaoImpl implements LMHardwareControlGroupDao, InitializingBean {
    private static final String removeSql;
    private static final String selectAllSql;
    private static final YukonRowMapper<LMHardwareControlGroup> rowMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
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
    
    public List<DistinctEnrollment> getDistinctEnrollments(int accountId, boolean past) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT InventoryId, LMGroupId, AccountId, ProgramId");
        sql.append("FROM LMHardwareControlGroup");
        sql.append("WHERE AccountID").eq(accountId);
        sql.append("  AND Type").eq(1);
        
        if(!past) {
            sql.append("AND GroupEnrollStop IS NULL");
        } else {
            sql.append("AND GroupEnrollStop IS NOT NULL");
        }
        
        return yukonJdbcTemplate.query(sql, new ParameterizedRowMapper<DistinctEnrollment> () {
            @Override
            public DistinctEnrollment mapRow(ResultSet rs, int rowNum) throws SQLException {
                DistinctEnrollment enrollment = new DistinctEnrollment();
                enrollment.setAccountId(rs.getInt("accountId"));
                enrollment.setInventoryId(rs.getInt("inventoryId"));
                enrollment.setGroupId(rs.getInt("LMGroupId"));
                enrollment.setProgramId(rs.getInt("programId"));
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
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void add(final LMHardwareControlGroup hardwareControlGroup) {
        template.insert(hardwareControlGroup);
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final LMHardwareControlGroup hardwareControlGroup) {
        int rowsAffected = 
            yukonJdbcTemplate.update(removeSql, hardwareControlGroup.getControlEntryId());
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
        yukonJdbcTemplate.update(unenrollHardwareSQL.toString(), now, inventoryId);
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
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void stopOptOut(int inventoryId, int accountId, LiteYukonUser currentUser, 
                             ReadableInstant stopDate) {
        
        SqlStatementBuilder optOutSQL = new SqlStatementBuilder();
        optOutSQL.append("UPDATE LMHardwareControlGroup");
        optOutSQL.append("SET OptOutStop").eq(stopDate);
        optOutSQL.append(",UserIdSecondAction").eq(currentUser.getUserID());
        optOutSQL.append("WHERE InventoryId").eq(inventoryId);
        optOutSQL.append("AND AccountId").eq(accountId);
        optOutSQL.append("AND NOT OptOutStart IS NULL");
        optOutSQL.append("AND OptOutStop IS NULL");

        yukonJdbcTemplate.update(optOutSQL);
    }    
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LMHardwareControlGroup getById(final int controlEntryId) {
        SqlStatementBuilder selectById = new SqlStatementBuilder(selectAllSql);
        selectById.append("WHERE ControlEntryId").eq(controlEntryId);

        return yukonJdbcTemplate.queryForObject(selectById, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByLMGroupId(final int groupId) {
        SqlStatementBuilder selectByLMGroupId = new SqlStatementBuilder(selectAllSql);
        selectByLMGroupId.append("WHERE LMGroupId").eq(groupId);

        return yukonJdbcTemplate.query(selectByLMGroupId, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByInventoryId(final int inventoryId) {
        SqlStatementBuilder selectByInventoryId = new SqlStatementBuilder(selectAllSql);
        selectByInventoryId.append("WHERE InventoryId").eq(inventoryId);
        
        return yukonJdbcTemplate.query(selectByInventoryId, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByAccountId(final int accountId) {
        SqlStatementBuilder selectByAccountId = new SqlStatementBuilder(selectAllSql);
        selectByAccountId.append("WHERE AccountId").eq(accountId);

        return yukonJdbcTemplate.query(selectByAccountId, rowMapper);
    }
    
    public List<LMHardwareControlGroup> getForPastEnrollments(int accountId) {
        SqlStatementBuilder selectByAccountId = new SqlStatementBuilder(selectAllSql);
        selectByAccountId.append("WHERE AccountId").eq(accountId);
        selectByAccountId.append("AND GroupEnrollStop IS NOT NULL");
        
        return yukonJdbcTemplate.query(selectByAccountId, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getDistinctGroupIdsByAccountId(final int accountId) {
        List<Integer> list = 
            yukonJdbcTemplate.query(selectDistinctGroupIdByAccountId, groupIdRowMapper, accountId);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByEnrollmentStartDateRange(Date first, Date second) {
        SqlStatementBuilder selectByEnrollStartRange = new SqlStatementBuilder(selectAllSql);
        selectByEnrollStartRange.append("WHERE GroupEnrollStart > ").appendArgument(first);
        selectByEnrollStartRange.append("AND GroupEnrollStart <= ").appendArgument(second);

        return yukonJdbcTemplate.query(selectByEnrollStartRange, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByEnrollmentStopDateRange(Date first, Date second) {
        SqlStatementBuilder selectByEnrollStopRange = new SqlStatementBuilder(selectAllSql);
        selectByEnrollStopRange.append("WHERE GroupEnrollStop > ").appendArgument(first);
        selectByEnrollStopRange.append("AND GroupEnrollStop <= ").appendArgument(second);

        return yukonJdbcTemplate.query(selectByEnrollStopRange, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByOptOutStartDateRange(Date first, Date second) {
        SqlStatementBuilder selectByOptOutStartRange = new SqlStatementBuilder(selectAllSql);
        selectByOptOutStartRange.append("WHERE OptOutStart > ").appendArgument(first);
        selectByOptOutStartRange.append("AND OptOutStart <= ").appendArgument(second);
        
        return yukonJdbcTemplate.query(selectByOptOutStartRange, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByOptOutStopDateRange(Date first, Date second) {
        SqlStatementBuilder selectByOptOutStopRange = new SqlStatementBuilder(selectAllSql);
        selectByOptOutStopRange.append("WHERE OptOutStop > ").appendArgument(first);
        selectByOptOutStopRange.append("AND OptOutStop <= ").appendArgument(second);
        
        return yukonJdbcTemplate.query(selectByOptOutStopRange, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByLMGroupIdAndAccountIdAndType(int lmGroupId, 
                                                                           int accountId, 
                                                                           int type) {
        SqlStatementBuilder selectByLMGroupIdAndAccountIdAndType = 
            new SqlStatementBuilder(selectAllSql);
        selectByLMGroupIdAndAccountIdAndType.append("WHERE LMGroupId").eq(lmGroupId);
        selectByLMGroupIdAndAccountIdAndType.append("AND AccountId").eq(accountId);
        selectByLMGroupIdAndAccountIdAndType.append("AND Type").eq(type);
        selectByLMGroupIdAndAccountIdAndType.append("ORDER BY GroupEnrollStart");

        return yukonJdbcTemplate.query(selectByLMGroupIdAndAccountIdAndType, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByInventoryIdAndAccountIdAndType(int inventoryId, 
                                                                             int accountId, 
                                                                             int type) {
        SqlStatementBuilder selectByInventoryIdAndAccountIdAndType = 
            new SqlStatementBuilder(selectAllSql);
        selectByInventoryIdAndAccountIdAndType.append("WHERE InventoryId").eq(inventoryId);
        selectByInventoryIdAndAccountIdAndType.append("AND AccountId").eq(accountId);
        selectByInventoryIdAndAccountIdAndType.append("AND Type").eq(type);

        return yukonJdbcTemplate.query(selectByInventoryIdAndAccountIdAndType, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountId(int inventoryId, 
                                                                                int lmGroupId, 
                                                                                int accountId) {

        SqlStatementBuilder selectByInventoryIdAndGroupIdAndAccountId = 
            new SqlStatementBuilder(selectAllSql);
        selectByInventoryIdAndGroupIdAndAccountId.append("WHERE InventoryId").eq(inventoryId);
        selectByInventoryIdAndGroupIdAndAccountId.append("AND LMGroupId").eq(lmGroupId);
        selectByInventoryIdAndGroupIdAndAccountId.append("AND AccountId").eq(accountId);
        
        return yukonJdbcTemplate.query(selectByInventoryIdAndGroupIdAndAccountId, rowMapper);
    }
    
    public List<LMHardwareControlGroup> 
                getByInventoryIdAndGroupIdAndAccountIdAndType(int inventoryId, int lmGroupId, 
                                                              int accountId, int type) {
        SqlStatementBuilder selectByInventoryIdAndGroupIdAndAccountIdAndType = 
            new SqlStatementBuilder(selectAllSql);
        selectByInventoryIdAndGroupIdAndAccountIdAndType.append("WHERE InventoryId").eq(inventoryId);
        selectByInventoryIdAndGroupIdAndAccountIdAndType.append("AND LMGroupId").eq(lmGroupId);
        selectByInventoryIdAndGroupIdAndAccountIdAndType.append("AND AccountId").eq(accountId);
        selectByInventoryIdAndGroupIdAndAccountIdAndType.append("AND Type").eq(type);
        
        return yukonJdbcTemplate.query(selectByInventoryIdAndGroupIdAndAccountIdAndType, rowMapper);
    }

    public List<LMHardwareControlGroup> getCurrentEnrollmentByAccountId(int accountId) {
        SqlStatementBuilder selectCurrentEnrollmentByAccountId = 
            new SqlStatementBuilder(selectAllSql);
        selectCurrentEnrollmentByAccountId.append("WHERE AccountId").eq(accountId);
        selectCurrentEnrollmentByAccountId.append("AND GroupEnrollStop IS NULL");
        selectCurrentEnrollmentByAccountId.append("AND NOT GroupEnrollStart IS NULL");
        
        return yukonJdbcTemplate.query(selectCurrentEnrollmentByAccountId, rowMapper);
    }    
    
    public List<LMHardwareControlGroup> 
                getCurrentEnrollmentByInventoryIdAndAccountId(int inventoryId, int accountId) {
        SqlStatementBuilder selectCurrentEnrollmentByInventoryIdAndAccountId = 
            new SqlStatementBuilder(selectAllSql);
        selectCurrentEnrollmentByInventoryIdAndAccountId.append("WHERE InventoryId").eq(inventoryId);
        selectCurrentEnrollmentByInventoryIdAndAccountId.append("AND AccountId").eq(accountId);
        selectCurrentEnrollmentByInventoryIdAndAccountId.append("AND GroupEnrollStop IS NULL");
        selectCurrentEnrollmentByInventoryIdAndAccountId.append("AND NOT GroupEnrollStart IS NULL");
        
        return yukonJdbcTemplate.query(selectCurrentEnrollmentByInventoryIdAndAccountId, rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> 
                getCurrentEnrollmentByProgramIdAndAccountId(int programId, int accountId) {
        SqlStatementBuilder selectCurrentEnrollmentByProgramIdAndAccountId = 
            new SqlStatementBuilder(selectAllSql);
        selectCurrentEnrollmentByProgramIdAndAccountId.append("WHERE ProgramId").eq(programId);
        selectCurrentEnrollmentByProgramIdAndAccountId.append("AND AccountId").eq(accountId);
        selectCurrentEnrollmentByProgramIdAndAccountId.append("AND GroupEnrollStop IS NULL");
        selectCurrentEnrollmentByProgramIdAndAccountId.append("AND NOT GroupEnrollStart IS NULL");
        
        return yukonJdbcTemplate.query(selectCurrentEnrollmentByProgramIdAndAccountId, rowMapper);
    }      

    public List<LMHardwareControlGroup> 
                getCurrentEnrollmentByInventoryIdAndProgramIdAndAccountId(int inventoryId, 
                                                                          int programId, 
                                                                          int accountId) {
        SqlStatementBuilder selectCurrentEnrollmentByInventoryIdProgramIdAndAccountId = 
            new SqlStatementBuilder(selectAllSql);
        selectCurrentEnrollmentByInventoryIdProgramIdAndAccountId.append("WHERE InventoryId").eq(inventoryId);
        selectCurrentEnrollmentByInventoryIdProgramIdAndAccountId.append("AND ProgramId").eq(programId);
        selectCurrentEnrollmentByInventoryIdProgramIdAndAccountId.append("AND AccountId").eq(accountId);
        selectCurrentEnrollmentByInventoryIdProgramIdAndAccountId.append("AND GroupEnrollStop IS NULL");
        selectCurrentEnrollmentByInventoryIdProgramIdAndAccountId.append("AND NOT GroupEnrollStart IS NULL");

        return yukonJdbcTemplate.query(selectCurrentEnrollmentByInventoryIdProgramIdAndAccountId, rowMapper);
    }    
    
    public List<LMHardwareControlGroup> getCurrentOptOutByProgramIdAndAccountId(int programId, 
                                                                                 int accountId) {
        SqlStatementBuilder selectCurrentOptOutsByProgramIdAndAccountId = 
            new SqlStatementBuilder(selectAllSql);
        selectCurrentOptOutsByProgramIdAndAccountId.append("WHERE ProgramId").eq(programId);
        selectCurrentOptOutsByProgramIdAndAccountId.append("AND AccountId").eq(accountId);
        selectCurrentOptOutsByProgramIdAndAccountId.append("AND OptOutStop IS NULL");
        selectCurrentOptOutsByProgramIdAndAccountId.append("AND NOT OptOutStart IS NULL");

        return yukonJdbcTemplate.query(selectCurrentOptOutsByProgramIdAndAccountId, rowMapper);
    }
    
    public List<LMHardwareControlGroup> 
                getCurrentOptOutByInventoryIdProgramIdAndAccountId(int inventoryId, int programId, 
                                                                   int accountId) {
        SqlStatementBuilder selectCurrentOptOutsByInventoryIdProgramIdAndAccountId = 
            new SqlStatementBuilder(selectAllSql);
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId.append("WHERE InventoryId").eq(inventoryId);
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId.append("AND ProgramId").eq(programId);
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId.append("AND AccountId").eq(accountId);
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId.append("AND OptOutStop IS NULL");
        selectCurrentOptOutsByInventoryIdProgramIdAndAccountId.append("AND NOT OptOutStart IS NULL");

        return yukonJdbcTemplate.query(selectCurrentOptOutsByInventoryIdProgramIdAndAccountId, rowMapper);
    }    
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getAll() {
        return yukonJdbcTemplate.query(new SqlStatementBuilder(selectAllSql), rowMapper);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareControlGroup> getAllByEnergyCompanyId(int energyCompanyId) {
        SqlStatementBuilder selectAccountId = new SqlStatementBuilder();
        selectAccountId.append("SELECT AccountId");
        selectAccountId.append("FROM ECToGenericMapping");
        selectAccountId.append("WHERE EnergyCompanyId").eq(energyCompanyId);

        SqlStatementBuilder selectAllByEnergyCompanyId = 
            new SqlStatementBuilder(selectAllSql);
        selectAllByEnergyCompanyId.append("WHERE AccountId IN (").append(selectAccountId).append(")");
        selectAllByEnergyCompanyId.append("ORDER BY AccountId");
                
        return yukonJdbcTemplate.query(selectAllByEnergyCompanyId, rowMapper);
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
        ParameterizedRowMapper<HardwareConfigAction> rowMapper =
            new ParameterizedRowMapper<HardwareConfigAction>() {
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

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareConfiguration> getOldConfigDataByInventoryId(int inventoryId) {
        List<LMHardwareConfiguration> list = yukonJdbcTemplate.query(selectOldInventoryConfigInfo, oldControlInfoRowMapper, inventoryId);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<LMHardwareConfiguration> getOldConfigDataByInventoryIdAndGroupId(int inventoryId, int lmGroupId) {
        List<LMHardwareConfiguration> list = yukonJdbcTemplate.query(selectOldInventoryLoadGroupConfigInfo, oldControlInfoRowMapper, inventoryId, lmGroupId);
        return list;
    }

    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<LMHardwareControlGroup>(yukonJdbcTemplate, nextValueHelper);
        template.withTableName(TABLE_NAME);
        template.withPrimaryKeyField("controlEntryId");
        template.withFieldMapper(controlGroupFieldMapper); 
    }
}
