package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.OrphanedRegulatorException;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.capcontrol.model.CcEvent;
import com.cannontech.capcontrol.model.CcEventSubType;
import com.cannontech.capcontrol.model.CcEventType;
import com.cannontech.capcontrol.model.CymePaoPoint;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.common.model.Phase;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.TimeRange;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.AdvancedFieldMapper;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SimpleTableAccessTemplate.CascadeMode;
import com.cannontech.database.SqlParameterChildSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ZoneDaoImpl implements ZoneDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private CcMonitorBankListDao ccMonitorBankListDao;
    private SimpleTableAccessTemplate<Zone> zoneTemplate;
    private SimpleTableAccessTemplate<RegulatorToZoneMapping> regulatorToZoneTemplate;
    
    private static YukonRowMapper<CapBankPointDelta> pointDeltaRowMapper = new YukonRowMapper<CapBankPointDelta>() {
        @Override
        public CapBankPointDelta mapRow(YukonResultSet rs) throws SQLException {
            CapBankPointDelta capBankPointDelta = new CapBankPointDelta();
            
            capBankPointDelta.setPointId(rs.getInt("PointId"));
            capBankPointDelta.setBankId(rs.getInt("DeviceId"));
            capBankPointDelta.setBankName(rs.getString("BankName"));
            capBankPointDelta.setCbcName(rs.getString("CbcName"));
            capBankPointDelta.setAffectedDeviceName(rs.getString("AffectedDeviceName"));
            capBankPointDelta.setAffectedPointName(rs.getString("AffectedPointName"));
            capBankPointDelta.setPreOpValue(rs.getDouble("PreOpValue"));
            capBankPointDelta.setDelta(rs.getDouble("Delta"));

            String staticDelta = rs.getString("StaticDelta").trim();
            capBankPointDelta.setStaticDelta("Y".equals(staticDelta));
            
            return capBankPointDelta;
        }
    };

    private static YukonRowMapper<Zone> zoneRowMapper = new YukonRowMapper<Zone>() {
        @Override
        public Zone mapRow(YukonResultSet rs) throws SQLException {
            Zone zone = new Zone();
            
            zone.setId(rs.getInt("ZoneId"));
            zone.setName(rs.getString("ZoneName"));
            zone.setSubstationBusId(rs.getInt("SubstationBusId"));
            zone.setGraphStartPosition(rs.getDouble("GraphStartPosition"));
            zone.setParentId(rs.getNullableInt("ParentId"));
            zone.setZoneType(rs.getEnum("ZoneType", ZoneType.class));

            return zone;
        }
    };

    private static YukonRowMapper<RegulatorToZoneMapping> regulatorToZoneRowMapper = new YukonRowMapper<RegulatorToZoneMapping>() {
        @Override
        public RegulatorToZoneMapping mapRow(YukonResultSet rs) throws SQLException {
            RegulatorToZoneMapping regulatorToZone = new RegulatorToZoneMapping();
            
            regulatorToZone.setRegulatorId(rs.getInt("RegulatorId"));
            regulatorToZone.setZoneId(rs.getInt("ZoneId"));
            regulatorToZone.setPhase(rs.getEnum("Phase", Phase.class));
            if (regulatorToZone.getPhase() == null) {
                regulatorToZone.setPhase(Phase.ALL);
            }

            return regulatorToZone;
        }
    };

    private static YukonRowMapper<CapBankToZoneMapping> bankToZoneRowMapper = new YukonRowMapper<CapBankToZoneMapping>() {
        @Override
        public CapBankToZoneMapping mapRow(YukonResultSet rs) throws SQLException {
            CapBankToZoneMapping bankToZone = new CapBankToZoneMapping();
            bankToZone.setDeviceId(rs.getInt("DeviceId"));
            bankToZone.setZoneId(rs.getInt("ZoneId"));
            bankToZone.setGraphPositionOffset(rs.getDouble("GraphPositionOffset"));
            bankToZone.setDistance(rs.getDouble("Distance"));
            return bankToZone;
        }
    };
    
    private static YukonRowMapper<PointToZoneMapping> pointToZoneRowMapper = new YukonRowMapper<PointToZoneMapping>() {
        @Override
        public PointToZoneMapping mapRow(YukonResultSet rs) throws SQLException {
            
            PointToZoneMapping pointToZone = new PointToZoneMapping();
            pointToZone.setPointId(rs.getInt("PointId"));
            pointToZone.setZoneId(rs.getInt("ZoneId"));
            pointToZone.setGraphPositionOffset(rs.getDouble("GraphPositionOffset"));
            pointToZone.setDistance(rs.getDouble("Distance"));
            pointToZone.setPhase(rs.getEnum("Phase", Phase.class));
            pointToZone.setIgnore(rs.getBoolean("Ignore"));
            return pointToZone;
        }
    };
    
    private static YukonRowMapper<CcEvent> ccEventRowMapper = new YukonRowMapper<CcEvent>() {
        @Override
        public CcEvent mapRow(YukonResultSet rs) throws SQLException {

            CcEvent ccEvent = new CcEvent();
            ccEvent.setId(rs.getInt("LogId"));
            ccEvent.setText(rs.getString("Text"));
            ccEvent.setDateTime(rs.getInstant("DateTime"));
            ccEvent.setDeviceName(rs.getString("PaoName"));
            ccEvent.setUserName(rs.getString("UserName"));
            ccEvent.setValue(rs.getInt("Value"));
            return ccEvent;
        }
    };
    
    @Override
    public Zone getZoneById(int zoneId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId, ZoneName, SubstationBusId, ParentId, GraphStartPosition, ZoneType");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE ZoneId").eq(zoneId);
        
        try {
            Zone zone = yukonJdbcTemplate.queryForObject(sqlBuilder, zoneRowMapper);
            List<RegulatorToZoneMapping> regulatorToZoneList = getRegulatorToZoneMappingsByZoneId(zoneId);
            zone.setRegulators(regulatorToZoneList);
            
            return zone;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Zone not found.", e);
        }
    }
    
    @Override
    public List<RegulatorToZoneMapping> getRegulatorToZoneMappingsByZoneId(int zoneId) {
        
        SqlStatementBuilder regulatorToZoneSql = new SqlStatementBuilder();
        regulatorToZoneSql.append("SELECT rtz.RegulatorId, rtz.ZoneId, cc.Phase");
        regulatorToZoneSql.append("FROM RegulatorToZoneMapping rtz");
        regulatorToZoneSql.append("LEFT JOIN CcMonitorBankList cc ON rtz.RegulatorId = cc.DeviceId");
        regulatorToZoneSql.append("WHERE rtz.zoneId").eq(zoneId);
        regulatorToZoneSql.append("ORDER BY cc.Phase");

        List<RegulatorToZoneMapping> regulatorToZoneList = yukonJdbcTemplate.query(regulatorToZoneSql, regulatorToZoneRowMapper);
        
        return regulatorToZoneList;
    }

    @Override
    public List<CapBankToZoneMapping> getBankToZoneMappingByZoneId(int zoneId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DeviceId, ZoneId, GraphPositionOffset, Distance");
        sqlBuilder.append("FROM CapBankToZoneMapping");
        sqlBuilder.append("WHERE ZoneId").eq(zoneId);
        sqlBuilder.append("ORDER BY GraphPositionOffset");
        
        List<CapBankToZoneMapping> capBankToZone = yukonJdbcTemplate.query(sqlBuilder, bankToZoneRowMapper);
        
        return capBankToZone;
    }
    
    @Override
    public Zone getZoneByRegulatorId(int regulatorId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId");
        sqlBuilder.append("FROM RegulatorToZoneMapping");
        sqlBuilder.append("WHERE RegulatorId").eq(regulatorId);
        
        
        try{
            int zoneId = yukonJdbcTemplate.queryForInt(sqlBuilder);
            Zone zone = getZoneById(zoneId);
            
            return zone;
        } catch (EmptyResultDataAccessException e) {
            throw new OrphanedRegulatorException();
        }
    }
    
    @Override
    public List<PointToZoneMapping> getPointToZoneMappingByZoneId(int zoneId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ptz.PointId, ptz.ZoneId, ptz.GraphPositionOffset, ptz.Distance, cc.Phase, ptz.Ignore");
        sqlBuilder.append("FROM PointToZoneMapping ptz");
        sqlBuilder.append("JOIN CcMonitorBankList cc ON ptz.PointId = cc.PointId");
        sqlBuilder.append("WHERE ptz.ZoneId").eq(zoneId);
        sqlBuilder.append("ORDER BY ptz.GraphPositionOffset");
        
        List<PointToZoneMapping> pointToZone = yukonJdbcTemplate.query(sqlBuilder, pointToZoneRowMapper);
        
        return pointToZone;
    }
    
    @Override
    public List<Zone> getZonesBySubBusId(int subBusId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId, ZoneName, SubstationBusId, ParentId, GraphStartPosition, ZoneType");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE SubstationBusId").eq(subBusId);
        
        List<Zone> zones = yukonJdbcTemplate.query(sqlBuilder, zoneRowMapper);

        for (Zone zone : zones) {
            List<RegulatorToZoneMapping> regulatorToZoneList = getRegulatorToZoneMappingsByZoneId(zone.getId());
            zone.setRegulators(regulatorToZoneList);
        }

        return zones;
    }
    
    @Override
    public Zone findParentZoneByBusId(int subBusId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId,ZoneName,SubstationBusId,ParentId,GraphStartPosition, ZoneType");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE SubstationBusId").eq(subBusId);
        sqlBuilder.append("AND ParentId IS NULL");
        
        try {
            Zone zone = yukonJdbcTemplate.queryForObject(sqlBuilder, zoneRowMapper);
            List<RegulatorToZoneMapping> regulatorToZoneList = getRegulatorToZoneMappingsByZoneId(zone.getId());
            zone.setRegulators(regulatorToZoneList);
            
            return zone;
        } catch (EmptyResultDataAccessException e) {
            //Eating the exception and returning null.
            return null;
        }
    }
    
    @Override
    public void save(Zone zone) {
        zoneTemplate.save(zone);
    }
    
    @Override
    public boolean delete(int zoneId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("DELETE FROM Zone");
        sqlBuilder.append("WHERE ZoneId").eq(zoneId);
       
        int rowsAffected = yukonJdbcTemplate.update(sqlBuilder);
        
        return (rowsAffected == 1);
    }
    
    @Override
    public List<Integer> getUnassignedCapBankIdsBySubBusId(int subBusId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DeviceId");
        sqlBuilder.append("FROM CCFeederBankList");
        sqlBuilder.append("WHERE FeederId IN (");
        sqlBuilder.append("  SELECT FeederId");
        sqlBuilder.append("  FROM CCFeederSubAssignment");
        sqlBuilder.append("  WHERE SubstationBusId").eq(subBusId);
        sqlBuilder.append("  )");
        sqlBuilder.append("  AND DeviceId NOT IN (");
        sqlBuilder.append("    SELECT DeviceId");
        sqlBuilder.append("    FROM CapBankToZoneMapping");
        sqlBuilder.append("    WHERE ZoneId IN (");
        sqlBuilder.append("      SELECT ZoneId");
        sqlBuilder.append("      FROM Zone");
        sqlBuilder.append("      WHERE SubstationBusId").eq(subBusId);
        sqlBuilder.append("      )");
        sqlBuilder.append("    )");

        try {
            return yukonJdbcTemplate.query(sqlBuilder, TypeRowMapper.INTEGER);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<Integer>();
        }
    }
    
    @Override
    public List<Integer> getCapBankIdsBySubBusId(int subBusId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DeviceId");
        sqlBuilder.append("FROM CapBankToZoneMapping");
        sqlBuilder.append("WHERE ZoneId IN (");
        sqlBuilder.append("  SELECT ZoneId");
        sqlBuilder.append("  FROM Zone");
        sqlBuilder.append("  WHERE SubstationBusId").eq(subBusId);
        sqlBuilder.append(")");

        return yukonJdbcTemplate.query(sqlBuilder, TypeRowMapper.INTEGER);
    }
    
    @Override
    public List<Integer> getCapBankIdsByZone(int zoneId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DeviceId");
        sqlBuilder.append("FROM CapBankToZoneMapping");
        sqlBuilder.append("WHERE ZoneId").eq(zoneId);

        return yukonJdbcTemplate.query(sqlBuilder, TypeRowMapper.INTEGER);
    }

    @Override
    public List<Integer> getAllUsedPointIds() {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT PointId");
        sqlBuilder.append("FROM PointToZoneMapping");
        sqlBuilder.append("UNION");
        sqlBuilder.append("SELECT PointID");
        sqlBuilder.append("FROM CCMONITORBANKLIST");
        
        return yukonJdbcTemplate.query(sqlBuilder, TypeRowMapper.INTEGER);
    }

    @Override
    public void updateCapBankToZoneMapping(int zoneId, List<CapBankToZoneMapping> banksToZone) {
        
        SqlStatementBuilder sqlBuilderDelete = new SqlStatementBuilder();
        sqlBuilderDelete.append("DELETE FROM CapBankToZoneMapping");
        sqlBuilderDelete.append("Where ZoneId").eq(zoneId);
        
        yukonJdbcTemplate.update(sqlBuilderDelete);
        
        for (CapBankToZoneMapping bankToZone : banksToZone) {
            SqlStatementBuilder sqlBuilderInsert = new SqlStatementBuilder();
            sqlBuilderInsert.append("INSERT INTO CapBankToZoneMapping (DeviceId, ZoneId, GraphPositionOffset, Distance)");
            sqlBuilderInsert.values(bankToZone.getDeviceId(), zoneId, bankToZone.getGraphPositionOffset(), bankToZone.getDistance());
            
            yukonJdbcTemplate.update(sqlBuilderInsert);
        }
    }

    @Override
    public void updatePointToZoneMapping(AbstractZone abstractZone, List<PointToZoneMapping> pointsToZone) {
        //capture the points that were previously assigned to the zone...
        SqlStatementBuilder sqlBuilderGetPoints = new SqlStatementBuilder();
        sqlBuilderGetPoints.append("SELECT PointId");
        sqlBuilderGetPoints.append("FROM PointToZoneMapping");
        sqlBuilderGetPoints.append("WHERE ZoneId").eq(abstractZone.getZoneId());
        List<Integer> oldPointIds = yukonJdbcTemplate.query(sqlBuilderGetPoints, TypeRowMapper.INTEGER);
        //...then delete them all
        SqlStatementBuilder sqlBuilderDelete = new SqlStatementBuilder();
        sqlBuilderDelete.append("DELETE FROM PointToZoneMapping");
        sqlBuilderDelete.append("Where ZoneId").eq(abstractZone.getZoneId());
        yukonJdbcTemplate.update(sqlBuilderDelete);
        
        for (PointToZoneMapping pointToZone : pointsToZone) {
            SqlStatementBuilder sqlBuilderInsert = new SqlStatementBuilder();
            sqlBuilderInsert.append("INSERT INTO PointToZoneMapping (PointId, ZoneId, GraphPositionOffset, Distance, Ignore)");
            sqlBuilderInsert.values(pointToZone.getPointId(), abstractZone.getZoneId(), pointToZone.getGraphPositionOffset(), pointToZone.getDistance(), pointToZone.isIgnore());
            
            yukonJdbcTemplate.update(sqlBuilderInsert);
            
            Integer currentPointId = pointToZone.getPointId();
            Phase currentPhase = pointToZone.getPhase();
            if(oldPointIds.contains(currentPointId)) {
                //if point is in list of points from pointToZoneMapping, we just
                //update CcMonitorBankList w/ new phase (then remove from list)
                ccMonitorBankListDao.updatePhase(currentPointId, currentPhase);
                oldPointIds.remove(currentPointId);
            } else {
                //if point is not in list of points from pointToZoneMapping, we
                //insert new, with strategy settings
                ccMonitorBankListDao.addAdditionalMonitorPoint(currentPointId, abstractZone.getSubstationBusId(), currentPhase);
            }
        }
        
        //if any points from pointToZoneMapping are not being updated, they've
        //been removed. Remove their entries from the table.
        if(!oldPointIds.isEmpty()) {
            ccMonitorBankListDao.removePoints(oldPointIds);
        }
    }
    
    @Override
    public void cleanUpBanksByFeeder(int feederId) {
        //Find parent SubBus and call cleanup by SubBus
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT SubstationBusId ");
        sqlBuilder.append("FROM CCFeederSubAssignment ");
        sqlBuilder.append("WHERE FeederId").eq(feederId);
        
        int subBusId;
        try {
            subBusId = yukonJdbcTemplate.queryForInt(sqlBuilder);
        } catch (EmptyResultDataAccessException e) {
            //Feeder not assigned to a bus. Ignore.
            return;
        }
        
        cleanUpBanksBySubBus(subBusId);
    }
    
    @Override
    public void cleanUpBanksBySubBus(int subBusId) {
        List<Integer> banksToRemove = null;
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DeviceId");
        sqlBuilder.append("FROM CapBankToZoneMapping");
        sqlBuilder.append("WHERE ZoneId IN (");
        sqlBuilder.append("  SELECT ZoneId");
        sqlBuilder.append("  FROM Zone");
        sqlBuilder.append("  WHERE SubstationBusId").eq(subBusId);
        sqlBuilder.append("  )");
        sqlBuilder.append("AND DeviceId NOT IN (");
        sqlBuilder.append("  SELECT DeviceId");
        sqlBuilder.append("  FROM CCFeederBankList");
        sqlBuilder.append("  WHERE FeederId IN (");
        sqlBuilder.append("    SELECT FeederId");
        sqlBuilder.append("    FROM CCFeederSubAssignment");
        sqlBuilder.append("    WHERE SubstationBusId").eq(subBusId);
        sqlBuilder.append("    )");
        sqlBuilder.append("  )");
        
        try {
            banksToRemove = yukonJdbcTemplate.query(sqlBuilder, TypeRowMapper.INTEGER);
        } catch (EmptyResultDataAccessException e) {
            //No Banks to remove.
            return;
        }
        removeBankToZoneMappings(banksToRemove);
    }
    
    @Override
    public void removeBankToZoneMappingByFeederId(int feederId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DeviceId ");
        sqlBuilder.append("FROM CCFeederBankList ");
        sqlBuilder.append("WHERE FeederId").eq(feederId);
        
        List<Integer> banksToRemove;
        try {
            banksToRemove = yukonJdbcTemplate.query(sqlBuilder, TypeRowMapper.INTEGER);
        } catch (EmptyResultDataAccessException e) {
            //No Banks to remove.
            return;
        }
        removeBankToZoneMappings(banksToRemove);
    }
    
    @Override
    public void removeBankToZoneMapping(int bankId) {
        
        List<Integer> bankIds = Lists.newArrayList();
        bankIds.add(bankId);
        removeBankToZoneMappings(bankIds);
    }
        
    @Override
    public List<CcEvent> getLatestCapBankEvents(List<Integer> zoneIds, TimeRange range) {
        Duration hours = Duration.standardHours(range.getHours());
        Instant since = Instant.now().minus(hours);
        List<CcEventSubType> subTypes = Lists.newArrayList(new CcEventSubType[] { CcEventSubType.StandardFlipOperation,
            CcEventSubType.ManualFlipOperation, CcEventSubType.ManualOperation, CcEventSubType.StandardOperation });
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EV.LogId, EV.Text, EV.DateTime, PAO.PaoName, EV.Value, EV.UserName");
        sql.append("FROM CapBankToZoneMapping CTZ");
        sql.append("JOIN YukonPAObject PAO ON CTZ.DeviceId = PAO.PAObjectID");
        sql.append("JOIN POINT PT ON PAO.PAObjectID = PT.PAObjectID");
        sql.append("JOIN CCEventLog EV ON PT.POINTID = EV.PointID");
        sql.append("WHERE CTZ.ZoneId").in(zoneIds);
        sql.append("AND EV.EventType").eq_k(CcEventType.CommandSent);
        sql.append("AND EV.EventSubtype").in(subTypes);
        sql.append("AND EV.DateTime").gte(since);

        List<CcEvent> events = yukonJdbcTemplate.query(sql, ccEventRowMapper);
        return events;
    }
    
    @Override
    public List<CcEvent> getLatestCommStatusEvents(int subBusId, TimeRange range) {
        Duration hours = Duration.standardHours(range.getHours());
        Instant since = Instant.now().minus(hours);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EV.LogId, EV.Text, EV.DateTime, YP.PaoName, EV.Value, EV.UserName");
        sql.append("FROM CcEventLog EV");
        sql.append("JOIN YukonPAObject YP ON EV.SubID = YP.PAObjectID");
        sql.append("WHERE EV.EventType").eq_k(CcEventType.IvvcCommStatus);
        sql.append("AND EV.SubId").eq(subBusId);
        sql.append("AND EV.DateTime").gte(since);

        List<CcEvent> events = yukonJdbcTemplate.query(sql, ccEventRowMapper);
        return events;
    }
    
    private void removeBankToZoneMappings(List<Integer> bankIds) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CapBankToZoneMapping");
        sql.append("Where DeviceId").in(bankIds);
        
        yukonJdbcTemplate.update(sql); 
    }
    
    @Override
    public List<CapBankPointDelta> getAllPointDeltasForBankIds(List<Integer> bankIds) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DMPR.DeviceId, DMPR.PointId, PAO.PAOName AS BankName,");
        sqlBuilder.append("  PAOC.PAOName AS CbcName, PAO2.PAOName AS AffectedDeviceName,");
        sqlBuilder.append("  P.PointName AS AffectedPointName, DMPR.PreOpValue, DMPR.Delta, DMPR.StaticDelta");
        sqlBuilder.append("FROM DynamicCCMonitorPointResponse DMPR");
        sqlBuilder.append("JOIN Point P ON P.PointId = DMPR.PointId");
        sqlBuilder.append("JOIN YukonPAObject PAO ON DMPR.DeviceId = PAO.PAObjectId");
        sqlBuilder.append("JOIN YukonPAObject PAO2 ON P.PAObjectId = PAO2.PAObjectId");
        sqlBuilder.append("JOIN CapBank CB ON CB.DeviceId = DMPR.DeviceId");
        sqlBuilder.append("JOIN YukonPAObject PAOC ON PAOC.PAObjectId = CB.ControlDeviceId");
        sqlBuilder.append("JOIN CCFeederBankList FBL ON FBL.DeviceId = CB.DeviceId");
        sqlBuilder.append("JOIN CCFeederSubAssignment FSA ON FSA.FeederId = FBL.FeederId");
        sqlBuilder.append("WHERE DMPR.DeviceId");
        sqlBuilder.in(bankIds);
        sqlBuilder.append("ORDER BY PAOC.PAOName, PAO2.PAOName");
        
        return yukonJdbcTemplate.query(sqlBuilder, pointDeltaRowMapper);
    }
    
    @Override
    public Double getPreOpForPoint(Integer bankId, Integer pointId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DMPR.PreOpValue");
        sqlBuilder.append("FROM DynamicCCMonitorPointResponse DMPR");
        sqlBuilder.append("WHERE DMPR.DeviceId").eq(bankId);
        sqlBuilder.append("AND DMPR.PointID").eq(pointId);
        
        try {
            return yukonJdbcTemplate.queryForObject(sqlBuilder, TypeRowMapper.DOUBLE);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public Double getDeltaForPoint(Integer bankId, Integer pointId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DMPR.Delta");
        sqlBuilder.append("FROM DynamicCCMonitorPointResponse DMPR");
        sqlBuilder.append("WHERE DMPR.DeviceId").eq(bankId);
        sqlBuilder.append("AND DMPR.PointID").eq(pointId);
        try {
            return yukonJdbcTemplate.queryForObject(sqlBuilder, TypeRowMapper.DOUBLE);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Integer> getMonitorPointsForBank(int deviceId) {

        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT PointID");
        sqlBuilder.append("FROM CCMonitorBankList");
        sqlBuilder.append("WHERE DeviceId").eq(deviceId);
        sqlBuilder.append("ORDER BY DisplayOrder");
        
        List<Integer> points = yukonJdbcTemplate.query(sqlBuilder, TypeRowMapper.INTEGER);
        
        return points;
    }

    @Override
    public Map<Integer, Phase> getMonitorPointsForBankAndPhase(int deviceId) {
        
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT PointID, Phase");
        sqlBuilder.append("FROM CCMonitorBankList");
        sqlBuilder.append("WHERE DeviceId").eq(deviceId);
        sqlBuilder.append("ORDER BY DisplayOrder");

        final Map<Integer, Phase> results = Maps.newHashMap();
        yukonJdbcTemplate.query(sqlBuilder, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int pointId = rs.getInt("PointID");
                Phase phase = rs.getEnum("Phase", Phase.class);
                results.put(pointId, phase);
            }
        });

        return results;
    }
    
    @Override
    public List<CymePaoPoint> getTapPointsBySubBusId(int substationBusId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT P.PointId, YPO.PaoName, YPO.PAObjectId, YPO.Type");
        sql.append("FROM ExtraPaoPointAssignment EPPA");
        sql.append("  JOIN YukonPAObject YPO on YPO.PAObjectID = EPPA.PAObjectID");
        sql.append("  JOIN Point P on P.PointId = EPPA.PointId");
        sql.append("  JOIN RegulatorToZoneMapping RZM on RZM.RegulatorId = EPPA.PAObjectId");
        sql.append("  JOIN Zone Z on RZM.ZoneId = Z.ZoneId");
        sql.append("WHERE Z.SubstationBusId").eq(substationBusId);
        sql.append("  AND EPPA.Attribute").eq_k(RegulatorPointMapping.TAP_POSITION);

        List<CymePaoPoint> results = yukonJdbcTemplate.query(sql, SubstationBusDao.cymePaoPointRowMapper);

        return results;
    }
    
    private AdvancedFieldMapper<Zone> zoneFieldMapper = new AdvancedFieldMapper<Zone>() {
        @Override
        public void extractValues(SqlParameterChildSink p, Zone zone) {
            p.addValue("ZoneName", zone.getName());
            p.addValue("SubstationBusId", zone.getSubstationBusId());
            p.addValue("ParentId", zone.getParentId());
            p.addValue("GraphStartPosition", zone.getGraphStartPosition());
            p.addValue("ZoneType", zone.getZoneType());
            p.addChildren(regulatorToZoneTemplate, zone.getRegulators());
        }
        @Override
        public Number getPrimaryKey(Zone zone) {
            return zone.getId();
        }
        @Override
        public void setPrimaryKey(Zone zone, int value) {
            zone.setId(value);
        }
    };

    private FieldMapper<RegulatorToZoneMapping> regulatorToZoneFieldMapper = new FieldMapper<RegulatorToZoneMapping>() {
        @Override
        public Number getPrimaryKey(RegulatorToZoneMapping regulatorToZone) {
            return regulatorToZone.getRegulatorId();
        }
        @Override
        public void setPrimaryKey(RegulatorToZoneMapping regulatorToZone, int value) {
            regulatorToZone.setRegulatorId(value);
        }
        @Override
        public void extractValues(MapSqlParameterSource parameterHolder, RegulatorToZoneMapping object) {}
    };

    @PostConstruct
    public void init() throws Exception {
        zoneTemplate = new SimpleTableAccessTemplate<Zone>(yukonJdbcTemplate, nextValueHelper);
        zoneTemplate.setTableName("Zone");
        zoneTemplate.setPrimaryKeyField("ZoneId");
        zoneTemplate.setAdvancedFieldMapper(zoneFieldMapper);
        
        regulatorToZoneTemplate = new SimpleTableAccessTemplate<RegulatorToZoneMapping>(yukonJdbcTemplate, nextValueHelper);
        regulatorToZoneTemplate.setTableName("RegulatorToZoneMapping");
        regulatorToZoneTemplate.setPrimaryKeyField("RegulatorId");
        regulatorToZoneTemplate.setParentForeignKeyField("ZoneId", CascadeMode.DELETE_ALL_CHILDREN_BEFORE_UPDATE);
        regulatorToZoneTemplate.setFieldMapper(regulatorToZoneFieldMapper);
    }
    
}