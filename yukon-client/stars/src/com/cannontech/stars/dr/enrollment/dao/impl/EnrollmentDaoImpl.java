package com.cannontech.stars.dr.enrollment.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.Pair;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.program.dao.ProgramRowMapper;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;

public class EnrollmentDaoImpl implements EnrollmentDao {
    
    private SimpleJdbcTemplate simpleJdbcTemplate;

    /** These strings are to help with the row mapper.  If you use both of these
     *  strings you will have the right table connections and data returned.
     */
    private final String enrollmentSQLHeader = 
        "SELECT LMPWP.applianceCategoryId, LMPWP.programId, LMHCG.lmGroupId, "+
        "       LMHCG.relay, LMHCG.inventoryId, AB.KWCapacity "+
        "FROM LMHardwareControlGroup LMHCG "+
        "INNER JOIN LMProgramDirectGroup LMPDG ON LMPDG.lmGroupDeviceId = LMHCG.lmGroupId "+
        "INNER JOIN LMProgramWebPublishing LMPWP ON LMPDG.deviceId = LMPWP.deviceId "+
        "INNER JOIN LMHardwareConfiguration LMHC ON (LMHC.inventoryId = LMHCG.inventoryId "+
        "                                            AND LMHC.addressingGroupId = LMHCG.lmGroupId) "+
        "INNER JOIN ApplianceBase AB ON LMHC.applianceId = AB.ApplianceId ";


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
            simpleJdbcTemplate.query(accountEnrollmentSQL.toString(), 
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
		sql.append("SELECT ProgramID, ProgramOrder, ywc.Description, ywc.url, AlternateDisplayName");
		sql.append("	, PAOName, yle.EntryText as ChanceOfControl, ApplianceCategoryID, LogoLocation ");
		sql.append("FROM LMProgramWebPublishing pwp");
		sql.append("	JOIN YukonWebConfiguration ywc ON pwp.WebsettingsID = ywc.ConfigurationID");
		sql.append("	JOIN YukonPAObject ypo ON ypo.PAObjectID = pwp.DeviceID");
		sql.append("	JOIN YukonListEntry yle ON yle.EntryID = pwp.ChanceOfControlID");
		sql.append("	JOIN LMProgramDirectGroup lmpdg ON pwp.DeviceID = lmpdg.DeviceId");
		sql.append("	JOIN LMHardwareControlGroup lmhcg ON lmhcg.LMGroupID = lmpdg.LMGroupDeviceId");
		sql.append("WHERE pwp.WebsettingsID = ywc.ConfigurationID");
		sql.append("	AND lmhcg.InventoryId = ?");
		sql.append("	AND NOT lmhcg.groupEnrollStart IS NULL");
		sql.append("	AND lmhcg.groupEnrollStop IS NULL");
		
		List<Program> programList = 
			simpleJdbcTemplate.query(sql.toString(), new ProgramRowMapper(simpleJdbcTemplate), inventoryId);
		
		return programList;
	}

	@Override
	public List<Program> getEnrolledProgramIdsByInventory(Integer inventoryId,
			Date startTime, Date stopTime) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ProgramID, ProgramOrder, ywc.Description, ywc.url, AlternateDisplayName");
		sql.append("	, PAOName, yle.EntryText as ChanceOfControl, ApplianceCategoryID, LogoLocation ");
		sql.append("FROM LMProgramWebPublishing pwp");
		sql.append("	JOIN YukonWebConfiguration ywc ON pwp.WebsettingsID = ywc.ConfigurationID");
		sql.append("	JOIN YukonPAObject ypo ON ypo.PAObjectID = pwp.DeviceID");
		sql.append("	JOIN YukonListEntry yle ON yle.EntryID = pwp.ChanceOfControlID");
		sql.append("	JOIN LMProgramDirectGroup lmpdg ON pwp.DeviceID = lmpdg.DeviceId");
		sql.append("	JOIN LMHardwareControlGroup lmhcg ON lmhcg.LMGroupID = lmpdg.LMGroupDeviceId");
		sql.append("WHERE pwp.WebsettingsID = ywc.ConfigurationID");
		sql.append("	AND lmhcg.InventoryId = ?");
		sql.append("	AND lmhcg.Type = ?");
		sql.append("	AND lmhcg.GroupEnrollStart <= ?");
		sql.append("	AND (lmhcg.GroupEnrollStop IS NULL OR lmhcg.GroupEnrollStop >= ?)");
		
		List<Program> programList = 
			simpleJdbcTemplate.query(sql.toString(), new ProgramRowMapper(simpleJdbcTemplate), 
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
		sql.append("	JOIN LMProgramDirectGroup pdg ON pdg.LMGroupDeviceId = lmhcg.LMGroupId");
		sql.append("	JOIN LMProgramWebPublishing pwp ON pwp.DeviceId = pdg.DeviceId");
		sql.append("WHERE pwp.ProgramId = ?");
		sql.append("	AND lmhcg.Type = ?");
		sql.append("	AND lmhcg.OptOutStart <= ?");
		sql.append("	AND (lmhcg.OptOutStop IS NULL OR lmhcg.OptOutStop >= ?)");
		
		List<Integer> inventoryIds = simpleJdbcTemplate.query(sql.toString(), new IntegerRowMapper(), 
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
		
		List<Integer> inventoryIds = simpleJdbcTemplate.query(sql.toString(), new IntegerRowMapper(), 
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
		sql.append("	JOIN LMProgramDirectGroup pdg ON pdg.LMGroupDeviceId = lmhcg.LMGroupId");
		sql.append("	JOIN LMProgramWebPublishing pwp ON pwp.DeviceId = pdg.DeviceId");
		sql.append("WHERE pwp.ProgramId = ?");
		sql.append("	AND lmhcg.Type = ?");
		sql.append("	AND lmhcg.OptOutStart <= ?");
		sql.append("	AND (lmhcg.OptOutStop IS NULL OR lmhcg.OptOutStop >= ?)");
		
		List<LMHardwareControlGroup> history = 
			simpleJdbcTemplate.query(sql.toString(), new LMHardwareControlGroupRowMapper(), 
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
		sql.append("		JOIN LMProgramDirectGroup pdg2 ON pdg2.LMGroupDeviceId = lmhcg2.LMGroupId");
		sql.append("			WHERE lmhcg2.Type = ").appendArgument(LMHardwareControlGroup.OPT_OUT_ENTRY);
		sql.append("			AND lmhcg2.OptOutStart <= ").appendArgument(stopDate);
		sql.append("			AND (lmhcg2.OptOutStop IS NULL OR lmhcg2.OptOutStop >= ").appendArgument(startDate);
		sql.append("		) )");
		sql.append(" GROUP BY pdg.DeviceId");
		
		List<Pair<Integer,Integer>> programIdCountList = simpleJdbcTemplate.query(sql.toString(), new ParameterizedRowMapper<Pair<Integer, Integer>>() {
		    @Override
		    public Pair<Integer,Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
		        Integer inventoryCount = new Integer(rs.getInt(1));
		        Integer programId = new Integer(rs.getInt(2));
		        return new Pair<Integer, Integer>(programId, inventoryCount);
		    }
		}, sql.getArguments());
		Map<Integer, Integer> programIdCountMap = new HashMap<Integer, Integer>();
		for (Pair<Integer, Integer> pair : programIdCountList) {
			programIdCountMap.put(pair.first, pair.second);
		}
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
            
            return hardwareControlGroup;
        }
    	
    };
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}

