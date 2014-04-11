package com.cannontech.web.tools.mapping.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.web.tools.mapping.dao.LocationDao;
import com.cannontech.web.tools.mapping.model.Location;
import com.google.common.collect.Sets;

public class LocationDaoImpl implements LocationDao {

    @Autowired
    YukonJdbcTemplate template;

    private final static YukonRowMapper<Location> LOCATION_MAPPER = new YukonRowMapper<Location>() {
        @Override
        public Location mapRow(YukonResultSet rs) throws SQLException {
            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PAObjectId", "Type");
            Location location = new Location();
            location.setPaoIdentifier(paoIdentifier);
            location.setTimestamp(rs.getInstant("Timestamp"));
            location.setLatitude(rs.getDouble("Latitude"));
            location.setLongitude(rs.getDouble("Longitude"));
            return location;
        }
    };

    @Override
    public Set<Location> getLastLocations(Collection<? extends YukonPao> paos) {
        Set<Integer> paoIds = Sets.newHashSet();
        for (YukonPao yukonPao : paos) {
            paoIds.add(yukonPao.getPaoIdentifier().getPaoId());
        }
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT PAObjectId,Timestamp,Latitude,Longitude,Type ");
                sql.append("FROM (");
                sql.append(/**/"SELECT l.PAObjectId,Timestamp,Latitude,Longitude,Type,");
                sql.append(/**//**/"ROW_NUMBER() OVER (PARTITION BY l.PAObjectId ORDER BY Timestamp DESC) AS rowNumber ");
                sql.append(/**/"FROM Location l JOIN YukonPAObject p ON l.PAObjectId = p.PAObjectID ");
                sql.append(/**/"WHERE l.PAObjectId").in(subList);
                sql.append(") ROW_NUMBERED ");
                sql.append("WHERE rowNumber").eq(1);
                return sql;
            }
        };
        final Set<Location> lastLocations = Sets.newHashSet();
        ChunkingSqlTemplate chunkingTemplate = new ChunkingSqlTemplate(template);
        chunkingTemplate.query(sqlGenerator, paoIds, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Location location = LOCATION_MAPPER.mapRow(new YukonResultSet(rs));
                lastLocations.add(location);
            }
        });
        return lastLocations;
    }

    @Override
    public Location getLastLocation(int paoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectId,Timestamp,Latitude,Longitude,Type ");
        sql.append("FROM (");
        sql.append(/**/"SELECT l.PAObjectId,Timestamp,Latitude,Longitude,Type,");
        sql.append(/**//**/"ROW_NUMBER() OVER (ORDER BY Timestamp DESC) AS rowNumber ");
        sql.append(/**/"FROM Location l JOIN YukonPAObject p ON l.PAObjectId = p.PAObjectID ");
        sql.append(/**/"WHERE l.PAObjectId").eq(paoId);
        sql.append(") ROW_NUMBERED ");
        sql.append("WHERE rowNumber").eq(1);
        return template.queryForObject(sql, LOCATION_MAPPER);
    }

    @Override
    public Set<Location> getAllLocations(int paoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectId,Timestamp,Latitude,Longitude ");
        sql.append("FROM Location l JOIN YukonPAObject p ON l.PAObjectId = p.PAObjectId ");
        sql.append("WHERE PAObjectId").eq(paoId);
        final Set<Location> locations = Sets.newHashSet();
        template.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Location location = LOCATION_MAPPER.mapRow(new YukonResultSet(rs));
                locations.add(location);
            }
        });
        return locations;
    }

    @Override
    public void save(Location location) {
        // Insert the location or update if already exists.
        SqlStatementBuilder insertSql = new SqlStatementBuilder();
        SqlParameterSink insertParams = insertSql.insertInto("Location");
        insertParams.addValue("PAObjectId", location.getPaoIdentifier().getPaoId());
        insertParams.addValue("Timestamp", location.getTimestamp());
        insertParams.addValue("Latitude", location.getLatitude());
        insertParams.addValue("Longitude", location.getLongitude());
        try {
            template.update(insertSql);
        } catch (DataIntegrityViolationException e) {
            // Device already has a location at this timestamp, update the coordinates.
            SqlStatementBuilder updateSql = new SqlStatementBuilder();
            SqlParameterSink updateParams = updateSql.update("Location");
            updateParams.addValue("PAObjectId", location.getPaoIdentifier().getPaoId());
            updateParams.addValue("Timestamp", location.getTimestamp());
            updateParams.addValue("Latitude", location.getLatitude());
            updateParams.addValue("Longitude", location.getLongitude());
            updateSql.append("WHERE PAObjectId").eq(location.getPaoIdentifier().getPaoId());
            updateSql.append("AND Timestamp").eq(location.getTimestamp());
            template.update(updateSql);
        }
    }

    @Override
    public void saveAll(Collection<Location> locations) {
        /*
         * // If "update if already exists" logic is not required, then SQL batch inserts can be
         * // used instead.
         * SqlStatementBuilder sql = new SqlStatementBuilder();
         * sql.append("INSERT INTO Location (");
         * sql.append("PAObjectId,Timestamp,Latitude,Longitude");
         * sql.append(") VALUES (?,?,?,?) ");
         * List<Object[]> batchArgs = Lists.newArrayList();
         * for (Location location : locations) {
         * batchArgs.add(new Object[] { location.getPaoIdentifier().getPaoId(),
         * location.getTimestamp(),
         * location.getLatitude(), location.getLongitude() });
         * }
         * template.batchUpdate(sql.getSql(), batchArgs);
         */
        for (Location location : locations) {
            save(location);
        }
    }

}