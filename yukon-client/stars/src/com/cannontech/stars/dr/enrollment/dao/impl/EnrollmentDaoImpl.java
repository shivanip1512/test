package com.cannontech.stars.dr.enrollment.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.program.dao.ProgramRowMapper;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;

public class EnrollmentDaoImpl implements EnrollmentDao {
    
    private YukonJdbcTemplate yukonJdbcTemplate;

    /** These strings are to help with the row mapper.  If you use both of these
     *  strings you will have the right table connections and data returned.
     */
    private final String enrollmentSQLHeader = 
        "SELECT AB.applianceCategoryId, AB.programId, LMHCG.lmGroupId, "+
        "       LMHCG.relay, LMHCG.inventoryId, AB.KWCapacity "+
        "FROM LMHardwareControlGroup LMHCG "+
        "INNER JOIN LMHardwareConfiguration LMHC ON LMHC.inventoryId = LMHCG.inventoryId "+
        "INNER JOIN ApplianceBase AB ON LMHC.applianceId = AB.ApplianceId " + 
        "                           AND LMHCG.programId = AB.programId ";


    /**
     * Gets all the programs the account is enrolled in
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<ProgramEnrollment> getActiveEnrollmentsByAccountId(int accountId) {
        
        SqlStatementBuilder accountEnrollmentSQL = new SqlStatementBuilder();
        accountEnrollmentSQL.append(enrollmentSQLHeader);
        accountEnrollmentSQL.append("WHERE LMHCG.AccountId = ?");
        accountEnrollmentSQL.append("AND NOT LMHCG.groupEnrollStart IS NULL");
        accountEnrollmentSQL.append("AND LMHCG.groupEnrollStop IS NULL");

        List<ProgramEnrollment> programEnrollments = 
        	yukonJdbcTemplate.query(accountEnrollmentSQL.toString(), 
                                     enrollmentRowMapper(), accountId);
            
        for (ProgramEnrollment programEnrollment : programEnrollments) {
            programEnrollment.setEnroll(true);
        }
        return programEnrollments;
    }
    
	@Override
	@Transactional
	public List<Program> getCurrentlyEnrolledProgramsByInventoryId(int inventoryId) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT pwp.ProgramID, ProgramOrder, ywc.Description, ywc.url, AlternateDisplayName");
		sql.append("	, PAOName, yle.EntryText as ChanceOfControl, ApplianceCategoryID, LogoLocation ");
		sql.append("FROM LMProgramWebPublishing pwp");
		sql.append("	JOIN YukonWebConfiguration ywc ON pwp.WebsettingsID = ywc.ConfigurationID");
		sql.append("	JOIN YukonPAObject ypo ON ypo.PAObjectID = pwp.DeviceID");
		sql.append("	JOIN YukonListEntry yle ON yle.EntryID = pwp.ChanceOfControlID");
		sql.append("	JOIN LMHardwareControlGroup lmhcg ON lmhcg.ProgramID = pwp.ProgramID");
		sql.append("WHERE lmhcg.InventoryId = ? AND lmhcg.Type = ?");
		sql.append("	AND NOT lmhcg.groupEnrollStart IS NULL");
		sql.append("	AND lmhcg.groupEnrollStop IS NULL");
		
		List<Program> programList = yukonJdbcTemplate.query(sql.toString(),
                                                             new ProgramRowMapper(yukonJdbcTemplate),
                                                             inventoryId,
                                                             LMHardwareControlGroup.ENROLLMENT_ENTRY);
		
		return programList;
	}

	@Override
	@Transactional
	public boolean isInventoryCurrentlyEnrolledProgram(int inventoryId, int programId) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT COUNT(*)");
		sql.append("FROM LMHardwareControlGroup lmhcg");
		sql.append("	JOIN LMProgramWebPublishing pwp ON (lmhcg.ProgramId = pwp.ProgramId)");
		sql.append("WHERE lmhcg.InventoryId = ?").appendArgument(inventoryId);
		sql.append("	AND pwp.DeviceId = ?").appendArgument(programId);
		sql.append("	AND lmhcg.Type = ?").appendArgument(LMHardwareControlGroup.ENROLLMENT_ENTRY);
		sql.append("	AND lmhcg.ProgramId > 0");
		sql.append("	AND NOT lmhcg.groupEnrollStart IS NULL");
		sql.append("	AND lmhcg.groupEnrollStop IS NULL");
		
		return yukonJdbcTemplate.queryForInt(sql) > 0;
	}
	
	@Override
	public List<Program> getEnrolledProgramIdsByInventory(Integer inventoryId,
			Date startTime, Date stopTime) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT pwp.ProgramID, ProgramOrder, ywc.Description, ywc.url, AlternateDisplayName");
		sql.append("	, PAOName, yle.EntryText as ChanceOfControl, ApplianceCategoryID, LogoLocation ");
		sql.append("FROM LMProgramWebPublishing pwp");
		sql.append("	JOIN YukonWebConfiguration ywc ON pwp.WebsettingsID = ywc.ConfigurationID");
		sql.append("	JOIN YukonPAObject ypo ON ypo.PAObjectID = pwp.DeviceID");
		sql.append("	JOIN YukonListEntry yle ON yle.EntryID = pwp.ChanceOfControlID");
		sql.append("	JOIN LMHardwareControlGroup lmhcg ON lmhcg.ProgramID = pwp.ProgramID");
		sql.append("WHERE lmhcg.InventoryId = ? AND lmhcg.Type = ?");
		sql.append("	AND lmhcg.GroupEnrollStart <= ?");
		sql.append("	AND (lmhcg.GroupEnrollStop IS NULL OR lmhcg.GroupEnrollStop >= ?)");
		
		List<Program> programList = 
			yukonJdbcTemplate.query(sql.toString(), new ProgramRowMapper(yukonJdbcTemplate), 
					inventoryId,
					LMHardwareControlGroup.ENROLLMENT_ENTRY,
					stopTime,
					startTime);
		
		return programList;
	}
	
	@Override
	public List<Integer> getOptedOutInventory(Program program, Date startDate,
			Date stopDate) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DISTINCT lmhcg.InventoryId ");
		sql.append("FROM LMHardwareControlGroup lmhcg");
		sql.append("WHERE lmhcg.ProgramId = ?");
		sql.append("	AND lmhcg.Type = ?");
		sql.append("	AND lmhcg.OptOutStart <= ?");
		sql.append("	AND (lmhcg.OptOutStop IS NULL OR lmhcg.OptOutStop >= ?)");
		
		List<Integer> inventoryIds = yukonJdbcTemplate.query(sql.toString(), new IntegerRowMapper(), 
													program.getProgramId(),
													LMHardwareControlGroup.OPT_OUT_ENTRY,
													stopDate,
													startDate);
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
	public List<LMHardwareControlGroup> getOptOutHistoryByProgram(
			Program program, Date startDate, Date stopDate) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT lmhcg.* ");
		sql.append("FROM LMHardwareControlGroup lmhcg");
		sql.append("WHERE lmhcg.ProgramId = ?");
		sql.append("	AND lmhcg.Type = ?");
		sql.append("	AND lmhcg.OptOutStart <= ?");
		sql.append("	AND (lmhcg.OptOutStop IS NULL OR lmhcg.OptOutStop >= ?)");
		
		List<LMHardwareControlGroup> history = 
			yukonJdbcTemplate.query(sql.toString(), new LMHardwareControlGroupRowMapper(), 
				program.getProgramId(),
				LMHardwareControlGroup.OPT_OUT_ENTRY,
				stopDate,
				startDate);

		return history;
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
		yukonJdbcTemplate.getJdbcOperations().query(sql.toString(), sql.getArguments(), new RowCallbackHandler() {
		
			@Override
			public void processRow(ResultSet rs) throws SQLException {
		        Integer inventoryCount = new Integer(rs.getInt(1));
		        Integer programId = new Integer(rs.getInt(2));
		        programIdCountMap.put(programId, inventoryCount);
			}
		});
		return programIdCountMap;
	}
    
    private static final ParameterizedRowMapper<ProgramEnrollment> enrollmentRowMapper() {
        final ParameterizedRowMapper<ProgramEnrollment> oldConfigInfoRowMapper = new ParameterizedRowMapper<ProgramEnrollment>() {
            public ProgramEnrollment mapRow(ResultSet rs, int rowNum) throws SQLException {
                ProgramEnrollment programEnrollment = new ProgramEnrollment();
                programEnrollment.setApplianceCategoryId(rs.getInt("applianceCategoryId"));
                programEnrollment.setProgramId(rs.getInt("programId"));
                programEnrollment.setLmGroupId(rs.getInt("lmGroupId"));
                programEnrollment.setApplianceKW(rs.getFloat("KWCapacity"));
                programEnrollment.setRelay(rs.getInt("relay"));
                programEnrollment.setInventoryId(rs.getInt("inventoryId"));
                return programEnrollment;
            }
        };
        return oldConfigInfoRowMapper;
    }
    
    private class LMHardwareControlGroupRowMapper implements ParameterizedRowMapper<LMHardwareControlGroup> {
        
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
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
}

