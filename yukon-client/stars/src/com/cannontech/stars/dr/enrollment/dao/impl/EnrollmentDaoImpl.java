package com.cannontech.stars.dr.enrollment.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
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
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}

