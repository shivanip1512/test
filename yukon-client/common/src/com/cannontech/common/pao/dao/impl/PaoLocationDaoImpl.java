package com.cannontech.common.pao.dao.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.pao.model.PaoLocationDetails;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PaoLocationDaoImpl implements PaoLocationDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private final static YukonRowMapper<PaoLocation> mapper = new YukonRowMapper<PaoLocation>() {
        @Override
        public PaoLocation mapRow(YukonResultSet rs) throws SQLException {
            
            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PAObjectId", "Type");
            double latitude = rs.getDouble("Latitude");
            double longitude = rs.getDouble("Longitude");
            Origin origin = rs.getEnum("Origin", Origin.class);
            Instant lastChangedDate = rs.getInstant("LastChangedDate");

            return new PaoLocation(paoIdentifier, latitude, longitude, origin, lastChangedDate);
        }
    };
    
    private final static YukonRowMapper<PaoLocationDetails> paoLoctionDetailMapper = new YukonRowMapper<PaoLocationDetails>() {
        @Override
        public PaoLocationDetails mapRow(YukonResultSet rs) throws SQLException {
             String paoName = rs.getString("PaoName");
             String meterNumber = rs.getString("MeterNumber");
             String latitude = rs.getString("Latitude");
             String longitude = rs.getString("Longitude");
             Origin origin = rs.getEnum("Origin", Origin.class);
             String lastChangedDate = rs.getString("LastChangedDate");

            return new PaoLocationDetails(paoName, meterNumber, latitude, longitude, origin, lastChangedDate);
        }
    };
    
    @Override
    public Set<PaoLocation> getLocations(Iterable<? extends YukonPao> paos) {
       return getLocations(Sets.newHashSet(PaoUtils.asPaoIdList(paos)));
    }
    
    @Override
    public Set<PaoLocation> getLocations(Set<Integer> paoIds) {
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("select pl.PAObjectId, Latitude, Longitude, Type, Origin, LastChangedDate");
                sql.append("from PaoLocation pl");
                sql.append("join YukonPAObject ypo on ypo.PAObjectId = pl.PAObjectId");
                sql.append("where pl.PAObjectId").in(subList);
                return sql;
            }
        };
        
        final Set<PaoLocation> locations = Sets.newHashSet();
        ChunkingSqlTemplate chunkingTemplate = new ChunkingSqlTemplate(jdbcTemplate);
        chunkingTemplate.query(sqlGenerator, paoIds, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                PaoLocation location = mapper.mapRow(rs);
                locations.add(location);
            }
        });
        
        return locations;
    }
    
    @Override
    public PaoLocation getLocation(int paoId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select pl.PAObjectId, Latitude, Longitude, Type, Origin, LastChangedDate");
        sql.append("from PaoLocation pl");
        sql.append("join YukonPAObject ypo on ypo.PAObjectId = pl.PAObjectId");
        sql.append("where pl.PAObjectId").eq(paoId);
        
        try {
            return jdbcTemplate.queryForObject(sql, mapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public List<PaoLocation> getAllLocations() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select pl.PAObjectId, Latitude, Longitude, Type, Origin, LastChangedDate");
        sql.append("from PaoLocation pl");
        sql.append("join YukonPAObject ypo on ypo.PAObjectId = pl.PAObjectId");
        
        return jdbcTemplate.query(sql, mapper);
    }
    
    @Override
    public void delete(int paoId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE from PaoLocation WHERE PAObjectId").eq(paoId);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void delete(Origin origin) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE from PaoLocation WHERE origin").eq_k(origin);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<PaoLocation> getLocations(Origin origin) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select pl.PAObjectId, Latitude, Longitude, Type, Origin, LastChangedDate");
        sql.append("from PaoLocation pl");
        sql.append("join YukonPAObject ypo on ypo.PAObjectId = pl.PAObjectId");
        sql.append("where origin").eq_k(origin);
        
        return jdbcTemplate.query(sql, mapper);
    }
    
    
    @Override
    public void save(PaoLocation location) {
        // Insert the location or update if already exists.
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink values = sql.insertInto("PaoLocation");
        values.addValue("PAObjectId", location.getPaoIdentifier().getPaoId());
        values.addValue("Latitude", location.getLatitude());
        values.addValue("Longitude", location.getLongitude());
        values.addValue("LastChangedDate", location.getLastChangedDate());
        values.addValue("Origin", location.getOrigin());
        try {
            jdbcTemplate.update(sql);
        } catch (DataIntegrityViolationException e) {
            // Device already has a location, update the coordinates.
            sql = new SqlStatementBuilder();
            values = sql.update("PaoLocation");
            values.addValue("PAObjectId", location.getPaoIdentifier().getPaoId());
            values.addValue("Latitude", location.getLatitude());
            values.addValue("Longitude", location.getLongitude());
            values.addValue("LastChangedDate", location.getLastChangedDate());
            values.addValue("Origin", location.getOrigin());
            sql.append("where PAObjectId").eq(location.getPaoIdentifier().getPaoId());
            jdbcTemplate.update(sql);
        }
    }  
    
    @Override
    public void save(List<PaoLocation> location) {
     
        List<List<PaoLocation>> locations = Lists.partition(location, ChunkingSqlTemplate.DEFAULT_SIZE);
        locations.forEach(batch -> {
            //assign devices
            SqlStatementBuilder insertSql = new SqlStatementBuilder();
            insertSql.append("INSERT INTO PaoLocation");
            insertSql.append("(PAObjectId, Latitude, Longitude, LastChangedDate, Origin)");
            insertSql.append("values");
            insertSql.append("(?, ?, ?, ?, ?)");
            
            jdbcTemplate.batchUpdate(insertSql.toString(), new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    PaoLocation location = batch.get(i);
                    ps.setInt(1, location.getPaoIdentifier().getPaoId());
                    ps.setBigDecimal(2, new BigDecimal(location.getLatitude()));
                    ps.setBigDecimal(3, new BigDecimal(location.getLongitude()));
                    ps.setTimestamp(4, new Timestamp(location.getLastChangedDate().getMillis()));
                    ps.setString(5, location.getOrigin().name());
                }
                @Override
                public int getBatchSize() {
                    return batch.size();
                }
            });
        });
    }

    @Override
    public List<PaoLocationDetails> getPaoLocationDetails(List<Integer> paoIds) {
        final ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.PaoName, dmg.MeterNumber As MeterNumber, loc.PaObjectId, Type, Latitude, Longitude, Origin, LastChangedDate");
                sql.append("FROM YukonPaobject ypo LEFT JOIN DeviceMeterGroup dmg ON ypo.PaObjectId = dmg.DeviceId");
                sql.append("LEFT JOIN PaoLocation loc ON loc.PaObjectId = ypo.PaObjectId");
                sql.append("WHERE ypo.PaObjectId").in(subList);
                return sql;
            }
        };
        return template.query(sqlGenerator, paoIds, paoLoctionDetailMapper);
    }
}