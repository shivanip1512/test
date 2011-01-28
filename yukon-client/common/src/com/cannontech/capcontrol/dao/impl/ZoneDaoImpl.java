package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.OrphanedRegulatorException;
import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.capcontrol.model.CcEvent;
import com.cannontech.capcontrol.model.CcEventType;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

public class ZoneDaoImpl implements ZoneDao, InitializingBean {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<Zone> zoneTemplate;
    
    private static YukonRowMapper<CapBankPointDelta> pointDeltaRowMapper = 
        new YukonRowMapper<CapBankPointDelta>() {
        @Override
        public CapBankPointDelta mapRow(YukonResultSet rs) throws SQLException {
            CapBankPointDelta capBankPointDelta = new CapBankPointDelta();
            
            capBankPointDelta.setPointId(rs.getInt("PointId"));
            capBankPointDelta.setBankId(rs.getInt("BankId"));
            capBankPointDelta.setBankName(rs.getString("BankName"));
            capBankPointDelta.setCbcName(rs.getString("CbcName"));
            capBankPointDelta.setAffectedDeviceName(rs.getString("AffectedDeviceName"));
            capBankPointDelta.setAffectedPointName(rs.getString("AffectedPointName"));
            capBankPointDelta.setPreOpValue(rs.getDouble("PreOpValue"));
            capBankPointDelta.setDelta(rs.getDouble("Delta"));
            
            String staticDelta = rs.getString("StaticDelta").trim().intern();
            capBankPointDelta.setStaticDelta("Y".equals(staticDelta));
            
            return capBankPointDelta;
        }
    };

    private static YukonRowMapper<Zone> zoneRowMapper = 
    	new YukonRowMapper<Zone>() {
		@Override
        public Zone mapRow(YukonResultSet rs) throws SQLException {
            Zone zone = new Zone();
            
            zone.setId(rs.getInt("ZoneId"));
            zone.setName(rs.getString("ZoneName"));
            zone.setRegulatorId(rs.getInt("RegulatorId"));
            zone.setSubstationBusId(rs.getInt("SubstationBusId"));
            zone.setGraphStartPosition(rs.getDouble("GraphStartPosition"));
            zone.setParentId(rs.getNullableInt("ParentId"));

            return zone;
        }
    };
    
    private static YukonRowMapper<CapBankToZoneMapping> bankToZoneRowMapper = 
		new YukonRowMapper<CapBankToZoneMapping>() {
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
    
    private static YukonRowMapper<PointToZoneMapping> pointToZoneRowMapper = 
    	new YukonRowMapper<PointToZoneMapping>() {
        public PointToZoneMapping mapRow(YukonResultSet rs) throws SQLException {
        	PointToZoneMapping pointToZone = new PointToZoneMapping();
        	pointToZone.setPointId(rs.getInt("PointId"));
        	pointToZone.setZoneId(rs.getInt("ZoneId"));
        	pointToZone.setGraphPositionOffset(rs.getDouble("GraphPositionOffset"));
        	pointToZone.setDistance(rs.getDouble("Distance"));
            return pointToZone;
        }
    };
    
    private static YukonRowMapper<CcEvent> ccEventRowMapper = 
        new YukonRowMapper<CcEvent>() {
        @Override
        public CcEvent mapRow(YukonResultSet rs) throws SQLException {
            CcEvent ccEvent = new CcEvent();
            
            ccEvent.setText(rs.getString("Text"));
            ccEvent.setDateTime(rs.getDate("DateTime"));
            
            return ccEvent;
        }
    };
    
    @Override
    public Zone getZoneById(int zoneId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId, ZoneName, RegulatorId, SubstationBusId, ParentId, GraphStartPosition");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE ZoneId").eq(zoneId);
        
        Zone zone = yukonJdbcTemplate.queryForObject(sqlBuilder, zoneRowMapper);
        
        return zone;
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
        sqlBuilder.append("SELECT ZoneId, ZoneName, RegulatorId, SubstationBusId, ParentId, GraphStartPosition");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE RegulatorId").eq(regulatorId);
        
        Zone zone;
        
        try{
            zone = yukonJdbcTemplate.queryForObject(sqlBuilder, zoneRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new OrphanedRegulatorException();
        }
        
        return zone;
    }
    
    @Override
    public List<PointToZoneMapping> getPointToZoneMappingByZoneId(int zoneId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT PointId, ZoneId, GraphPositionOffset, Distance");
        sqlBuilder.append("FROM PointToZoneMapping");
        sqlBuilder.append("WHERE ZoneId").eq(zoneId);
        sqlBuilder.append("ORDER BY GraphPositionOffset");
        
        List<PointToZoneMapping> pointToZone = yukonJdbcTemplate.query(sqlBuilder, pointToZoneRowMapper);
        
        return pointToZone;
    }
    
    @Override
    public List<Zone> getZonesBySubBusId(int subBusId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId, ZoneName, RegulatorId, SubstationBusId, ParentId, GraphStartPosition");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE SubstationBusId").eq(subBusId);
        
        List<Zone> zones = yukonJdbcTemplate.query(sqlBuilder, zoneRowMapper);
        
        return zones;
    }
    
