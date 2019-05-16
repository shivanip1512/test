package com.cannontech.stars.dr.enrollment.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.program.dao.ProgramRowMapper;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class EnrollmentDaoImpl implements EnrollmentDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private CustomerAccountDao customerAccountDao;

    /** These strings are to help with the row mapper.  If you use both of these
     *  strings you will have the right table connections and data returned.
     */
    private final String enrollmentSQLHeader =
        "SELECT AB.applianceCategoryId, AB.programId, LMHCG.lmGroupId, "+
        "       LMHCG.relay, LMHCG.inventoryId, AB.KWCapacity, AB.ApplianceId "+
        "FROM LMHardwareControlGroup LMHCG "+
        "INNER JOIN LMHardwareConfiguration LMHC ON LMHC.inventoryId = LMHCG.inventoryId "+
        "INNER JOIN ApplianceBase AB ON LMHC.applianceId = AB.ApplianceId " +
        "                           AND LMHCG.programId = AB.programId " +
    	"							AND LMHCG.accountId = AB.accountId ";


    @Override
    public Set<Integer> getActiveEnrolledInventoryIdsForGroupIds(Collection<Integer> groupIds) {

        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT LMHCG.InventoryId");
        sql.append("FROM LMHardwareControlGroup LMHCG");
        sql.append("WHERE LMGroupId").in(groupIds);
        sql.append(  "AND NOT LMHCG.groupEnrollStart IS NULL");
        sql.append(  "AND LMHCG.groupEnrollStop IS NULL");

        List<Integer> inventoryIds = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        Set<Integer> uniqueInventoryIds = Sets.newHashSet(inventoryIds);

        return uniqueInventoryIds;
    }
    
    @Override
    public Multimap<Integer, Integer> getActiveEnrolledInventoryIdsMapForGroupIds(Collection<Integer> groupIds) {

        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT LMHCG.InventoryId, LMGroupId");
                sql.append("FROM LMHardwareControlGroup LMHCG");
                sql.append("WHERE LMGroupId").in(groupIds);
                sql.append("AND NOT LMHCG.groupEnrollStart IS NULL");
                sql.append("AND LMHCG.groupEnrollStop IS NULL");
                return sql;
            }
        };
        RowMapper<Map.Entry<Integer, Integer>> rowMapper = new RowMapper<Entry<Integer, Integer>>() {
            @Override
            public Entry<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer groupId = rs.getInt("LMGroupId");
                Integer inventoryId = rs.getInt("InventoryId");
                return Maps.immutableEntry(groupId, inventoryId);
            }
        };
        return template.multimappedQuery(sqlGenerator, groupIds, rowMapper, Functions.identity());
    }

    /**
     * Gets all the programs the account is enrolled in
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<ProgramEnrollment> getActiveEnrollmentsByAccountId(int accountId) {

        SqlStatementBuilder accountEnrollmentSQL = new SqlStatementBuilder();
        accountEnrollmentSQL.append(enrollmentSQLHeader);
        accountEnrollmentSQL.append("WHERE LMHCG.AccountId").eq(accountId);
        accountEnrollmentSQL.append("AND NOT LMHCG.groupEnrollStart IS NULL");
        accountEnrollmentSQL.append("AND LMHCG.groupEnrollStop IS NULL");

        List<ProgramEnrollment> programEnrollments =
        	yukonJdbcTemplate.query(accountEnrollmentSQL,
                                     enrollmentRowMapper);

        for (ProgramEnrollment programEnrollment : programEnrollments) {
            programEnrollment.setEnroll(true);
        }
        return programEnrollments;
    }

    @Override
    public boolean isAccountEnrolled(int accountId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM LMHardwareControlGroup");
        sql.append("WHERE AccountId").eq(accountId);
        sql.append("  AND Type").eq(LMHardwareControlGroup.ENROLLMENT_ENTRY);
        sql.append("  AND GroupEnrollStop IS NULL");

        int activeEnrollments = yukonJdbcTemplate.queryForInt(sql);

        return activeEnrollments > 0;
    }

    @Override
    public List<ProgramEnrollment> getActiveEnrollmentsByInventory(int inventoryId) {

        /** Will be 0 for devices not assigned to an account */
        int accountId = customerAccountDao.getAccountByInventoryId(inventoryId).getAccountId();

        SqlStatementBuilder accountEnrollmentSQL = new SqlStatementBuilder();
        accountEnrollmentSQL.append(enrollmentSQLHeader);
        accountEnrollmentSQL.append("WHERE lmhcg.accountId").eq(accountId);
        accountEnrollmentSQL.append("AND lmhcg.inventoryId").eq(inventoryId);
        accountEnrollmentSQL.append("AND lmhcg.groupEnrollStart IS NOT NULL");
        accountEnrollmentSQL.append("AND lmhcg.groupEnrollStop IS NULL");

        List<ProgramEnrollment> programEnrollments =
            yukonJdbcTemplate.query(accountEnrollmentSQL, enrollmentRowMapper);

        for (ProgramEnrollment programEnrollment : programEnrollments) {
            programEnrollment.setEnroll(true);
        }
        return programEnrollments;
    }

    @Override
    public List<ProgramEnrollment> getHistoricalEnrollmentsByInventoryId(
            int inventoryId, Instant when) {
        SqlStatementBuilder accountEnrollmentSQL = new SqlStatementBuilder();
        accountEnrollmentSQL.append(enrollmentSQLHeader);
        accountEnrollmentSQL.append("WHERE lmhcg.inventoryId").eq(inventoryId);
        accountEnrollmentSQL.append("AND lmhcg.type").eq(LMHardwareControlGroup.ENROLLMENT_ENTRY);
        accountEnrollmentSQL.append("AND lmhcg.groupEnrollStart").lte(when);
        accountEnrollmentSQL.append("AND (lmhcg.groupEnrollStop IS NULL");
        accountEnrollmentSQL.append(    "OR lmhcg.groupEnrollStop").gt(when).append(")");

        List<ProgramEnrollment> programEnrollments =
            yukonJdbcTemplate.query(accountEnrollmentSQL, enrollmentRowMapper);

        for (ProgramEnrollment programEnrollment : programEnrollments) {
            programEnrollment.setEnroll(true);
        }
        return programEnrollments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramEnrollment> findConflictingEnrollments(int accountId,
            int assignedProgramId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(enrollmentSQLHeader);
        sql.append("WHERE lmhcg.accountId").eq(accountId);
        sql.append("AND ab.applianceCategoryId");
        sql.append(    "IN (SELECT applianceCategoryid");
        sql.append(        "FROM lmProgramWebPublishing");
        sql.append(        "WHERE programId").eq(assignedProgramId).append(")");
        sql.append("AND lmhcg.programId").neq(assignedProgramId);
        sql.append("AND lmhcg.groupEnrollStart IS NOT NULL");
        sql.append("AND lmhcg.groupEnrollStop IS NULL");

        List<ProgramEnrollment> retVal = null;
        retVal = yukonJdbcTemplate.query(sql, enrollmentRowMapper);
        for (ProgramEnrollment programEnrollment : retVal) {
            programEnrollment.setEnroll(false);
        }

        return retVal;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramEnrollment> findOtherApplianceConflictingEnrollments(int accountId, int assignedProgramId,
            List<Integer> inventoryIds, int assignedProgramCategoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(enrollmentSQLHeader);
        sql.append("WHERE lmhcg.AccountId").eq(accountId);
        // Same device enrollment in different appliance category, leads to conflict
        sql.append("AND ab.ApplianceCategoryId").neq(assignedProgramCategoryId);
        sql.append("AND lmhc.InventoryID ").in(inventoryIds);
        sql.append("AND lmhcg.GroupEnrollStart IS NOT NULL AND lmhcg.GroupEnrollStop IS NULL");
        List<ProgramEnrollment> retVal = null;
        retVal = yukonJdbcTemplate.query(sql, enrollmentRowMapper);
        for (ProgramEnrollment programEnrollment : retVal) {
            programEnrollment.setEnroll(false);
        }
        return retVal;
    }
    
	@Override
	public List<Program> getEnrolledProgramIdsByInventory(Integer inventoryId, Date startTime, Date stopTime) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT pwp.ProgramID, ProgramOrder, ywc.Description, ywc.url, AlternateDisplayName");
		sql.append("  , PAOName, yle.EntryText as ChanceOfControl, ApplianceCategoryID, LogoLocation, ypo.Type, lmhcg.Relay");
		sql.append("FROM LMProgramWebPublishing pwp");
		sql.append("	JOIN YukonWebConfiguration ywc ON pwp.WebsettingsID = ywc.ConfigurationID");
		sql.append("	JOIN YukonPAObject ypo ON ypo.PAObjectID = pwp.DeviceID");
		sql.append("	JOIN YukonListEntry yle ON yle.EntryID = pwp.ChanceOfControlID");
		sql.append("	JOIN LMHardwareControlGroup lmhcg ON lmhcg.ProgramID = pwp.ProgramID");
		sql.append("WHERE lmhcg.InventoryId").eq(inventoryId);
		sql.append("AND lmhcg.Type").eq(LMHardwareControlGroup.ENROLLMENT_ENTRY);

		if (startTime == null && stopTime == null) {

			Date now = new Date();
			sql.append("AND lmhcg.GroupEnrollStart IS NOT NULL");
			sql.append("AND (lmhcg.GroupEnrollStop").gt(now).append("OR lmhcg.GroupEnrollStop IS NULL)");

		} else {

			if (startTime == null) {
				startTime = new Date(0); //epoch
			}
			if (stopTime == null) {
				stopTime = new Date(); // now
			}

			sql.append("AND lmhcg.GroupEnrollStart").lte(stopTime);
			sql.append("AND (lmhcg.GroupEnrollStop").gte(startTime).append("OR lmhcg.GroupEnrollStop IS NULL)");
		}

		List<Program> programList = yukonJdbcTemplate.query(sql, new ProgramRowMapper(yukonJdbcTemplate) {
            @Override
            public Program mapRow(YukonResultSet rs) throws SQLException {
                Program program = super.mapRow(rs);
                program.setRelay(rs.getInt("Relay"));
                return program;
            }
        });

		return programList;
	}

	@Override
	public List<Integer> getOptedOutInventory(Program program, Date startDate, Date stopDate) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DISTINCT lmhcg.InventoryId ");
		sql.append("FROM LMHardwareControlGroup lmhcg");
		sql.append("WHERE lmhcg.ProgramId").eq(program.getProgramId());
		sql.append("	AND lmhcg.Type").eq_k(LMHardwareControlGroup.OPT_OUT_ENTRY);
		sql.append("	AND lmhcg.OptOutStart").lte(stopDate);
		sql.append("	AND (lmhcg.OptOutStop IS NULL");
		sql.append("         OR lmhcg.OptOutStop").gte(startDate).append(")");

		List<Integer> inventoryIds = yukonJdbcTemplate.query(sql, TypeRowMapper.INTEGER);
		return inventoryIds;
	}

	@Override
	public List<Integer> getCurrentlyOptedOutInventory() {

		Date now = new Date();

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DISTINCT lmhcg.InventoryId ");
		sql.append("FROM LMHardwareControlGroup lmhcg");
		sql.append("WHERE lmhcg.Type = ?");
		sql.append("	AND lmhcg.OptOutStart <= ?");
		sql.append("	AND lmhcg.OptOutStop IS NULL");

		List<Integer> inventoryIds = yukonJdbcTemplate.query(sql.toString(), new IntegerRowMapper(),
				LMHardwareControlGroup.OPT_OUT_ENTRY,
				now);
		return inventoryIds;
	}

	@Override
	public Map<Integer,Integer> getActiveEnrollmentExcludeOptOutCount(Date startDate, Date stopDate) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append(" SELECT COUNT(DISTINCT lmhcg.InventoryId), pdg.DeviceId ");
		sql.append(" FROM LMHardwareControlGroup lmhcg");
		sql.append(" JOIN LMProgramDirectGroup pdg ON pdg.LMGroupDeviceId = lmhcg.LMGroupId");
		sql.append("	WHERE lmhcg.Type = ").appendArgument(LMHardwareControlGroup.ENROLLMENT_ENTRY);
        sql.append("	AND NOT LMHCG.groupEnrollStart IS NULL");
        sql.append("	AND LMHCG.groupEnrollStop IS NULL");
		sql.append("	AND lmhcg.InventoryId not in ");
		sql.append("		(SELECT DISTINCT lmhcg2.InventoryId ");
		sql.append("		FROM LMHardwareControlGroup lmhcg2");
		sql.append("			WHERE lmhcg2.Type = ").appendArgument(LMHardwareControlGroup.OPT_OUT_ENTRY);
		sql.append("			AND lmhcg2.OptOutStart <= ").appendArgument(stopDate);
		sql.append("			AND (lmhcg2.OptOutStop IS NULL OR lmhcg2.OptOutStop >= ").appendArgument(startDate);
		sql.append("		) )");
		sql.append(" GROUP BY pdg.DeviceId");

		final Map<Integer, Integer> programIdCountMap = new HashMap<Integer, Integer>();
		yukonJdbcTemplate.query(sql.toString(), sql.getArguments(), new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
		        Integer inventoryCount = new Integer(rs.getInt(1));
		        Integer programId = new Integer(rs.getInt(2));
		        programIdCountMap.put(programId, inventoryCount);
			}
		});
		return programIdCountMap;
	}

	@Override
    public boolean isInService(int inventoryId) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();

	    sql.append("SELECT le.yukonDefinitionId FROM lmHardwareEvent he");
	    sql.append("JOIN lmCustomerEventBase ceb ON he.eventId = ceb.eventId");
	    sql.append("JOIN yukonListEntry le ON ceb.actionId = le.entryId");
	    sql.append("WHERE le.yukonDefinitionId IN (");
	    sql.append(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).append(",");
	    sql.append(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP).append(",");
	    sql.append(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION).append(",");
	    sql.append(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).append(")");
	    sql.append("AND he.inventoryId").eq(inventoryId);
	    sql.append("ORDER BY eventDateTime DESC");

	    List<Integer> actions = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
	    if (actions.size() == 0) {
	        return true;
	    }

	    int lastAction = actions.get(0);
	    return lastAction == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED
	        || lastAction == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP;
    }

	@Override
    public boolean isEnrolled(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM lmHardwareControlGroup hcg");
        sql.append("WHERE hcg.groupEnrollStart IS NOT NULL");
        sql.append(  "AND hcg.groupEnrollStop IS NULL");
        sql.append(  "AND hcg.type").eq(LMHardwareControlGroup.ENROLLMENT_ENTRY);
        sql.append(  "AND hcg.inventoryId").eq(inventoryId);
        return yukonJdbcTemplate.queryForInt(sql) > 0;
    }

    private final static YukonRowMapper<ProgramEnrollment> enrollmentRowMapper = new YukonRowMapper<ProgramEnrollment>(){
        @Override
        public ProgramEnrollment mapRow(YukonResultSet rs) throws SQLException {
            ProgramEnrollment programEnrollment = new ProgramEnrollment();
            programEnrollment.setApplianceCategoryId(rs.getInt("applianceCategoryId"));
            programEnrollment.setAssignedProgramId(rs.getInt("programId"));
            programEnrollment.setLmGroupId(rs.getInt("lmGroupId"));
            programEnrollment.setApplianceKW(rs.getFloat("KWCapacity"));
            programEnrollment.setRelay(rs.getInt("relay"));
            programEnrollment.setInventoryId(rs.getInt("inventoryId"));
            return programEnrollment;
        }};

    private class LMHardwareControlGroupRowMapper
                        implements YukonRowMapper<LMHardwareControlGroup> {

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

    }

}