package com.cannontech.dr.estimatedload.dao.impl;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.dr.estimatedload.EstimatedLoadApplianceCategoryInfo;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException.Type;
import com.cannontech.dr.estimatedload.dao.EstimatedLoadDao;
import com.cannontech.loadcontrol.LoadControlClientConnection;

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
        } catch (IndexOutOfBoundsException | DataAccessException e) {
            // No AverageKwLoad value was found which means that it was either NULL in the table, or the LmProgram 
            // requested was never assigned to an appliance category that the AverageKwLoad value can be grabbed from.
            log.debug("No appliance category id or average kw load value was found when attempting to look up " +
                    "this info for program id: " + lmProgramId);
            String programName = loadControlClient.getProgramSafe(lmProgramId).getYukonName();
            throw new EstimatedLoadCalculationException(Type.APPLIANCE_CATEGORY_INFO_NOT_FOUND, programName);
        }
    }

    @Override
    public Integer getCurrentGearIdForProgram(int lmProgramId, int gearNumber) {
         SqlStatementBuilder sql = new SqlStatementBuilder();
         sql.append("SELECT GearId");
         sql.append("FROM LmProgramDirectGear");
         sql.append("WHERE DeviceId").eq(lmProgramId);
         sql.append("AND GearNumber").eq(gearNumber);
         
         return yukonJdbcTemplate.queryForInt(sql);
    }
}
