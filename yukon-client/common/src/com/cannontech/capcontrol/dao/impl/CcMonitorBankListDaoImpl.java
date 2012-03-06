package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.enums.Phase;

public class CcMonitorBankListDaoImpl implements CcMonitorBankListDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private StrategyDao strategyDao;
    @Autowired private PointDao pointDao;
    
    private final YukonRowMapper<ZonePointPhaseHolder> regulatorPointRowMapper = new YukonRowMapper<ZonePointPhaseHolder>() {
        @Override
        public ZonePointPhaseHolder mapRow(YukonResultSet rs) throws SQLException {
            int zoneId = rs.getInt("ZoneId");
            int pointId = rs.getInt("PointId");
            Phase phase = rs.getEnum("Phase", Phase.class);
            return new ZonePointPhaseHolder(zoneId, pointId, phase);
        }
    };
    
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
            deviceInfo.setParentPaoIdentifier(paoIdentifier);
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
        sql.append("OR cc.PointId IN (");
        sql.append(  "SELECT PointId");
        sql.append(  "FROM PointToZoneMapping");
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
        sql.append("WHERE DeviceId").eq(deviceInfo.getParentPaoIdentifier().getPaoId());
        sql.append("AND PointId").eq(deviceInfo.getPointId());
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateDeviceInfo(List<VoltageLimitedDeviceInfo> deviceInfoList) {
        for(VoltageLimitedDeviceInfo deviceInfo : deviceInfoList) {
            updateDeviceInfo(deviceInfo);
        }
    }
    
    @Override
    public int addDeviceInfo(VoltageLimitedDeviceInfo info) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("CcMonitorBankList");
        sink.addValue("DeviceId", info.getParentPaoIdentifier().getPaoId());
        sink.addValue("PointId", info.getPointId());
        sink.addValue("DisplayOrder", 1);
        sink.addValue("Scannable", YNBoolean.YES);
        sink.addValue("NINAvg", 3);
        sink.addValue("UpperBandwidth", info.getUpperLimit());
        sink.addValue("LowerBandwidth", info.getLowerLimit());
        sink.addValue("Phase", info.getPhase());
        sink.addValue("OverrideStrategy", YNBoolean.valueOf(info.isOverrideStrategy()));
        
        return yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public int updatePhase(int pointId, Phase phase) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE CcMonitorBankList");
        sql.append("SET Phase").eq(phase);
        sql.append("WHERE PointId").eq(pointId);
        return yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public int removePoints(List<Integer> pointIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CcMonitorBankList");
        sql.append("WHERE PointId").in(pointIds);
        return yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public int addRegulatorPoint(int regulatorId) {
        ZonePointPhaseHolder zonePointPhase = getZonePointPhaseByRegulatorId(regulatorId);
        LimitsHolder limits = getLimitsFromStrategy(zonePointPhase.zoneId);
        
        //paoType here may not be technically correct, but it will never actually be used. Only the id is necessary.
        PaoIdentifier paoId = new PaoIdentifier(regulatorId, PaoType.PHASE_OPERATED);
        VoltageLimitedDeviceInfo info = buildNewInfoObject(paoId, zonePointPhase.pointId, limits, zonePointPhase.phase);
        return addDeviceInfo(info);
    }
    
    @Override
    public int addAdditionalMonitorPoint(int pointId, int zoneId, Phase phase) {
        LimitsHolder limits = getLimitsFromStrategy(zoneId);
        PaoIdentifier paoId = pointDao.getPaoPointIdentifier(pointId).getPaoIdentifier();
        
        VoltageLimitedDeviceInfo info = buildNewInfoObject(paoId, pointId, limits, phase);
        return addDeviceInfo(info);
    }
    
    @Override
    public void updateRegulatorPoint(int regulatorId) {
        ZonePointPhaseHolder zonePointPhase = getZonePointPhaseByRegulatorId(regulatorId);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE CcMonitorBankList");
        sql.append("SET Phase").eq(zonePointPhase.phase);
        sql.append("WHERE PointId").eq(zonePointPhase.pointId);
        sql.append("AND DeviceId").eq(regulatorId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public boolean deleteNonMatchingRegulatorPoint(int regulatorId, int pointIdToMatch) {
        SqlStatementBuilder getRegulatorPointSql = new SqlStatementBuilder();
        getRegulatorPointSql.append("SELECT PointId");
        getRegulatorPointSql.append("FROM RegulatorToZoneMapping");
        getRegulatorPointSql.append("JOIN ExtraPaoPointAssignment eppa ON RegulatorId = eppa.PAObjectId");
        getRegulatorPointSql.append("WHERE RegulatorId").eq(regulatorId);
        getRegulatorPointSql.append("AND Attribute").eq_k(BuiltInAttribute.VOLTAGE_Y);
        
        try {
            //if there is no voltage_y point assigned, this will throw an exception
            int pointId = yukonJdbcTemplate.queryForInt(getRegulatorPointSql);
            
            //check to see if it matches the specified pointId. Only delete if it
            //DOESN'T match.
            if(pointId != pointIdToMatch) {
                SqlStatementBuilder deleteSql = new SqlStatementBuilder();
                deleteSql.append("DELETE FROM CcMonitorBankList");
                deleteSql.append("WHERE DeviceId").eq(regulatorId);
                deleteSql.append("AND PointId").eq(pointId);
                yukonJdbcTemplate.update(deleteSql);
            } else {
                return false;
            }
        } catch(EmptyResultDataAccessException e) {
            return false;
        }
        return true;
    }
    
    @Override
    public void removePointsByZone(int zoneId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CcMonitorBankList");
        sql.append("WHERE PointId IN (");
        sql.append(  "SELECT PointId");
        sql.append(  "FROM RegulatorToZoneMapping rtzm");
        sql.append(  "JOIN ExtraPaoPointAssignment eppa ON rtzm.RegulatorId = eppa.PAObjectId");
        sql.append(  "WHERE ZoneId").eq(zoneId);
        sql.append(")");
        sql.append("OR PointId IN (");
        sql.append(  "SELECT PointId");
        sql.append(  "FROM PointToZoneMapping");
        sql.append(  "WHERE ZoneId").eq(zoneId);
        sql.append(")");
        
        yukonJdbcTemplate.update(sql);
    }
    
    private VoltageLimitedDeviceInfo buildNewInfoObject(PaoIdentifier paoId, int pointId, LimitsHolder limits, Phase phase) {
        VoltageLimitedDeviceInfo info = new VoltageLimitedDeviceInfo();
        info.setParentPaoIdentifier(paoId);
        info.setPointId(pointId);
        info.setUpperLimit(limits.upperLimit);
        info.setLowerLimit(limits.lowerLimit);
        info.setPhase(phase);
        info.setOverrideStrategy(false);
        
        return info;
    }
    
    private ZonePointPhaseHolder getZonePointPhaseByRegulatorId(int regulatorId) {
        SqlStatementBuilder getZoneInfoSql = new SqlStatementBuilder();
        getZoneInfoSql.append("SELECT ZoneId, PointId, Phase");
        getZoneInfoSql.append("FROM RegulatorToZoneMapping");
        getZoneInfoSql.append("JOIN ExtraPaoPointAssignment eppa ON RegulatorId = eppa.PAObjectId");
        getZoneInfoSql.append("WHERE RegulatorId").eq(regulatorId);
        getZoneInfoSql.append("AND Attribute").eq_k(BuiltInAttribute.VOLTAGE_Y);
        
        return yukonJdbcTemplate.queryForObject(getZoneInfoSql, regulatorPointRowMapper);
    }
    
    private LimitsHolder getLimitsFromStrategy(int zoneId) {
        SqlStatementBuilder getStrategyIdSql = new SqlStatementBuilder();
        getStrategyIdSql.append("SELECT StrategyId");
        getStrategyIdSql.append("FROM Zone");
        getStrategyIdSql.append("JOIN CcSeasonStrategyAssignment ccssa ON Zone.SubstationBusId = ccssa.PaObjectId");
        getStrategyIdSql.append("WHERE ZoneId").eq(zoneId);
        int strategyId = yukonJdbcTemplate.queryForInt(getStrategyIdSql);
        
        //get the upper and lower limits from the strategy
        CapControlStrategy strategy = strategyDao.getForId(strategyId);
        List<PeakTargetSetting> settings = strategy.getTargetSettings();
        double upperVoltLimit = 0.0;
        double lowerVoltLimit = 0.0;
        for(PeakTargetSetting setting : settings) {
            if(setting.getType() == TargetSettingType.UPPER_VOLT_LIMIT) {
                upperVoltLimit = Double.parseDouble(setting.getPeakValue());
            } else if(setting.getType() == TargetSettingType.LOWER_VOLT_LIMIT) {
                lowerVoltLimit = Double.parseDouble(setting.getPeakValue());
            }
        }
        return new LimitsHolder(upperVoltLimit, lowerVoltLimit);
    }
    
    private class LimitsHolder {
        public double upperLimit;
        public double lowerLimit;
        public LimitsHolder(double upperLimit, double lowerLimit) {
            this.upperLimit = upperLimit;
            this.lowerLimit = lowerLimit;
        }
    }
    
    private class ZonePointPhaseHolder {
        public int zoneId;
        public int pointId;
        public Phase phase;
        public ZonePointPhaseHolder(int zoneId, int pointId, Phase phase) {
            this.zoneId = zoneId;
            this.pointId = pointId;
            this.phase = phase;
        }
    }
}
