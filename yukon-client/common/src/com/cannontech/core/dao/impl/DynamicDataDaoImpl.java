package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DynamicDataDao;
import com.cannontech.database.YukonJdbcTemplate;

public class DynamicDataDaoImpl implements DynamicDataDao {

    private final ParameterizedRowMapper<CapBankPointDelta> pointDeltaRowMapper = new PointDeltaRowMapper();
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public List<CapBankPointDelta> getAllPointDeltasForBankIds(List<Integer> bankIds) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        
        sqlBuilder.append("SELECT DMPR.BankId, DMPR.PointId, PAO.PAOName AS BankName,");
        sqlBuilder.append(       "PAO.PAOName AS CbcName, PAO2.PAOName AS AffectedDeviceName,");
        sqlBuilder.append(       "P.PointName AS AffectedPointName, DMPR.PreOpValue, DMPR.Delta");
        sqlBuilder.append("FROM DynamicCCMonitorPointResponse DMPR");
        sqlBuilder.append("JOIN Point P ON P.PointId = DMPR.PointId");
        sqlBuilder.append("JOIN YukonPAObject PAO ON DMPR.BankId = PAO.PAObjectId");
        sqlBuilder.append("JOIN YukonPAObject PAO2 ON P.PAObjectId = PAO2.PAObjectId");
        sqlBuilder.append("JOIN CapBank CB ON CB.DeviceId = DMPR.BankId");
        sqlBuilder.append("JOIN YukonPAObject PAOC ON PAOC.PAObjectId = CB.ControlDeviceId");
        sqlBuilder.append("JOIN CCFeederBankList FBL ON FBL.DeviceId = CB.DeviceId");
        sqlBuilder.append("JOIN CCFeederSubAssignment FSA ON FSA.FeederId = FBL.FeederId");
        sqlBuilder.append("WHERE DMPR.BankId");
        sqlBuilder.in(bankIds);
        
        return yukonJdbcTemplate.query(sqlBuilder, pointDeltaRowMapper);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    private class PointDeltaRowMapper implements ParameterizedRowMapper<CapBankPointDelta> {
        @Override
        public CapBankPointDelta mapRow(ResultSet rs, int rowNum) throws SQLException {
        	CapBankPointDelta capBankPointDelta = new CapBankPointDelta();
        	
        	capBankPointDelta.setPointId(rs.getInt("PointId"));
            capBankPointDelta.setBankId(rs.getInt("BankId"));
            capBankPointDelta.setBankName(rs.getString("BankName"));
            capBankPointDelta.setCbcName(rs.getString("CbcName"));
            capBankPointDelta.setAffectedDeviceName(rs.getString("AffectedDeviceName"));
            capBankPointDelta.setAffectedPointName(rs.getString("AffectedPointName"));
            capBankPointDelta.setPreOpValue(rs.getDouble("PreOpValue"));
            capBankPointDelta.setDelta(rs.getDouble("Delta"));
            
            return capBankPointDelta;
        }
    }
    
}
