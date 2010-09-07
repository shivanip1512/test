package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.model.PointDelta;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DynamicDataDao;
import com.cannontech.database.YukonJdbcTemplate;

public class DynamicDataDaoImpl implements DynamicDataDao {

    private final ParameterizedRowMapper<PointDelta> pointDeltaRowMapper = new PointDeltaRowMapper();
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public List<PointDelta> getAllPointDeltasForBankIds(List<Integer> bankIds) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT dmpr.BankID,dmpr.PointID,yp.paoname as BankName,");
        sqlBuilder.append("ypc.PAOName as CbcName,yp2.paoname as AffectedDeviceName,");
        sqlBuilder.append("pp.pointname as AffectedPointName,dmpr.preopvalue,dmpr.delta");
        sqlBuilder.append("FROM dynamicccmonitorpointresponse dmpr,YukonPAObject yp,");
        sqlBuilder.append("YukonPAObject ypc,capbank c,YukonPAObject yp2,point pp,");
        sqlBuilder.append("ccfeederbanklist fb,CCFeederSubAssignment fsa");
        sqlBuilder.append("WHERE pp.POINTID = dmpr.PointID");
        sqlBuilder.append("and dmpr.BankID = yp.PAObjectID");
        sqlBuilder.append("and pp.PAObjectID = yp2.PAObjectID");
        sqlBuilder.append("and ypc.PAObjectID = c.CONTROLDEVICEID");
        sqlBuilder.append("and c.DEVICEID = dmpr.BankID");
        sqlBuilder.append("and fb.DeviceID = c.DEVICEID");
        sqlBuilder.append("and fsa.FeederID = fb.FeederID");
        sqlBuilder.append("and dmpr.BankID");
        sqlBuilder.in(bankIds);
        
        return yukonJdbcTemplate.query(sqlBuilder, pointDeltaRowMapper);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    private class PointDeltaRowMapper implements ParameterizedRowMapper<PointDelta> {
        @Override
        public PointDelta mapRow(ResultSet rs, int rowNum) throws SQLException {
            
            int pointId = rs.getInt("PointID");
            int bankId = rs.getInt("BankID");
            String bankName = rs.getString("BankName");
            String cbcName = rs.getString("CbcName");
            String affectedDeviceName = rs.getString("AffectedDeviceName");
            String affectedPointName = rs.getString("AffectedPointName");
            double preOpValue = rs.getDouble("preopvalue");
            double delta = rs.getDouble("delta");
            
            return new PointDelta(pointId,bankId,bankName,cbcName,affectedDeviceName,affectedPointName,preOpValue,delta);
        }
    }
    
}
