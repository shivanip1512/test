package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.cbc.dao.ZoneDao;
import com.cannontech.cbc.model.Zone;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class ZoneDaoImpl implements ZoneDao, InitializingBean {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<Zone> template;
    
    private static final ParameterizedRowMapper<Zone> rowMapper = ZoneDaoImpl.createRowMapper();
    
    private static final ParameterizedRowMapper<Zone> createRowMapper() {
        ParameterizedRowMapper<Zone> rowMapper = new ParameterizedRowMapper<Zone>() {
            public Zone mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                Zone zone = new Zone();
                zone.setId(rs.getInt("ZoneId"));
                zone.setName(rs.getString("ZoneName"));
                zone.setRegulatorId(rs.getInt("RegulatorId"));
                zone.setSubstationBusId(rs.getInt("SubstationBusId"));
                
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
    
    @Override
    public Zone getZoneById(int zoneId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId,ZoneName,RegulatorId,SubstationBusId,ParentId");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE ZoneId");
        sqlBuilder.eq(zoneId);
        
        Zone zone = yukonJdbcTemplate.queryForObject(sqlBuilder, rowMapper);
        
        return zone;
    }

    @Override
    public List<Zone> getZonesBySubBusId(int subBusId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ZoneId,ZoneName,RegulatorId,SubstationBusId,ParentId");
        sqlBuilder.append("FROM Zone");
        sqlBuilder.append("WHERE SubstationBusId");
        sqlBuilder.eq(subBusId);
        
        List<Zone> zones = yukonJdbcTemplate.query(sqlBuilder, rowMapper);
        
        return zones;
    }

    @Override
    public void add(Zone zone) {
        template.insert(zone);
    }

    @Override
    public void update(Zone zone) {
        template.update(zone);
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
        

        return yukonJdbcTemplate.query(sqlBuilder, 
            new ParameterizedRowMapper<Integer>() {
                public Integer mapRow(ResultSet rs, int num) throws SQLException{
                    Integer i = new Integer ( rs.getInt("DeviceId") );
                    return i;
                }
            }
        );
    }

    @Override
    public List<Integer> getPointIdsByZone(int zoneId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT PointId");
        sqlBuilder.append("FROM PointToZoneMapping");
        sqlBuilder.append("WHERE ZoneId").eq(zoneId);
        

        return yukonJdbcTemplate.query(sqlBuilder, 
            new ParameterizedRowMapper<Integer>() {
                public Integer mapRow(ResultSet rs, int num) throws SQLException{
                    Integer i = new Integer ( rs.getInt("PointId") );
                    return i;
                }
            }
        );
    }

    @Override
    public void updateCapBankAssignments(int zoneId, List<Integer> bankIds) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("DELETE FROM CapBankToZoneMapping");
        sqlBuilder.append("Where ZoneId").eq(zoneId);
        
        yukonJdbcTemplate.update(sqlBuilder);
        
        for (Integer deviceId : bankIds) {
            sqlBuilder = new SqlStatementBuilder();
            sqlBuilder.append("INSERT INTO CapBankToZoneMapping (DeviceId,ZoneId)");
            sqlBuilder.values(deviceId, zoneId);
            
            yukonJdbcTemplate.update(sqlBuilder);
        }
    }

    @Override
    public void updatePointAssignments(int zoneId, List<Integer> pointids) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("DELETE FROM PointToZoneMapping");
        sqlBuilder.append("Where ZoneId").eq(zoneId);
        
        yukonJdbcTemplate.update(sqlBuilder);
        
        for (Integer pointId : pointids) {
            sqlBuilder = new SqlStatementBuilder();
            sqlBuilder.append("INSERT INTO PointToZoneMapping (PointId,ZoneId)");
            sqlBuilder.values(pointId, zoneId);
            
            yukonJdbcTemplate.update(sqlBuilder);
        }
    }
    
    private FieldMapper<Zone> zoneFieldMapper = new FieldMapper<Zone>() {
        public void extractValues(MapSqlParameterSource p, Zone zone) {
            p.addValue("ZoneName", zone.getName());
            p.addValue("RegulatorId", zone.getRegulatorId());
            p.addValue("SubstationBusId", zone.getSubstationBusId());
            p.addValue("ParentId", zone.getParentId());
            
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
