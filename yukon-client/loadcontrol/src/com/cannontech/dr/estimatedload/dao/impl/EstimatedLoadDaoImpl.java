package com.cannontech.dr.estimatedload.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.dr.estimatedload.EstimatedLoadApplianceCategoryInfo;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException.Type;
import com.cannontech.dr.estimatedload.dao.EstimatedLoadDao;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.message.util.ConnectionException;

public class EstimatedLoadDaoImpl implements EstimatedLoadDao{
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadDaoImpl.class);
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private LoadControlClientConnection loadControlClient;

    @Override
    public EstimatedLoadApplianceCategoryInfo getAcIdAndAverageKwLoadForLmProgram(int lmProgramId)
            throws EstimatedLoadCalculationException {
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
        
        try {
            return yukonJdbcTemplate.queryForLimitedResults(sql, rowMapper, 1).get(0);
        } catch (IndexOutOfBoundsException | DataAccessException | NullPointerException e) {
            // No AverageKwLoad value was found which means that it was either NULL in the table, or the LmProgram 
            // requested was never assigned to an appliance category that the AverageKwLoad value can be grabbed from.
            log.debug("No appliance category id or average kw load value was found when attempting to look up " +
                    "this info for program id: " + lmProgramId);
            LMProgramBase programBase = getLmProgramBase(lmProgramId);
            throw new EstimatedLoadCalculationException(Type.APPLIANCE_CATEGORY_INFO_NOT_FOUND,
                    programBase.getYukonName());
        }
    }



    @Override
    public Integer getCurrentGearIdForProgram(int lmProgramId, int gearNumber) throws EstimatedLoadCalculationException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GearId");
        sql.append("FROM LmProgramDirectGear");
        sql.append("WHERE DeviceId").eq(lmProgramId);
        sql.append("AND GearNumber").eq(gearNumber);
        
        try {
            return yukonJdbcTemplate.queryForInt(sql);
        } catch(DataAccessException e) {
            LMProgramBase programBase = getLmProgramBase(lmProgramId);
            log.debug("Gear not found for LM Program: " + programBase.getYukonName()
                    + " with gear number: " + gearNumber);
            throw new EstimatedLoadCalculationException(Type.GEAR_NUMBER_NOT_FOUND, programBase.getYukonName(),
                    gearNumber);
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
        sql.append("      AND  LMHCG.GroupEnrollStop IS NULL");
        sql.append("      AND  LMPWP.DeviceId").eq(programId).append(")");
        sql.append("  AND  Type = 1");
        sql.append("  AND  GroupEnrollStop IS NULL");
        
        return yukonJdbcTemplate.query(sql, RowMapper.INTEGER);
    }

    private LMProgramBase getLmProgramBase(int lmProgramId) throws EstimatedLoadCalculationException {
        LMProgramBase programBase;
        try {
            programBase = loadControlClient.getProgramSafe(lmProgramId);
        } catch (ConnectionException e2){
            throw new EstimatedLoadCalculationException(Type.LOAD_MANAGEMENT_SERVER_NOT_CONNECTED);
        } catch (NotFoundException e2) {
            throw new EstimatedLoadCalculationException(Type.LOAD_MANAGEMENT_DATA_NOT_FOUND);
        }
        if (programBase == null) {
            throw new EstimatedLoadCalculationException(Type.LOAD_MANAGEMENT_DATA_NOT_FOUND);
        }
        return programBase;
    }
}
