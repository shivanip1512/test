package com.cannontech.common.pao.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
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
    
    @Override
    public Set<PaoLocation> getLocations(Iterable<? extends YukonPao> paos) {
        
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
        chunkingTemplate.query(sqlGenerator, PaoUtils.asPaoIdList(paos), new YukonRowCallbackHandler() {
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
}