package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.cannontech.common.model.PaoLocation;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoLocationDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.google.common.collect.Sets;

public class PaoLocationDaoImpl implements PaoLocationDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;

    private final static YukonRowMapper<PaoLocation> locationMapper = new YukonRowMapper<PaoLocation>() {
        @Override
        public PaoLocation mapRow(YukonResultSet rs) throws SQLException {
            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PAObjectId", "Type");
            PaoLocation location = new PaoLocation();
            location.setPaoIdentifier(paoIdentifier);
            location.setLatitude(rs.getDouble("Latitude"));
            location.setLongitude(rs.getDouble("Longitude"));
            return location;
        }
    };

    @Override
    public Set<PaoLocation> getLocations(Iterable<? extends YukonPao> paos) {
        Set<Integer> paoIds = Sets.newHashSet();
        for (YukonPao yukonPao : paos) {
            paoIds.add(yukonPao.getPaoIdentifier().getPaoId());
        }
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT l.PAObjectId,Latitude,Longitude,Type ");
                sql.append("FROM PaoLocation l JOIN YukonPAObject p ON l.PAObjectId = p.PAObjectID ");
                sql.append("WHERE l.PAObjectId").in(subList);
                return sql;
            }
        };
        final Set<PaoLocation> lastLocations = Sets.newHashSet();
        ChunkingSqlTemplate chunkingTemplate = new ChunkingSqlTemplate(jdbcTemplate);
        chunkingTemplate.query(sqlGenerator, paoIds, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                PaoLocation location = locationMapper.mapRow(rs);
                lastLocations.add(location);
            }
        });
        return lastLocations;
    }

    @Override
    public PaoLocation getLocation(int paoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT l.PAObjectId,Latitude,Longitude,Type ");
        sql.append("FROM PaoLocation l JOIN YukonPAObject p ON l.PAObjectId = p.PAObjectID ");
        sql.append("WHERE l.PAObjectId").eq(paoId);
        return jdbcTemplate.queryForObject(sql, locationMapper);
    }

    @Override
    public void save(PaoLocation location) {
        // Insert the location or update if already exists.
        SqlStatementBuilder insertSql = new SqlStatementBuilder();
        SqlParameterSink insertParams = insertSql.insertInto("PaoLocation");
        insertParams.addValue("PAObjectId", location.getPaoIdentifier().getPaoId());
        insertParams.addValue("Latitude", location.getLatitude());
        insertParams.addValue("Longitude", location.getLongitude());
        try {
            jdbcTemplate.update(insertSql);
        } catch (DataIntegrityViolationException e) {
            // Device already has a location, update the coordinates.
            SqlStatementBuilder updateSql = new SqlStatementBuilder();
            SqlParameterSink updateParams = updateSql.update("PaoLocation");
            updateParams.addValue("PAObjectId", location.getPaoIdentifier().getPaoId());
            updateParams.addValue("Latitude", location.getLatitude());
            updateParams.addValue("Longitude", location.getLongitude());
            updateSql.append("WHERE PAObjectId").eq(location.getPaoIdentifier().getPaoId());
            jdbcTemplate.update(updateSql);
        }
    }

    @Override
    public void saveAll(Iterable<PaoLocation> locations) {
        /*
         * // If "update if already exists" logic is not required, then SQL batch inserts can be
         * // used instead.
         * SqlStatementBuilder sql = new SqlStatementBuilder();
         * sql.append("INSERT INTO Location (");
         * sql.append("PAObjectId,Latitude,Longitude");
         * sql.append(") VALUES (?,?,?) ");
         * List<Object[]> batchArgs = Lists.newArrayList();
         * for (Location location : locations) {
         * batchArgs.add(new Object[] { location.getPaoIdentifier().getPaoId(),
         * location.getLatitude(), location.getLongitude() });
         * }
         * template.batchUpdate(sql.getSql(), batchArgs);
         */
        for (PaoLocation location : locations) {
            save(location);
        }
    }
}