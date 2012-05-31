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
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.enums.Phase;
import com.cannontech.enums.RegulatorPointMapping;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class CcMonitorBankListDaoImpl implements CcMonitorBankListDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private StrategyDao strategyDao;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

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
            String paoName = rs.getString("PaoName");
            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PaObjectId", "Type");
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
            
            PaoType paoType = deviceInfo.getParentPaoIdentifier().getPaoType();
            if (paoDefinitionDao.isTagSupported(paoType, PaoTag.VOLTAGE_REGULATOR)) {
                deviceInfo.setRegulator(true);
            }
            
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
    public void updateDeviceInfosFromStrategyId(int strategyId) {
        List<VoltageLimitedDeviceInfo> capBankDeviceInfos = getCapBankDeviceInfosFromStrategyId(strategyId);
        List<VoltageLimitedDeviceInfo> regulatorDeviceInfos = getRegulatorDeviceInfosFromStrategyId(strategyId);
        List<VoltageLimitedDeviceInfo> additionalPointDeviceInfos = getAdditionalPointDeviceInfosFromStrategyId(strategyId);

        List<VoltageLimitedDeviceInfo> allDeviceInfos = Lists.newArrayListWithCapacity(capBankDeviceInfos.size() + regulatorDeviceInfos.size()
                                                                                       + additionalPointDeviceInfos.size());
        allDeviceInfos.addAll(capBankDeviceInfos);
        allDeviceInfos.addAll(regulatorDeviceInfos);
        allDeviceInfos.addAll(additionalPointDeviceInfos);
        
        LimitsHolder strategyLimits = getStrategyLimits(strategyId);
        for (VoltageLimitedDeviceInfo deviceInfo : allDeviceInfos) {
            deviceInfo.setLowerLimit(strategyLimits.lowerLimit);
            deviceInfo.setUpperLimit(strategyLimits.upperLimit);
        }
        
        updateDeviceInfo(allDeviceInfos);
    }
    
    private List<VoltageLimitedDeviceInfo> getCapBankDeviceInfosFromStrategyId(int strategyId) {
        SqlStatementBuilder innerSql = new SqlStatementBuilder();
        innerSql.append("  CASE ");
        innerSql.append("    WHEN (strat3u.StrategyId IS not NULL) THEN strat3u.StrategyId");
        innerSql.append("    WHEN (strat2u.StrategyId IS not NULL) THEN strat2u.StrategyId");
        innerSql.append("    WHEN (strat1u.StrategyId IS not NULL) THEN strat1u.StrategyId");
        innerSql.append("    ELSE -1");
        innerSql.append("  END StrategyId");
        getInitialCcMonitorBankListJoins(innerSql);
        innerSql.append("  JOIN CapBank cb ON cb.deviceid = ccmb.deviceid AND cb.controlDeviceID = p.paobjectid");
        innerSql.append("  JOIN CCFeederBankList fb ON fb.DeviceID = ccmb.deviceid");
        innerSql.append("  JOIN CCFeederSubAssignment fsa ON fsa.FeederID = fb.FeederID");
        innerSql.append("  JOIN CCSubstationSubBusList ssb ON ssb.SubStationBusID = fsa.SubStationBusID");
        innerSql.append("  JOIN CCSubAreaAssignment sa ON sa.SubstationBusID = ssb.SubStationID");
        innerSql.append("  LEFT JOIN CCSeasonStrategyAssignment seasStrat1 ON sa.AreaID = seasStrat1.PAObjectId");
        innerSql.append("  LEFT JOIN CCSeasonStrategyAssignment seasStrat2 ON ssb.SubStationBusID = seasStrat2.PAObjectId");
        innerSql.append("  LEFT JOIN CCSeasonStrategyAssignment seasStrat3 ON fsa.FeederID = seasStrat3.PAObjectId");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat1u ON strat1u.StrategyId = seasStrat1.StrategyId AND strat1u.SettingName = 'Upper Volt Limit' AND strat1u.SettingType = 'PEAK'");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat1l ON strat1l.StrategyId = seasStrat1.StrategyId AND strat1l.SettingName = 'Lower Volt Limit' AND strat1l.SettingType = 'PEAK'");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat2u ON strat2u.StrategyId = seasStrat2.StrategyId AND strat2u.SettingName = 'Upper Volt Limit' AND strat2u.SettingType = 'PEAK'");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat2l ON strat2l.StrategyId = seasStrat2.StrategyId AND strat2l.SettingName = 'Lower Volt Limit' AND strat2l.SettingType = 'PEAK'");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat3u ON strat3u.StrategyId = seasStrat3.StrategyId AND strat3u.SettingName = 'Upper Volt Limit' AND strat3u.SettingType = 'PEAK'");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat3l ON strat3l.StrategyId = seasStrat3.StrategyId AND strat3l.SettingName = 'Lower Volt Limit' AND strat3l.SettingType = 'PEAK'");

        return getDeviceInfosFromStrategyId(innerSql, strategyId);
    }
    
    private List<VoltageLimitedDeviceInfo> getRegulatorDeviceInfosFromStrategyId(int strategyId) {
        SqlStatementBuilder innerSql = new SqlStatementBuilder();
        innerSql.append("  CASE ");
        innerSql.append("    WHEN (strat2u.StrategyId IS not NULL) THEN strat2u.StrategyId");
        innerSql.append("    WHEN (strat1u.StrategyId IS not NULL) THEN strat1u.StrategyId");
        innerSql.append("    ELSE -1");
        innerSql.append("  END StrategyId");
        getInitialCcMonitorBankListJoins(innerSql);
        innerSql.append("  JOIN regulatortozonemapping zm on zm.RegulatorId = ccmb.deviceid");
        innerSql.append("  join Zone z on z.ZoneId = zm.ZoneId");
        innerSql.append("  JOIN CCSubstationSubBusList ssb ON ssb.SubStationBusID = z.SubStationBusID");
        innerSql.append("  JOIN CCSubAreaAssignment sa ON sa.SubstationBusID = ssb.SubStationID");
        innerSql.append("  LEFT JOIN CCSeasonStrategyAssignment seasStrat1 ON sa.AreaID = seasStrat1.PAObjectId");
        innerSql.append("  LEFT JOIN CCSeasonStrategyAssignment seasStrat2 ON ssb.SubStationBusID = seasStrat2.PAObjectId");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat1u ON strat1u.StrategyId = seasStrat1.StrategyId AND strat1u.SettingName = 'Upper Volt Limit' AND strat1u.SettingType = 'PEAK'");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat1l ON strat1l.StrategyId = seasStrat1.StrategyId AND strat1l.SettingName = 'Lower Volt Limit' AND strat1l.SettingType = 'PEAK'");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat2u ON strat2u.StrategyId = seasStrat2.StrategyId AND strat2u.SettingName = 'Upper Volt Limit' AND strat2u.SettingType = 'PEAK'");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat2l ON strat2l.StrategyId = seasStrat2.StrategyId AND strat2l.SettingName = 'Lower Volt Limit' AND strat2l.SettingType = 'PEAK'");

        return getDeviceInfosFromStrategyId(innerSql, strategyId);
    }
    
    private List<VoltageLimitedDeviceInfo> getAdditionalPointDeviceInfosFromStrategyId(int strategyId) {
        SqlStatementBuilder innerSql = new SqlStatementBuilder();
        innerSql.append("  CASE ");
        innerSql.append("    WHEN (strat2u.StrategyId IS not NULL) THEN strat2u.StrategyId");
        innerSql.append("    WHEN (strat1u.StrategyId IS not NULL) THEN strat1u.StrategyId");
        innerSql.append("    ELSE -1");
        innerSql.append("  END StrategyId");
        getInitialCcMonitorBankListJoins(innerSql);
        innerSql.append("  AND p.PAObjectID = ccmb.DeviceId");
        innerSql.append("  JOIN PointToZoneMapping zm on zm.PointId = p.pointid");
        innerSql.append("  join Zone z on z.ZoneId = zm.ZoneId");
        innerSql.append("  JOIN CCSubstationSubBusList ssb ON ssb.SubStationBusID = z.SubStationBusID");
        innerSql.append("  JOIN CCSubAreaAssignment sa ON sa.SubstationBusID = ssb.SubStationID");
        innerSql.append("  LEFT JOIN CCSeasonStrategyAssignment seasStrat1 ON sa.AreaID = seasStrat1.PAObjectId");
        innerSql.append("  LEFT JOIN CCSeasonStrategyAssignment seasStrat2 ON ssb.SubStationBusID = seasStrat2.PAObjectId");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat1u ON strat1u.StrategyId = seasStrat1.StrategyId AND strat1u.SettingName = 'Upper Volt Limit' AND strat1u.SettingType = 'PEAK'");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat1l ON strat1l.StrategyId = seasStrat1.StrategyId AND strat1l.SettingName = 'Lower Volt Limit' AND strat1l.SettingType = 'PEAK'");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat2u ON strat2u.StrategyId = seasStrat2.StrategyId AND strat2u.SettingName = 'Upper Volt Limit' AND strat2u.SettingType = 'PEAK'");
        innerSql.append("  LEFT JOIN CCStrategyTargetSettings strat2l ON strat2l.StrategyId = seasStrat2.StrategyId AND strat2l.SettingName = 'Lower Volt Limit' AND strat2l.SettingType = 'PEAK'");
        
        return getDeviceInfosFromStrategyId(innerSql, strategyId);
    }

    private void getInitialCcMonitorBankListJoins(SqlStatementBuilder innerSql) {
        innerSql.append("  FROM ccmonitorbanklist ccmb");
        innerSql.append("  JOIN YukonPaObject ypo ON ccmb.deviceid = ypo.paObjectId");
        innerSql.append("  JOIN POINT p ON ccmb.PointId = p.PointId");
    }

    private List<VoltageLimitedDeviceInfo> getDeviceInfosFromStrategyId(SqlStatementBuilder inner, int strategyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT innerValues.deviceid as PaObjectId, innerValues.PaoName, innerValues.Type, innerValues.PointId, innerValues.PointName, innerValues.Phase, innerValues.LowerBandwidth, innerValues.UpperBandwidth, innerValues.OverrideStrategy");
        sql.append("FROM");
        sql.append(" (select");
        sql.append("  ccmb.DeviceId,");
        sql.append("  ypo.PaoName,");
        sql.append("  ypo.Type,");
        sql.append("  p.PointId,");
        sql.append("  p.PointName,");
        sql.append("  ccmb.Phase,");
        sql.append("  ccmb.LowerBandwidth,");
        sql.append("  ccmb.UpperBandwidth,");
        sql.append("  ccmb.OverrideStrategy,");
        sql.appendFragment(inner);
        sql.append("  ) innerValues");
        sql.append("where innerValues.strategyid").eq(strategyId);
        sql.append("AND innerValues.OverrideStrategy").eq_k(String.valueOf(CtiUtilities.falseChar));

        return yukonJdbcTemplate.query(sql, deviceInfoRowMapper);
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
    public int removeDevices(List<Integer> deviceIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CcMonitorBankList");
        sql.append("WHERE DeviceId").in(deviceIds);
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
        if(zonePointPhase == null) {
            return 0; //no rows affected
        }
                
        LimitsHolder limits = getLimitsFromStrategyByZoneId(zonePointPhase.zoneId);
        
        //paoType here may not be technically correct, but it will never actually be used. Only the id is necessary.
        PaoIdentifier paoId = new PaoIdentifier(regulatorId, PaoType.PHASE_OPERATED);
        VoltageLimitedDeviceInfo info = buildNewInfoObject(paoId, zonePointPhase.pointId, limits, zonePointPhase.phase);
        return addDeviceInfo(info);
    }
    
    @Override
    public int addRegulatorPoint(int regulatorId, Phase phase, int substationBusId) {
        SqlStatementBuilder getPointIdSql = new SqlStatementBuilder();
        getPointIdSql.append("SELECT PointId");
        getPointIdSql.append("FROM ExtraPaoPointAssignment");
        getPointIdSql.append("WHERE PaObjectId").eq(regulatorId);
        getPointIdSql.append("AND Attribute").eq_k(RegulatorPointMapping.VOLTAGE_Y);
        
        int pointId;
        try {
            pointId = yukonJdbcTemplate.queryForInt(getPointIdSql);
        } catch(EmptyResultDataAccessException e) {
            return 0; //no rows affected
        }
        
        LimitsHolder limits = getLimitsFromStrategyBySubbusId(substationBusId);
        
        //paoType here may not be technically correct, but it will never actually be used. Only the id is necessary.
        PaoIdentifier paoId = new PaoIdentifier(regulatorId, PaoType.PHASE_OPERATED);
        VoltageLimitedDeviceInfo info = buildNewInfoObject(paoId, pointId, limits, phase);
        return addDeviceInfo(info);
    }
    
    @Override
    public int addAdditionalMonitorPoint(int pointId, int substationBusId, Phase phase) {
        LimitsHolder limits = getLimitsFromStrategyBySubbusId(substationBusId);
        PaoIdentifier paoId = pointDao.getPaoPointIdentifier(pointId).getPaoIdentifier();
        
        VoltageLimitedDeviceInfo info = buildNewInfoObject(paoId, pointId, limits, phase);
        return addDeviceInfo(info);
    }
    
    @Override
    public void updateRegulatorPoint(int regulatorId) {
        ZonePointPhaseHolder zonePointPhase = getZonePointPhaseByRegulatorId(regulatorId);
        if(zonePointPhase == null) {
            return;
        }
        
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
        getRegulatorPointSql.append("SELECT cc.PointId");
        getRegulatorPointSql.append("FROM CcMonitorBankList cc");
        getRegulatorPointSql.append("JOIN ExtraPaoPointAssignment eppa ON cc.DeviceId = eppa.PAObjectId");
        getRegulatorPointSql.append("WHERE cc.DeviceId").eq(regulatorId);
        getRegulatorPointSql.append("AND eppa.Attribute").eq_k(RegulatorPointMapping.VOLTAGE_Y);
        
        try {
            //if there is no voltage_y point assigned, this will throw an exception
            int pointId = yukonJdbcTemplate.queryForInt(getRegulatorPointSql);
            
            //check to see if it matches the specified pointId. Only delete if it
            //DOESN'T match.
            if(pointId != pointIdToMatch) {
                removeByDeviceId(regulatorId, pointId);
            } else {
                return false;
            }
        } catch(EmptyResultDataAccessException e) {
            // An entry doesn't exist, that's fine. We are treating this
            // like we just deleted something, so we return true (below)
        }
        return true;
    }
    
    @Override
    public void removeByDeviceId(int deviceId, Integer pointId) {
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE FROM CcMonitorBankList");
        deleteSql.append("WHERE DeviceId").eq(deviceId);
        if (pointId != null) {
            deleteSql.append("AND PointId").eq(pointId);
        }
        yukonJdbcTemplate.update(deleteSql);
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
        getZoneInfoSql.append("AND Attribute").eq_k(RegulatorPointMapping.VOLTAGE_Y);
        
        try {
            return yukonJdbcTemplate.queryForObject(getZoneInfoSql, regulatorPointRowMapper);
        } catch(EmptyResultDataAccessException e) {
            //regulator does not have a Voltage Y point assigned.
            return null;
        }
    }
    
    private LimitsHolder getLimitsFromStrategyByZoneId(int zoneId) {
        SqlStatementBuilder getStrategyIdSql = new SqlStatementBuilder();
        getStrategyIdSql.append("SELECT StrategyId");
        getStrategyIdSql.append("FROM Zone");
        getStrategyIdSql.append("JOIN CcSeasonStrategyAssignment ccssa ON Zone.SubstationBusId = ccssa.PaObjectId");
        getStrategyIdSql.append("WHERE ZoneId").eq(zoneId);
        int strategyId = yukonJdbcTemplate.queryForInt(getStrategyIdSql);
        
        //get the upper and lower limits from the strategy
        return getStrategyLimits(strategyId);
    }
    
    private LimitsHolder getLimitsFromStrategyBySubbusId(int substationBusId) {
        SqlStatementBuilder getStrategyIdSql = new SqlStatementBuilder();
        getStrategyIdSql.append("SELECT StrategyId");
        getStrategyIdSql.append("FROM CcSeasonStrategyAssignment");
        getStrategyIdSql.append("WHERE PAObjectId").eq(substationBusId);
        List<Integer> strategyIds = yukonJdbcTemplate.query(getStrategyIdSql, new IntegerRowMapper());
        
        if(strategyIds.size() == 1) {
            //subbus strategy found
            return getStrategyLimits(Iterables.getOnlyElement(strategyIds));
        } else {
            //no subbus strategy, get area strategy
            getStrategyIdSql = new SqlStatementBuilder();
            getStrategyIdSql.append("SELECT StrategyId");
            getStrategyIdSql.append("FROM CcSeasonStrategyAssignment CSSA");
            getStrategyIdSql.append("JOIN CcSubAreaAssignment CSAA ON CSAA.AreaID = CSSA.PAObjectId");
            getStrategyIdSql.append("JOIN CcSubstationSubbusList CSSL ON CSSL.SubStationID = CSAA.SubstationBusID");
            getStrategyIdSql.append("WHERE cssl.SubStationBusID").eq(substationBusId);
            int strategyId = yukonJdbcTemplate.queryForInt(getStrategyIdSql);
            return getStrategyLimits(strategyId);
        }
    }
    
    private LimitsHolder getStrategyLimits(int strategyId) {
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
