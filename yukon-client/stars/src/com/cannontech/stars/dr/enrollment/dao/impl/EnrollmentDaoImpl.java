package com.cannontech.stars.dr.enrollment.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;

public class EnrollmentDaoImpl implements EnrollmentDao {
    
    private SimpleJdbcTemplate simpleJdbcTemplate;

    /** These strings are to help with the row mapper.  If you use both of these
     *  strings you will have the right table connections and data returned.
     */
    private String enrollmentSQLHeader = "SELECT LMPWP.applianceCategoryId, LMPWP.programId, LMHCG.lmGroupId, " +
                                         "       LMHCG.relay, LMHCG.inventoryId, AB.KWCapacity " +
                                         "FROM LMHardwareControlGroup LMHCG, LMProgramDirectGroup LMPDG, ApplianceBase AB, " +
                                         "     LMProgramWebPublishing LMPWP, LMHardwareConfiguration LMHC ";

    private String enrollmentSQLFooter = " LMPDG.lmGroupDeviceId = LMHCG.lmGroupId " +
                                         "AND LMPDG.deviceId = LMPWP.deviceId " +
                                         "AND LMHC.inventoryId = LMHCG.inventoryId " +
                                         "AND LMHC.addressingGroupId = LMHCG.lmGroupId " +
                                         "AND LMHC.applianceId = AB.ApplianceId";


    /**
     * Gets all the programs the account is enrolled in
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<ProgramEnrollment> getActiveEnrollmentByAccountId(int accountId) {
        
        SqlStatementBuilder accountEnrollmentSQL = new SqlStatementBuilder();
        accountEnrollmentSQL.append(enrollmentSQLHeader);
        accountEnrollmentSQL.append("WHERE LMHCG.AccountId = ?");
        accountEnrollmentSQL.append("AND NOT LMHCG.groupEnrollStart IS NULL");
        accountEnrollmentSQL.append("AND LMHCG.groupEnrollStop IS NULL");
        accountEnrollmentSQL.append("AND "+enrollmentSQLFooter);

        try {
            List<ProgramEnrollment> programEnrollments = 
                simpleJdbcTemplate.query(accountEnrollmentSQL.toString(), 
                                         enrollmentRowMapper(), accountId);
            
            for (ProgramEnrollment programEnrollment : programEnrollments) {
                programEnrollment.setEnroll(true);
            }
            return programEnrollments;
            
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Collections.emptyList(); 
        }
    }
    
    private static final ParameterizedRowMapper<ProgramEnrollment> enrollmentRowMapper() {
        final ParameterizedRowMapper<ProgramEnrollment> oldConfigInfoRowMapper = new ParameterizedRowMapper<ProgramEnrollment>() {
            public ProgramEnrollment mapRow(ResultSet rs, int rowNum) throws SQLException {
                ProgramEnrollment programEnrollment = new ProgramEnrollment();
                programEnrollment.setApplianceCategoryId(rs.getInt("applianceCategoryId"));
                programEnrollment.setProgramId(rs.getInt("programId"));
                programEnrollment.setLmGroupId(rs.getInt("lmGroupId"));
                programEnrollment.setApplianceKW(rs.getDouble("KWCapacity"));
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

