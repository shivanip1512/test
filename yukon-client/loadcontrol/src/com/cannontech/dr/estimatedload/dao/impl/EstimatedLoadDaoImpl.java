package com.cannontech.dr.estimatedload.dao.impl;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.dr.estimatedload.dao.EstimatedLoadDao;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class EstimatedLoadDaoImpl implements EstimatedLoadDao{
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadDaoImpl.class);
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private LoadControlClientConnection loadControlClient;
    @Override
    public Pair<Integer, Double> getAcIdAndAverageKwLoadForLmProgram(int lmProgramId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AC.ApplianceCategoryId, AC.AverageKwLoad");
        sql.append("FROM LmProgram LMP");
        sql.append("JOIN LMProgramWebPublishing LMPWP ON LMP.DeviceID = LMPWP.DeviceID");
        sql.append("JOIN ApplianceCategory AC ON LMPWP.ApplianceCategoryID = AC.ApplianceCategoryID");
        sql.append("WHERE LMP.DeviceId").eq(lmProgramId);
        YukonRowMapper<Pair<Integer, Double>> rowMapper = new YukonRowMapper<Pair<Integer, Double>>() {
            @Override
            public Pair<Integer, Double> mapRow(YukonResultSet rs) throws SQLException {
                return new Pair<Integer, Double>(rs.getInt("ApplianceCategoryId"),
                        Double.parseDouble(rs.getString("AverageKwLoad")));
            }
        };
        
        try {
            return yukonJdbcTemplate.queryForLimitedResults(sql, rowMapper, 1).get(0);
        } catch (DataAccessException | IndexOutOfBoundsException e) {
            // No AverageKwLoad value was found which means that it was either NULL in the table, or
            // the LmProgram whose value was requested was never assigned to an appliance category
            // that the AverageKwLoad value can be grabbed from.
            return null;
        }
    }

    @Override
    public Integer getCurrentGearIdForProgram(int lmProgramId) {
         LMProgramBase program = loadControlClient.getProgram(lmProgramId);
         Integer gearNumber;
         if (((IGearProgram) program).getCurrentGear() == null) {
             gearNumber = 1;
         } else {
             gearNumber = ((IGearProgram) program).getCurrentGear().getGearNumber();
         }
         SqlStatementBuilder sql = new SqlStatementBuilder();
         sql.append("SELECT GearId");
         sql.append("FROM LmProgramDirectGear");
         sql.append("WHERE DeviceId").eq(lmProgramId);
         sql.append("AND GearNumber").eq(gearNumber);
         
         return yukonJdbcTemplate.queryForInt(sql);
    }
}
