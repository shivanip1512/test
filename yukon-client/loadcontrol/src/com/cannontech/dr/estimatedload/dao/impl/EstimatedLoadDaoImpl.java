package com.cannontech.dr.estimatedload.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.dr.estimatedload.ApplianceCategoryInfoNotFoundException;
import com.cannontech.dr.estimatedload.ApplianceCategoryNotFoundException;
import com.cannontech.dr.estimatedload.EstimatedLoadApplianceCategoryInfo;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.GearNotFoundException;
import com.cannontech.dr.estimatedload.dao.EstimatedLoadDao;
import com.cannontech.loadcontrol.LoadControlClientConnection;

public class EstimatedLoadDaoImpl implements EstimatedLoadDao{
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadDaoImpl.class);
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private LoadControlClientConnection loadControlClient;

    @Override
    public EstimatedLoadApplianceCategoryInfo getAcIdAndAverageKwLoadForLmProgram(int lmProgramId)
            throws EstimatedLoadException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AC.ApplianceCategoryId, AC.AverageKwLoad");
        sql.append("FROM LmProgram LMP");
        sql.append("JOIN LMProgramWebPublishing LMPWP ON LMP.DeviceID = LMPWP.DeviceID");
        sql.append("JOIN ApplianceCategory AC ON LMPWP.ApplianceCategoryID = AC.ApplianceCategoryID");
        sql.append("WHERE LMP.DeviceId").eq(lmProgramId);
        YukonRowMapper<EstimatedLoadApplianceCategoryInfo> rowMapper =
                new YukonRowMapper<EstimatedLoadApplianceCategoryInfo>() {
            @Override
            public EstimatedLoadApplianceCategoryInfo mapRow(YukonResultSet rs) throws SQLException {
                return new EstimatedLoadApplianceCategoryInfo(rs.getInt("ApplianceCategoryId"),
                        rs.getNullableDouble("AverageKwLoad"));
            }
        };
        
        EstimatedLoadApplianceCategoryInfo result;
        try {
            result = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("No appliance category could be found for the program with id: " + lmProgramId);
            throw new ApplianceCategoryNotFoundException();
        }
        if (result.getAvgKwLoad() == null) {
            // No AverageKwLoad value was found for the appliance category. 
            log.error("No average kw load value was found when attempting to look up this info for program id: "
                    + lmProgramId);
            throw new ApplianceCategoryInfoNotFoundException();
        }
        return result;
    }

    @Override
    public Integer getCurrentGearIdForProgram(int lmProgramId, int gearNumber) throws EstimatedLoadException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GearId");
        sql.append("FROM LmProgramDirectGear");
        sql.append("WHERE DeviceId").eq(lmProgramId);
        sql.append("AND GearNumber").eq(gearNumber);
        
        try {
            return yukonJdbcTemplate.queryForInt(sql);
        } catch(DataAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug("Gear not found for LM program id: " + lmProgramId
                        + " with gear number: " + gearNumber);
            }
            throw new GearNotFoundException(lmProgramId);
        }
    }

    @Override
    public List<Integer> findOtherEnrolledProgramsForDevicesInProgram(int programId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT LMPWP.DeviceId");
        sql.append("FROM   LMHardwareControlGroup LMHCG");
        sql.append("JOIN   LMProgramWebPublishing LMPWP ON LMPWP.ProgramId = LMHCG.ProgramId");
        sql.append("WHERE  LMHCG.InventoryId IN (");
        sql.append("    SELECT LMHCG.InventoryId");
        sql.append("    FROM   LMHardwareControlGroup LMHCG");
        sql.append("    JOIN   LMProgramWebPublishing LMPWP ON LMPWP.ProgramId = LMHCG.ProgramId");
        sql.append("    WHERE  LMHCG.Type = 1");
        sql.append("      AND  LMHCG.GroupEnrollStart IS NOT NULL");
        sql.append("      AND  LMHCG.GroupEnrollStop IS NULL");
        sql.append("      AND  LMPWP.DeviceId").eq(programId).append(")");
        sql.append("  AND  Type = 1");
        sql.append("  AND  GroupEnrollStop IS NULL");
        sql.append("  AND  DeviceId").neq(programId);
        
        return yukonJdbcTemplate.query(sql, RowMapper.INTEGER);
    }

    @Override
    public int getOverlappingEnrollmentSize(int calculatingProgramId, int controllingProgramId,
            List<Integer> previousControllingProgramIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM (");
        sql.append(getOuterOverlappingEnrollmentSql(calculatingProgramId, controllingProgramId));
        for (Integer controllingId : previousControllingProgramIds) {
            // Queries in this loop exclude previously-counted devices from contributing to the current total.
            sql.append(getNotExistsOverlappingEnrollmentSql(calculatingProgramId, controllingId));
        }
        sql.append(") T");
        
        return yukonJdbcTemplate.queryForInt(sql);
    }

    /**
     * This query is the base of the overlapping enrollment calculation.  It uses LMProgramWebPublishing to
     * determine the assigned program id of each LM program id (both calculating & controlling programs) and then
     * examines the enrollments for the two assigned programs by looking at LMHardwareControlGroup and comparing
     * inventory id and relay id for a given active enrollment.  If there is a non-null enroll start date and a null
     * enroll stop date, and the inventory id and relay id match, then that device/relay combination is enrolled
     * in both LM programs, so it contributes to the overlapping enrollment count. 
     * 
     * @param calculatingId The lm program id for which the estimated load calculation is being performed.
     * @param controllingId An lm program sharing enrollments with the calculating program and actively controlling.
     * @return The top-level SQL statement that will determine the overlapping device/relay combinations.
     */
    private SqlFragmentSource getOuterOverlappingEnrollmentSql(int calculatingId, int controllingId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        /* This query uses a self-join on LMHardwareControlGroup, comparing the InventoryId and Relay values
         * for two distinct LM programs.  The two aliases, LHCG1 and LHCG2, are used to distinguish
         * the InventoryId and Relay values in the calculating program from those in the currently 
         * controlling program. */
        sql.append("SELECT DISTINCT LHCG1.InventoryID, LHCG1.Relay");
        sql.append("FROM LMHardwareControlGroup LHCG1, LMHardwareControlGroup LHCG2,"); 
        sql.append(     "LMProgramWebPublishing LMPWP1, LMProgramWebPublishing LMPWP2");
        sql.append("WHERE LMPWP1.DeviceID").eq(calculatingId);
        sql.append(  "AND LMPWP1.ProgramID = LHCG1.ProgramId");
        sql.append(  "AND LHCG1.GroupEnrollStart IS NOT NULL");
        sql.append(  "AND LHCG1.GroupEnrollStop IS NULL");
        
        sql.append(  "AND LMPWP2.DeviceID").eq(controllingId);
        sql.append(  "AND LMPWP2.ProgramID = LHCG2.ProgramId");
        sql.append(  "AND LHCG2.GroupEnrollStart IS NOT NULL");
        sql.append(  "AND LHCG2.GroupEnrollStop IS NULL");
        
        sql.append(  "AND LHCG1.InventoryID = LHCG2.InventoryID");
        sql.append(  "AND LHCG1.Relay = LHCG2.Relay");
        return sql;
    }

    /**
     * This query is functionally identical to getOuterOverlappingEnrollmentSql() except that it is determining
     * previously counted overlapping device/relay combinations so that they can be excluded from the current
     * calculation to prevent double-counting a single device/relay enrollment when determining how much of a reduction
     * each controlling program will contribute to the kW Savings Now calculation.  If a device has overlapping
     * enrollments in more than two lm programs, it should only be counted once as it cannot actively control for two
     * programs at once.  In a situation where this behavior is requested by the operator, Yukon may attempt to send
     * messages to the device telling it to control for two programs simultaneously, but in the field a device will only
     * respond to the last message received.  It can only be controlled once, so its reduction should be counted once.
     * 
     * @param calculatingId The lm program id for which the estimated load calculation is being performed.
     * @param previousControllingId An lm program sharing enrollments with the calculating program and controlling.
     * @return The lower-level SQL statement that will determine previously used overlapping device/relay combinations
     * so that they can be excluded from the current calculation.
     */
    private SqlFragmentSource getNotExistsOverlappingEnrollmentSql(int calculatingId, int previousControllingId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        /* This query uses a self-join on LMHardwareControlGroup, comparing the InventoryId and Relay values
         * for two distinct LM programs.  The two aliases, LHCG3 and LHCG4, are used to distinguish
         * the InventoryId and Relay values in the calculating program from those in the previously considered 
         * controlling programs. By design this query must be used after getOuterOverlappingEnrollmentSql() as
         * the last two lines of this query join to LHCG1 (from the previous query) and are used to exclude from 
         * current consideration any devices that have been taken into account for previous controlling programs. */
        sql.append("AND NOT EXISTS (SELECT LHCG3.InventoryID, LHCG3.Relay");
        sql.append(                "FROM LMHardwareControlGroup LHCG3, LMHardwareControlGroup LHCG4,"); 
        sql.append(                     "LMProgramWebPublishing LMPWP3, LMProgramWebPublishing LMPWP4");
        sql.append(                "WHERE LMPWP3.DeviceID").eq(calculatingId);
        sql.append(                  "AND LMPWP3.ProgramID = LHCG3.ProgramId");
        sql.append(                  "AND LHCG3.GroupEnrollStart IS NOT NULL");
        sql.append(                  "AND LHCG3.GroupEnrollStop IS NULL");
        
        sql.append(                  "AND LMPWP4.DeviceID").eq(previousControllingId);
        sql.append(                  "AND LMPWP4.ProgramID = LHCG4.ProgramId");
        sql.append(                  "AND LHCG4.GroupEnrollStart IS NOT NULL");
        sql.append(                  "AND LHCG4.GroupEnrollStop IS NULL");
        
        sql.append(                  "AND LHCG3.InventoryID = LHCG4.InventoryID");
        sql.append(                  "AND LHCG3.Relay = LHCG4.Relay");
        sql.append(                  "AND LHCG1.InventoryID = LHCG3.InventoryID");
        sql.append(                  "AND LHCG1.Relay = LHCG3.Relay)");
        return sql;
    }

}
