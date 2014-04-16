package com.cannontech.web.tools.mapping.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.web.tools.mapping.dao.LocationDao;
import com.cannontech.web.tools.mapping.model.Location;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class LocationDaoImpl implements LocationDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;

    private final String projection;

    public enum FeaturePropertiesKey {
        PAO_IDENTIFIER("paoIdentifier");

        private final String featurePropertiesKey;

        FeaturePropertiesKey(String featurePropertiesKey) {
            this.featurePropertiesKey = featurePropertiesKey;
        }

        public String getFeaturePropertiesKey() {
            return featurePropertiesKey;
        }

        @Override
        public String toString() {
            return this.getFeaturePropertiesKey();
        }
    }

    private final static YukonRowMapper<Location> locationMapper = new YukonRowMapper<Location>() {
        @Override
        public Location mapRow(YukonResultSet rs) throws SQLException {
            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PAObjectId", "Type");
            Location location = new Location();
            location.setPaoIdentifier(paoIdentifier);
            location.setLatitude(rs.getDouble("Latitude"));
            location.setLongitude(rs.getDouble("Longitude"));
            return location;
        }
    };

    @Autowired
    public LocationDaoImpl(ConfigurationSource configSource) {
        String mapProjection = configSource.getString(MasterConfigStringKeysEnum.MAP_PROJECTION);
        projection = StringUtils.isEmpty(mapProjection) ? "EPSG:4326" : mapProjection;
    }

    @Override
    public Set<Location> getLocations(Iterable<? extends YukonPao> paos) {
        Set<Integer> paoIds = Sets.newHashSet();
        for (YukonPao yukonPao : paos) {
            paoIds.add(yukonPao.getPaoIdentifier().getPaoId());
        }
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT l.PAObjectId,Latitude,Longitude,Type ");
                sql.append("FROM Location l JOIN YukonPAObject p ON l.PAObjectId = p.PAObjectID ");
                sql.append("WHERE l.PAObjectId").in(subList);
                return sql;
            }
        };
        final Set<Location> lastLocations = Sets.newHashSet();
        ChunkingSqlTemplate chunkingTemplate = new ChunkingSqlTemplate(jdbcTemplate);
        chunkingTemplate.query(sqlGenerator, paoIds, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                Location location = locationMapper.mapRow(rs);
                lastLocations.add(location);
            }
        });
        return lastLocations;
    }

    @Override
    public FeatureCollection getLocationsAsGeoJson(Iterable<? extends YukonPao> paos) {
        Set<Location> locations = getLocations(paos);
        FeatureCollection features = new FeatureCollection();
        // Set coordinate reference system for these locations.
        Map<String, Object> crsProperties = Maps.newHashMap();
        crsProperties.put("name", projection);
        Crs crs = new Crs();
        crs.setProperties(crsProperties);
        features.setCrs(crs);
        for (Location location : locations) {
            Feature feature = new Feature();
            // Feature "id" is paoId.
            feature.setId(Integer.toString(location.getPaoIdentifier().getPaoId()));
            Point point = new Point(location.getLongitude(), location.getLatitude());
            feature.setGeometry(point);
            // Set feature properties.
            feature.getProperties().put(FeaturePropertiesKey.PAO_IDENTIFIER.toString(),
                                        location.getPaoIdentifier());
            features.add(feature);
        }
        return features;
    }

    @Override
    public Location getLocation(int paoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT l.PAObjectId,Latitude,Longitude,Type ");
        sql.append("FROM Location l JOIN YukonPAObject p ON l.PAObjectId = p.PAObjectID ");
        sql.append("WHERE l.PAObjectId").eq(paoId);
        return jdbcTemplate.queryForObject(sql, locationMapper);
    }

    @Override
    public void save(Location location) {
        // Insert the location or update if already exists.
        SqlStatementBuilder insertSql = new SqlStatementBuilder();
        SqlParameterSink insertParams = insertSql.insertInto("Location");
        insertParams.addValue("PAObjectId", location.getPaoIdentifier().getPaoId());
        insertParams.addValue("Latitude", location.getLatitude());
        insertParams.addValue("Longitude", location.getLongitude());
        try {
            jdbcTemplate.update(insertSql);
        } catch (DataIntegrityViolationException e) {
            // Device already has a location, update the coordinates.
            SqlStatementBuilder updateSql = new SqlStatementBuilder();
            SqlParameterSink updateParams = updateSql.update("Location");
            updateParams.addValue("PAObjectId", location.getPaoIdentifier().getPaoId());
            updateParams.addValue("Latitude", location.getLatitude());
            updateParams.addValue("Longitude", location.getLongitude());
            updateSql.append("WHERE PAObjectId").eq(location.getPaoIdentifier().getPaoId());
            jdbcTemplate.update(updateSql);
        }
    }

    @Override
    public void saveAll(Iterable<Location> locations) {
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
        for (Location location : locations) {
            save(location);
        }
    }
}