package com.cannontech.cbc.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.cbc.dao.ZoneDao;
import com.cannontech.cbc.model.Zone;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class ZoneDaoImpl implements ZoneDao, InitializingBean {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<Zone> template;
    
    private static final YukonRowMapper<Zone> zoneRowMapper = ZoneDaoImpl.createZoneRowMapper();
    private static final YukonRowMapper<CapBankToZoneMapping> bankToZoneRowMapper = ZoneDaoImpl.createBankToZoneRowMapper();
    private static final YukonRowMapper<PointToZoneMapping> pointToZoneRowMapper = ZoneDaoImpl.createPointToZoneRowMapper();
    
    private static final YukonRowMapper<Zone> createZoneRowMapper() {
    	YukonRowMapper<Zone> rowMapper = new YukonRowMapper<Zone>() {
            public Zone mapRow(YukonResultSet rs) throws SQLException {
                
                Zone zone = new Zone();
                zone.setId(rs.getInt("ZoneId"));
                zone.setName(rs.getString("ZoneName"));
                zone.setRegulatorId(rs.getInt("RegulatorId"));
                zone.setSubstationBusId(rs.getInt("SubstationBusId"));
                zone.setGraphStartPosition(rs.getDouble("GraphStartPosition"));
                
                //This gets set to Zero by the RS call if the value is NULL
                zone.setParentId(rs.getInt("ParentId"));
                //If it was null, set it as such
                if (rs.wasNull()) {
                    zone.setParentId(null);
                }

                return zone;
            }
        };
        return rowMapper;
    }
    
    private static final YukonRowMapper<CapBankToZoneMapping> createBankToZoneRowMapper() {
    	YukonRowMapper<CapBankToZoneMapping> rowMapper = new YukonRowMapper<CapBankToZoneMapping>() {
            public CapBankToZoneMapping mapRow(YukonResultSet rs) throws SQLException {
            	CapBankToZoneMapping bankToZone = new CapBankToZoneMapping();
            	bankToZone.setDeviceId(rs.getInt("DeviceId"));
            	bankToZone.setZoneId(rs.getInt("ZoneId"));
            	bankToZone.setPosition(rs.getDouble("Position"));
            	bankToZone.setDistance(rs.getDouble("Distance"));
                return bankToZone;
            }
        };
        return rowMapper;
    }
    
    private static final YukonRowMapper<PointToZoneMapping> createPointToZoneRowMapper() {
    	YukonRowMapper<PointToZoneMapping> rowMapper = new YukonRowMapper<PointToZoneMapping>() {
            public PointToZoneMapping mapRow(YukonResultSet rs) throws SQLException {
            	PointToZoneMapping pointToZone = new PointToZoneMapping();
            	pointToZone.setPointId(rs.getInt("PointId"));
            	pointToZone.setZoneId(rs.getInt("ZoneId"));
            	pointToZone.setPosition(rs.getDouble("Position"));
            	pointToZone.setDistance(rs.getDouble("Distance"));
                return pointToZone;
            }
        };
        return rowMapper;
    }
    
    @Override
    public Zone getZoneById(int zoneId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId, ZoneName, RegulatorId, SubstationBusId, ParentId, GraphStartPosition");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE ZoneId");
        sqlBuilder.eq(zoneId);
        
        Zone zone = yukonJdbcTemplate.queryForObject(sqlBuilder, zoneRowMapper);
        
        return zone;
    }
    
    @Override
    public List<CapBankToZoneMapping> getBankToZoneMappingById(int zoneId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DeviceId, ZoneId, Position, Distance");
        sqlBuilder.append("FROM CapBankToZoneMapping");
        sqlBuilder.append("WHERE ZoneId");
        sqlBuilder.eq(zoneId);
        sqlBuilder.append("ORDER BY Position");
        
        List<CapBankToZoneMapping> capBankToZone = yukonJdbcTemplate.query(sqlBuilder, bankToZoneRowMapper);
        
        return capBankToZone;
    }
    
    @Override
    public Zone getZoneByRegulatorId(int regulatorId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId, ZoneName, RegulatorId, SubstationBusId, ParentId, GraphStartPosition");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE RegulatorId");
        sqlBuilder.eq(regulatorId);
        
        Zone zone = yukonJdbcTemplate.queryForObject(sqlBuilder, zoneRowMapper);
        
        return zone;
    }
    
    @Override
    public List<PointToZoneMapping> getPointToZoneMappingById(int zoneId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT PointId, ZoneId, Position, Distance");
        sqlBuilder.append("FROM PointToZoneMapping");
        sqlBuilder.append("WHERE ZoneId");
        sqlBuilder.eq(zoneId);
        sqlBuilder.append("ORDER BY Position");
        
        List<PointToZoneMapping> pointToZone = yukonJdbcTemplate.query(sqlBuilder, pointToZoneRowMapper);
        
        return pointToZone;
    }

    @Override
    public List<Zone> getZonesBySubBusId(int subBusId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId, ZoneName, RegulatorId, SubstationBusId, ParentId, GraphStartPosition");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE SubstationBusId");
        sqlBuilder.eq(subBusId);
        
        List<Zone> zones = yukonJdbcTemplate.query(sqlBuilder, zoneRowMapper);
        
        return zones;
    }
    
    @Override
    public Zone getParentZoneByBusId(int subBusId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId,ZoneName,RegulatorId,SubstationBusId,ParentId,GraphStartPosition");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE SubstationBusId");
        sqlBuilder.eq(subBusId);
        sqlBuilder.append("AND ParentId IS NULL");
        
        Zone zone = null;
        
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
        template.save(zone);
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
    public List<Integer> getCapBankIdsByZone(int zoneId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT DeviceId");
        sqlBuilder.append("FROM CapBankToZoneMapping");
        sqlBuilder.append("WHERE ZoneId");
        sqlBuilder.eq(zoneId);
        

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
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("DELETE FROM CapBankToZoneMapping");
        sqlBuilder.append("Where ZoneId").eq(zoneId);
        
        yukonJdbcTemplate.update(sqlBuilder);
        
        for (CapBankToZoneMapping bankToZone : banksToZone) {
            sqlBuilder = new SqlStatementBuilder();
            sqlBuilder.append("INSERT INTO CapBankToZoneMapping (DeviceId, ZoneId, Position, Distance)");
            sqlBuilder.values(bankToZone.getDeviceId(), bankToZone.getZoneId(), bankToZone.getPosition(), bankToZone.getDistance());
            
            yukonJdbcTemplate.update(sqlBuilder);
    	}
    }

    @Override
    public void updatePointToZoneMapping(int zoneId, List<PointToZoneMapping> pointsToZone) {
    	SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
		sqlBuilder.append("DELETE FROM PointToZoneMapping");
        sqlBuilder.append("Where ZoneId").eq(zoneId);
        
        yukonJdbcTemplate.update(sqlBuilder);
        
        for (PointToZoneMapping pointToZone : pointsToZone) {
            sqlBuilder = new SqlStatementBuilder();
            sqlBuilder.append("INSERT INTO PointToZoneMapping (PointId, ZoneId, Position, Distance)");
            sqlBuilder.values(pointToZone.getPointId(), pointToZone.getZoneId(), pointToZone.getPosition(), pointToZone.getDistance());
            
            yukonJdbcTemplate.update(sqlBuilder);
        }
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
        template = new SimpleTableAccessTemplate<Zone>(yukonJdbcTemplate, nextValueHelper);
        template.withTableName("Zone");
        template.withPrimaryKeyField("ZoneId");
        template.withFieldMapper(zoneFieldMapper);
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