    @Override
    public Zone findParentZoneByBusId(int subBusId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId,ZoneName,RegulatorId,SubstationBusId,ParentId,GraphStartPosition");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE SubstationBusId").eq(subBusId);
        sqlBuilder.append("AND ParentId IS NULL");
        
        Zone zone;
        
        try {
            zone = yukonJdbcTemplate.queryForObject(sqlBuilder, zoneRowMapper);
        } catch (EmptyResultDataAccessException e) {
            //Eating the exception and returning null.
            zone = null;
        }
        return zone;
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
            return yukonJdbcTemplate.query(sqlBuilder, new IntegerRowMapper());
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

        return yukonJdbcTemplate.query(sqlBuilder, new IntegerRowMapper());
    }
    
    @Override
    public List<Integer> getCapBankIdsByZone(int zoneId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DeviceId");
        sqlBuilder.append("FROM CapBankToZoneMapping");
        sqlBuilder.append("WHERE ZoneId").eq(zoneId);

        return yukonJdbcTemplate.query(sqlBuilder, new IntegerRowMapper());
    }

    @Override
    public List<Integer> getPointIdsByZone(int zoneId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT PointId");
        sqlBuilder.append("FROM PointToZoneMapping");
        sqlBuilder.append("WHERE ZoneId").eq(zoneId);
        

        return yukonJdbcTemplate.query(sqlBuilder, new IntegerRowMapper());
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
    public void updatePointToZoneMapping(int zoneId, List<PointToZoneMapping> pointsToZone) {
    	SqlStatementBuilder sqlBuilderDelete = new SqlStatementBuilder();
		sqlBuilderDelete.append("DELETE FROM PointToZoneMapping");
        sqlBuilderDelete.append("Where ZoneId").eq(zoneId);
        
        yukonJdbcTemplate.update(sqlBuilderDelete);
        
        for (PointToZoneMapping pointToZone : pointsToZone) {
        	SqlStatementBuilder sqlBuilderInsert = new SqlStatementBuilder();
        	sqlBuilderInsert.append("INSERT INTO PointToZoneMapping (PointId, ZoneId, GraphPositionOffset, Distance)");
        	sqlBuilderInsert.values(pointToZone.getPointId(), zoneId, pointToZone.getGraphPositionOffset(), pointToZone.getDistance());
            
            yukonJdbcTemplate.update(sqlBuilderInsert);
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
            banksToRemove = yukonJdbcTemplate.query(sqlBuilder, new IntegerRowMapper());
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
            banksToRemove = yukonJdbcTemplate.query(sqlBuilder, new IntegerRowMapper());
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
    public List<CcEvent> getLatestEvents(int zoneId, int subBusId, int rowLimit) {       
        final List<CcEventType> bankEventTypes = Lists.newArrayList( new CcEventType[] {CcEventType.BankStateUpdate, 
                                                      CcEventType.CommandSent, 
                                                      CcEventType.ManualCommand});
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT * FROM ( ");
        sql.append("  SELECT EV.Text,EV.DateTime,ROW_NUMBER() over (ORDER BY EV.DateTime desc) rn");
        sql.append("  FROM CcEventLog EV");
        sql.append("  WHERE EV.LogId IN ( ");
        sql.append("    SELECT LogId");
        sql.append("    FROM CcEventLog");
        sql.append("    WHERE (EventType").eq(CcEventType.IvvcCommStatus);
        sql.append("      AND SubId").eq(subBusId);
        sql.append("    )");
        sql.append("    UNION");
        sql.append("    SELECT LogId");
        sql.append("    FROM CCEventLog EV2");
        sql.append("    JOIN Point P ON EV2.PointId = P.PointId AND EV2.EventType").in(bankEventTypes);
        sql.append("    JOIN YukonPAObject PAO ON P.PAObjectId = PAO.PAObjectId");
        sql.append("    WHERE PAO.PAObjectId IN (");
        sql.append("      SELECT DeviceId");          
        sql.append("      FROM CapBankToZoneMapping");
        sql.append("      WHERE ZoneId").eq(zoneId);
        sql.append("    )");
        sql.append("    UNION");
        sql.append("    SELECT LogId");
        sql.append("    FROM CCEventLog EV3");
        sql.append("    JOIN Zone Z ON EV3.RegulatorId = Z.RegulatorId AND EV3.EventType").eq(CcEventType.IvvcTapOperation);        
        sql.append("    WHERE Z.ZoneId").eq(zoneId);
        sql.append("  )");
        sql.append(") numberedRows");
        sql.append("WHERE numberedRows.rn").lte(rowLimit);
        sql.append("ORDER BY numberedRows.DateTime desc");

        return yukonJdbcTemplate.query(sql,ccEventRowMapper);
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
        
        sqlBuilder.append("SELECT DMPR.BankId, DMPR.PointId, PAO.PAOName AS BankName,");
        sqlBuilder.append("  PAOC.PAOName AS CbcName, PAO2.PAOName AS AffectedDeviceName,");
        sqlBuilder.append("  P.PointName AS AffectedPointName, DMPR.PreOpValue, DMPR.Delta, DMPR.StaticDelta");
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
        sqlBuilder.append("ORDER BY PAOC.PAOName, PAO2.PAOName");
        
        return yukonJdbcTemplate.query(sqlBuilder, pointDeltaRowMapper);
    }

    @Override
    public List<Integer> getMonitorPointsForBank(int bankId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        
        sqlBuilder.append("SELECT PointID");
        sqlBuilder.append("FROM CCMonitorBankList");
        sqlBuilder.append("WHERE BankID").eq(bankId);
        sqlBuilder.append("ORDER BY DisplayOrder");
        
        List<Integer> points = yukonJdbcTemplate.query(sqlBuilder, new IntegerRowMapper());
        return points;
    }
    
    private FieldMapper<Zone> zoneFieldMapper = new FieldMapper<Zone>() {
        public void extractValues(MapSqlParameterSource p, Zone zone) {
            p.addValue("ZoneName", zone.getName());
            p.addValue("RegulatorId", zone.getRegulatorId());
            p.addValue("SubstationBusId", zone.getSubstationBusId());
            p.addValue("ParentId", zone.getParentId());
            p.addValue("GraphStartPosition", zone.getGraphStartPosition());
        }
        public Number getPrimaryKey(Zone zone) {
            return zone.getId();
        }
        public void setPrimaryKey(Zone zone, int value) {
            zone.setId(value);
        }
    };
    
    @Override
    public void afterPropertiesSet() throws Exception {
        zoneTemplate = new SimpleTableAccessTemplate<Zone>(yukonJdbcTemplate, nextValueHelper);
        zoneTemplate.setTableName("Zone");
        zoneTemplate.setPrimaryKeyField("ZoneId");
        zoneTemplate.setFieldMapper(zoneFieldMapper);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
