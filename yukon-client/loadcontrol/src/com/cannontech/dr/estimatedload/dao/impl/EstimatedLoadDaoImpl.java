package com.cannontech.dr.estimatedload.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
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
        
        List<EstimatedLoadApplianceCategoryInfo> results = yukonJdbcTemplate.queryForLimitedResults(sql, rowMapper, 1);
        if (results.size() != 1) {
            // The program is not currently assigned to an appliance category.
            log.error("No appliance category could be found for the program with id: " + lmProgramId);
            throw new ApplianceCategoryNotFoundException();
        } else if (results.get(0).getAvgKwLoad() == null) {
            // No AverageKwLoad value was found for the appliance category. 
            log.error("No average kw load value was found when attempting to look up this info for program id: "
                    + lmProgramId);
            throw new ApplianceCategoryInfoNotFoundException();
        }
        return results.get(0);
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
        sql.append("      AND  LMHCG.GroupEnrollStop IS NULL");
        sql.append("      AND  LMPWP.DeviceId").eq(programId).append(")");
        sql.append("  AND  Type = 1");
        sql.append("  AND  GroupEnrollStop IS NULL");
        
        return yukonJdbcTemplate.query(sql, RowMapper.INTEGER);
    }

}
