package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.StrategyLimitsHolder;
import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;
import com.cannontech.common.model.Phase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.Iterables;

public class CcMonitorBankListDaoImpl implements CcMonitorBankListDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private StrategyDao strategyDao;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DbChangeManager dbChangeManager;

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
            if(phase==null){
                phase=Phase.ALL;
            }
            double lowerLimit = rs.getDouble("LowerBandwidth");
            double upperLimit = rs.getDouble("UpperBandwidth");
            boolean overrideStrategy = rs.getBoolean("OverrideStrategy");
            
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

        Collections.sort(deviceInfoList, new Comparator<VoltageLimitedDeviceInfo>(){

            @Override
            public int compare(VoltageLimitedDeviceInfo o1, VoltageLimitedDeviceInfo o2) {
                return o1.getPhase().compareTo(o2.getPhase());
            }
        });

        return deviceInfoList;
    }
    
    @Override
    public void updateDeviceInfo(VoltageLimitedDeviceInfo deviceInfo) {
        /*In The database null value represents a 3 phase system,so while updating the 
         * devices, if it is a 3 phase system need to insert null for a 3 phase system */
        Phase phase=deviceInfo.getPhase();
        if(phase==Phase.ALL){
            phase=null;
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE CcMonitorBankList");
        sql.append("SET Phase").eq(phase);
        sql.append(", LowerBandwidth").eq(deviceInfo.getLowerLimit());
        sql.append(", UpperBandwidth").eq(deviceInfo.getUpperLimit());
        sql.append(", OverrideStrategy").eq(YNBoolean.valueOf(deviceInfo.isOverrideStrategy()));
        sql.append("WHERE DeviceId").eq(deviceInfo.getParentPaoIdentifier().getPaoId());
        sql.append("AND PointId").eq(deviceInfo.getPointId());
        yukonJdbcTemplate.update(sql);

        dbChangeManager.processDbChange(DbChangeType.UPDATE, 
                                        DbChangeCategory.CC_MONITOR_BANK_LIST, 
                                        deviceInfo.getPointId());
    }
    
    @Override
    public void updateDeviceInfo(List<VoltageLimitedDeviceInfo> deviceInfoList) {
        for(VoltageLimitedDeviceInfo deviceInfo : deviceInfoList) {
            updateDeviceInfo(deviceInfo);
        }
    }

    @Override
    public int addDeviceInfo(VoltageLimitedDeviceInfo info) {
        Phase phase=info.getPhase();
        if(phase==Phase.ALL){
            phase=null;
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("CcMonitorBankList");
        sink.addValue("DeviceId", info.getParentPaoIdentifier().getPaoId());
        sink.addValue("PointId", info.getPointId());
        sink.addValue("DisplayOrder", 1);
        sink.addValue("Scannable", YNBoolean.YES);
        sink.addValue("NINAvg", 3);
        sink.addValue("UpperBandwidth", info.getUpperLimit());
        sink.addValue("LowerBandwidth", info.getLowerLimit());
        sink.addValue("Phase", phase);
        sink.addValue("OverrideStrategy", YNBoolean.valueOf(info.isOverrideStrategy()));
        
        return yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public int updatePhase(int pointId, Phase phase) {
        if(phase==Phase.ALL){
            phase=null;
        }
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
                
        StrategyLimitsHolder limits = getLimitsFromStrategyByZoneId(zonePointPhase.zoneId);
        
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
        
        StrategyLimitsHolder limits = getLimitsFromStrategyBySubbusId(substationBusId);
        
        //paoType here may not be technically correct, but it will never actually be used. Only the id is necessary.
        PaoIdentifier paoId = new PaoIdentifier(regulatorId, PaoType.PHASE_OPERATED);
        VoltageLimitedDeviceInfo info = buildNewInfoObject(paoId, pointId, limits, phase);
        return addDeviceInfo(info);
    }
    
    @Override
    public int addAdditionalMonitorPoint(int pointId, int substationBusId, Phase phase) {
        StrategyLimitsHolder limits = getLimitsFromStrategyBySubbusId(substationBusId);
        PaoIdentifier paoId = pointDao.getPaoPointIdentifier(pointId).getPaoIdentifier();
        
        VoltageLimitedDeviceInfo info = buildNewInfoObject(paoId, pointId, limits, phase);
        return addDeviceInfo(info);
    }
    
    @Override
    public void updateRegulatorPhase(int regulatorId, Phase phase) {
        ZonePointPhaseHolder zonePointPhase = getZonePointPhaseByRegulatorId(regulatorId);
        if(zonePointPhase == null) {
            return;
        }
        if(phase==Phase.ALL){
            phase=null;
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE CcMonitorBankList").set("Phase", phase);
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
    public boolean updateRegulatorVoltagePoint(int regulatorId, int newPointId) {
        SqlStatementBuilder getRegulatorPointSql = new SqlStatementBuilder();
        getRegulatorPointSql.append("SELECT cc.PointId");
        getRegulatorPointSql.append("FROM CcMonitorBankList cc");
        getRegulatorPointSql.append("JOIN ExtraPaoPointAssignment eppa ON cc.DeviceId = eppa.PAObjectId");
        getRegulatorPointSql.append("WHERE cc.DeviceId").eq(regulatorId);
        getRegulatorPointSql.append("AND eppa.Attribute").eq_k(RegulatorPointMapping.VOLTAGE_Y);
        
        try {
            //if there is no voltage_y point assigned, this will throw an exception
            int existingPointId = yukonJdbcTemplate.queryForInt(getRegulatorPointSql);
            
            //check to see if it matches the specified pointId. Only delete if it
            //DOESN'T match.
            if(existingPointId != newPointId) {
                updateVoltagePoint(regulatorId, existingPointId, newPointId);
            } else {
                return false;
            }
        } catch(EmptyResultDataAccessException e) {
            // An entry doesn't exist, that's fine. We are treating this
            // like we just deleted something, so we return true (below)
        }
        return true;
    }
    
    private void updateVoltagePoint(int deviceId, Integer existingPointId, int newPointId) {
       
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE CcMonitorBankList");
        sql.set("PointId", newPointId);
        sql.append("WHERE DeviceId").eq(deviceId);
        sql.append("AND PointId").eq(existingPointId);
        yukonJdbcTemplate.update(sql);
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
    
    private VoltageLimitedDeviceInfo buildNewInfoObject(PaoIdentifier paoId, int pointId, StrategyLimitsHolder limits, Phase phase) {
        VoltageLimitedDeviceInfo info = new VoltageLimitedDeviceInfo();
        info.setParentPaoIdentifier(paoId);
        info.setPointId(pointId);
        info.setUpperLimit(limits.getUpperLimit());
        info.setLowerLimit(limits.getLowerLimit());
        info.setPhase(phase);
        info.setOverrideStrategy(false);
        
        return info;
    }
    
    private ZonePointPhaseHolder getZonePointPhaseByRegulatorId(int regulatorId) {
        SqlStatementBuilder getZoneInfoSql = new SqlStatementBuilder();
        getZoneInfoSql.append("SELECT rtz.ZoneId, cc.PointId, cc.Phase");
        getZoneInfoSql.append("FROM RegulatorToZoneMapping rtz");
        getZoneInfoSql.append("JOIN CcMonitorBankList cc ON rtz.RegulatorId = cc.DeviceId");
        getZoneInfoSql.append("JOIN ExtraPaoPointAssignment eppa ON RegulatorId = eppa.PAObjectId");
        getZoneInfoSql.append("WHERE rtz.RegulatorId").eq(regulatorId);
        getZoneInfoSql.append("AND eppa.Attribute").eq_k(RegulatorPointMapping.VOLTAGE_Y);
        
        try {
            return yukonJdbcTemplate.queryForObject(getZoneInfoSql, regulatorPointRowMapper);
        } catch(EmptyResultDataAccessException e) {
            //regulator does not have a Voltage Y point assigned.
            return null;
        }
    }
    
    public Phase getPhaseByRegulatorId(int regulatorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT cc.Phase");
        sql.append("FROM CcMonitorBankList cc");
        sql.append("WHERE DeviceId").eq(regulatorId);
        
        Phase phase = yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<Phase>() {

            @Override
            public Phase mapRow(YukonResultSet rs) throws SQLException {
                Phase phase = rs.getEnum("Phase", Phase.class);
                if (phase == null) phase = Phase.ALL;
                return phase;
            }
        });
        
        return phase;
    }
    
    private StrategyLimitsHolder getLimitsFromStrategyByZoneId(int zoneId) {
        SqlStatementBuilder getStrategyIdSql = new SqlStatementBuilder();
        getStrategyIdSql.append("SELECT StrategyId");
        getStrategyIdSql.append("FROM Zone");
        getStrategyIdSql.append("JOIN CcSeasonStrategyAssignment ccssa ON Zone.SubstationBusId = ccssa.PaObjectId");
        getStrategyIdSql.append("WHERE ZoneId").eq(zoneId);
        int strategyId = yukonJdbcTemplate.queryForInt(getStrategyIdSql);
        
        //get the upper and lower limits from the strategy
        return strategyDao.getStrategyLimitsHolder(strategyId);
    }
    
    private StrategyLimitsHolder getLimitsFromStrategyBySubbusId(int substationBusId) {
        SqlStatementBuilder getStrategyIdSql = new SqlStatementBuilder();
        getStrategyIdSql.append("SELECT StrategyId");
        getStrategyIdSql.append("FROM CcSeasonStrategyAssignment");
        getStrategyIdSql.append("WHERE PAObjectId").eq(substationBusId);
        List<Integer> strategyIds = yukonJdbcTemplate.query(getStrategyIdSql, RowMapper.INTEGER);
        
        if(strategyIds.size() == 1) {
            //subbus strategy found
            return strategyDao.getStrategyLimitsHolder(Iterables.getOnlyElement(strategyIds));
        } else {
            //no subbus strategy, get area strategy
            getStrategyIdSql = new SqlStatementBuilder();
            getStrategyIdSql.append("SELECT StrategyId");
            getStrategyIdSql.append("FROM CcSeasonStrategyAssignment CSSA");
            getStrategyIdSql.append("JOIN CcSubAreaAssignment CSAA ON CSAA.AreaID = CSSA.PAObjectId");
            getStrategyIdSql.append("JOIN CcSubstationSubbusList CSSL ON CSSL.SubStationID = CSAA.SubstationBusID");
            getStrategyIdSql.append("WHERE cssl.SubStationBusID").eq(substationBusId);
            int strategyId = yukonJdbcTemplate.queryForInt(getStrategyIdSql);
            return strategyDao.getStrategyLimitsHolder(strategyId);
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
