package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.enums.Phase;

public class CcMonitorBankListDaoImpl implements CcMonitorBankListDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    private final YukonRowMapper<VoltageLimitedDeviceInfo> deviceInfoRowMapper = new YukonRowMapper<VoltageLimitedDeviceInfo>() {
        @Override
        public VoltageLimitedDeviceInfo mapRow(YukonResultSet rs) throws SQLException {
            int paoId = rs.getInt("PaObjectId");
            String paoName = rs.getString("PaoName");
            PaoType paoType = rs.getEnum("Type", PaoType.class);
            PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, paoType);
            int pointId = rs.getInt("PointId");
            String pointName = rs.getString("PointName");
            Phase phase = rs.getEnum("Phase", Phase.class);
            double lowerLimit = rs.getDouble("LowerBandwidth");
            double upperLimit = rs.getDouble("UpperBandwidth");
            boolean overrideStrategy = rs.getEnum("OverrideStrategy", YNBoolean.class).getBoolean();
            
            VoltageLimitedDeviceInfo deviceInfo = new VoltageLimitedDeviceInfo();
            deviceInfo.setPaoIdentifier(paoIdentifier);
            deviceInfo.setPaoName(paoName);
            deviceInfo.setPointId(pointId);
            deviceInfo.setPointName(pointName);
            deviceInfo.setPhase(phase);
            deviceInfo.setLowerLimit(lowerLimit);
            deviceInfo.setUpperLimit(upperLimit);
            deviceInfo.setOverrideStrategy(overrideStrategy);
            
            return deviceInfo;
        }
    };
    
    @Override
    public List<VoltageLimitedDeviceInfo> getDeviceInfoByZoneId(int zoneId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PaObjectId, ypo.PaoName, ypo.Type, p.PointId, p.PointName, cc.Phase, cc.LowerBandwidth, cc.UpperBandwidth, cc.OverrideStrategy");
        sql.append("FROM CcMonitorBankList cc");
        sql.append("JOIN YukonPaObject ypo ON cc.DeviceId = ypo.PaObjectId");
        sql.append("JOIN Point p ON cc.PointId = p.PointId");
        sql.append("WHERE cc.DeviceId IN (");
        sql.append(  "SELECT DeviceId");
        sql.append(  "FROM CapBankToZoneMapping");
        sql.append(  "WHERE ZoneId").eq_k(zoneId);
        sql.append(")");
        sql.append("OR cc.DeviceId IN (");
        sql.append(  "SELECT RegulatorId");
        sql.append(  "FROM RegulatorToZoneMapping");
        sql.append(  "WHERE ZoneId").eq_k(zoneId);
        sql.append(")");
        
        List<VoltageLimitedDeviceInfo> deviceInfoList = yukonJdbcTemplate.query(sql, deviceInfoRowMapper);
        return deviceInfoList;
    }
    
    @Override
    public void updateDeviceInfo(VoltageLimitedDeviceInfo deviceInfo) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE CcMonitorBankList");
        sql.append("SET Phase").eq(deviceInfo.getPhase());
        sql.append(", LowerBandwidth").eq(deviceInfo.getLowerLimit());
        sql.append(", UpperBandwidth").eq(deviceInfo.getUpperLimit());
        sql.append(", OverrideStrategy").eq(YNBoolean.valueOf(deviceInfo.isOverrideStrategy()));
        sql.append("WHERE DeviceId").eq(deviceInfo.getPaoIdentifier().getPaoId());
        sql.append("AND PointId").eq(deviceInfo.getPointId());
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateDeviceInfo(List<VoltageLimitedDeviceInfo> deviceInfoList) {
        for(VoltageLimitedDeviceInfo deviceInfo : deviceInfoList) {
            updateDeviceInfo(deviceInfo);
        }
    }
}
