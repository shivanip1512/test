package com.cannontech.dr.assetavailability.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.dr.assetavailability.AllRelayCommunicationTimes;
import com.cannontech.dr.assetavailability.DeviceCommunicationTimes;
import com.cannontech.dr.assetavailability.DeviceRelayCommunicationTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DynamicLcrCommunicationsDaoImpl implements DynamicLcrCommunicationsDao {
    private static final Logger log = YukonLogManager.getLogger(DynamicLcrCommunicationsDaoImpl.class);
    private static final Function<Integer, Integer> integerIdentity = Functions.identity();
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private ChunkingMappedSqlTemplate chunkingMappedSqlTemplate;
    private static final ImmutableMap<Integer, String> RELAY_COLUMNS =
            ImmutableMap.of(
                1, "Relay1Runtime",
                2, "Relay2Runtime",
                3, "Relay3Runtime",
                4, "Relay4Runtime");
    
    @PostConstruct
    public void init() {
        chunkingMappedSqlTemplate = new ChunkingMappedSqlTemplate(jdbcTemplate);
    }
    
    @Override
    public Map<Integer, DeviceCommunicationTimes> findTimes(Collection<Integer> deviceIds) {
        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT DeviceId, LastCommunication, LastNonZeroRuntime");
                sql.append("FROM DynamicLcrCommunications");
                sql.append("WHERE DeviceId").in(subList);
                return sql;
            }
        };
        
        YukonRowMapper<Map.Entry<Integer, DeviceCommunicationTimes>> rowMapper = 
                new YukonRowMapper<Map.Entry<Integer, DeviceCommunicationTimes>>() {
            @Override
            public Map.Entry<Integer, DeviceCommunicationTimes> mapRow(YukonResultSet rs) throws SQLException{
                Integer deviceId = rs.getInt("DeviceId");
                Instant lastCommunication = rs.getInstant("LastCommunication");
                Instant lastNonZeroRuntime = rs.getInstant("LastNonZeroRuntime");
                DeviceCommunicationTimes times = new DeviceCommunicationTimes(lastCommunication, lastNonZeroRuntime);
                return Maps.immutableEntry(deviceId, times);
            }
        };
        
        Map<Integer, DeviceCommunicationTimes> results = 
                chunkingMappedSqlTemplate.mappedQuery(sqlFragmentGenerator, deviceIds, rowMapper, integerIdentity);
        
        if(results.size() < deviceIds.size()) {
            Set<Integer> missingIds = Sets.difference(new HashSet<>(deviceIds), results.keySet());
            for(Integer deviceId : missingIds) {
                results.put(deviceId, null);
            }
        }
        
        return results;
    }
    
    @Override
    public Map<Integer, AllRelayCommunicationTimes> findAllRelayCommunicationTimes(Collection<Integer> deviceIds) {
        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT DeviceId, LastCommunication, LastNonZeroRuntime, Relay1Runtime, Relay2Runtime, Relay3Runtime, Relay4Runtime");
                sql.append("FROM DynamicLcrCommunications");
                sql.append("WHERE DeviceId").in(subList);
                return sql;
            }
        };
        
        YukonRowMapper<Map.Entry<Integer, AllRelayCommunicationTimes>> rowMapper = 
                new YukonRowMapper<Map.Entry<Integer, AllRelayCommunicationTimes>>() {
            @Override
            public Map.Entry<Integer, AllRelayCommunicationTimes> mapRow(YukonResultSet rs) throws SQLException{
                Integer deviceId = rs.getInt("DeviceId");
                Instant lastCommunication = rs.getInstant("LastCommunication");
                Instant lastNonZeroRuntime = rs.getInstant("LastNonZeroRuntime");
                Instant relay1Runtime = rs.getInstant("Relay1Runtime");
                Instant relay2Runtime = rs.getInstant("Relay2Runtime");
                Instant relay3Runtime = rs.getInstant("Relay3Runtime");
                Instant relay4Runtime = rs.getInstant("Relay4Runtime");
                
                AllRelayCommunicationTimes times = new AllRelayCommunicationTimes(lastCommunication, lastNonZeroRuntime,
                        relay1Runtime, relay2Runtime, relay3Runtime, relay4Runtime);
                return Maps.immutableEntry(deviceId, times);
            }
        };
        
        Map<Integer, AllRelayCommunicationTimes> results = 
                chunkingMappedSqlTemplate.mappedQuery(sqlFragmentGenerator, deviceIds, rowMapper, integerIdentity);
        
        if(results.size() < deviceIds.size()) {
            Set<Integer> missingIds = Sets.difference(new HashSet<>(deviceIds), results.keySet());
            for(Integer deviceId : missingIds) {
                results.put(deviceId, null);
            }
        }
        
        return results;
    }
    
    @Override
    @Transactional
    public boolean updateComms(PaoIdentifier paoIdentifier, Instant newTimestamp) {
        Instant lastCommunication = null;
        lastCommunication = findLastCommunicationTime(paoIdentifier.getPaoId());
        if(lastCommunication == null) {
            //No row exists - create a new one with this communication time
            return insertCommunicationTime(paoIdentifier.getPaoId(), newTimestamp);
        } else if (lastCommunication.isBefore(newTimestamp)) {
            //New point data is newer than db value. Update.
            return updateCommunicationTime(paoIdentifier.getPaoId(), newTimestamp);
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean updateRuntimeAndComms(PaoIdentifier paoIdentifier, final int relay, Instant newTimestamp) {
        validateRelay(relay);
        
        //Read current values
        DeviceRelayCommunicationTimes oldTimes = findDeviceRelayCommunicationTimes(paoIdentifier.getPaoId(), relay);
        if (oldTimes == null) {
            //No row exists - create a new one with these values
            return insertRelayRuntimeAndComms(paoIdentifier.getPaoId(), relay, newTimestamp);
        }
        
        //Row exists - determine what to update
        LcrCommunicationsComparison comparison = compareNewTimestamp(oldTimes, relay, newTimestamp);
        
        //update values
        if (comparison.isNeedsUpdate()) {
            Map<String, Object> setValues = buildSetValuesForUpdate(comparison, relay, newTimestamp);
            return updateRelayRuntimeAndComms(paoIdentifier.getPaoId(), setValues);
        }
        return false;
    }
    
    /**
     * Retrieves the last communication time for the specified device. If the device has never communicated, null is
     * returned.
     */
    private Instant findLastCommunicationTime(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LastCommunication");
        sql.append("FROM DynamicLcrCommunications");
        sql.append("WHERE DeviceId").eq(deviceId);
        List<Instant> list = jdbcTemplate.query(sql, RowMapper.INSTANT_NULLABLE);
        if(list.size() == 0) {
            return null;
        }
        return Iterables.getOnlyElement(list); //should only ever be one entry per deviceId
    }
    
    /**
     * Retrieves the last communication time, last runtime, and last relay runtime for the specified device.
     * DeviceRelayCommunicationTimes will be null for any device that has never communicated.
     */
    private DeviceRelayCommunicationTimes findDeviceRelayCommunicationTimes(int deviceId, final int relay) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LastCommunication, LastNonZeroRuntime,").append(RELAY_COLUMNS.get(relay));
        sql.append("FROM DynamicLcrCommunications");
        sql.append("WHERE DeviceId").eq(deviceId);
        DeviceRelayCommunicationTimes oldTimes = null;
        try {
            oldTimes = jdbcTemplate.queryForObject(sql, new YukonRowMapper<DeviceRelayCommunicationTimes>() {
                @Override
                public DeviceRelayCommunicationTimes mapRow(YukonResultSet rs) throws SQLException {
                    Instant lastCommunication = rs.getInstant("LastCommunication");
                    Instant lastNonZeroRuntime = rs.getInstant("LastNonZeroRuntime");
                    Instant relayLastNonZeroRuntime = rs.getInstant(RELAY_COLUMNS.get(relay));
                    return new DeviceRelayCommunicationTimes(lastCommunication, lastNonZeroRuntime, relay, relayLastNonZeroRuntime);
                }
            });
        } catch (EmptyResultDataAccessException e) {
            //no entry, return null
        }
        return oldTimes;
    }
    
    /**
     * Attempts to insert a new row for the device, with only the last communication time specified.
     * @return True if the operation succeeded.
     */
    private boolean insertCommunicationTime(int deviceId, Instant lastCommunicationTimestamp) {
        log.trace("Inserting new row in DynamicLcrCommunications for device " + deviceId + ", timestamp: " 
                  + lastCommunicationTimestamp);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("DynamicLcrCommunications");
        sink.addValue("DeviceId", deviceId);
        sink.addValue("LastCommunication", lastCommunicationTimestamp);
        //all other values are null
        int rowsAffected = jdbcTemplate.update(sql);
        if(rowsAffected == 0) {
            log.error("Failed to insert row in LcrCommunications for device" + deviceId + ", timestamp: " 
                      + lastCommunicationTimestamp);
        }
        return rowsAffected > 0;
    }
    
    /**
     * Attempts to insert a new row for the device, with last communication, runtime and relay runtime specified.
     * @return True if the operation succeeded.
     */
    private boolean insertRelayRuntimeAndComms(int deviceId, int relay, Instant newTimestamp) {
        log.trace("Inserting new row in DynamicLcrCommunications for device " + deviceId + ", timestamp: " 
                  + newTimestamp + ", relay: " + relay);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("DynamicLcrCommunications");
        sink.addValue("DeviceId", deviceId);
        sink.addValue("LastCommunication", newTimestamp);
        sink.addValue("LastNonZeroRuntime", newTimestamp);
        sink.addValue(RELAY_COLUMNS.get(relay), newTimestamp);
        //other runtime values are null
        int rowsAffected = jdbcTemplate.update(sql);
        if(rowsAffected == 0) {
            log.error("Failed to insert row in DynamicLcrCommunications for device" + deviceId + ", timestamp: " 
                      + newTimestamp + ", relay: " + relay);
        }
        return rowsAffected > 0;
    }
    
    /**
     * Attempts to update an existing row for the device with the last communication time specified.
     * If no entry exists for this device in the table, the update will fail. This method does not check that the
     * specified timestamp is newer than the current last communication time for the device - this should be checked
     * beforehand.
     * @return True if the operation succeeded.
     */
    private boolean updateCommunicationTime(int deviceId, Instant lastCommunicationTimestamp) {
        log.trace("Updating row in DynamicLcrCommunications for device " + deviceId + ", timestamp: "
                  + lastCommunicationTimestamp);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE DynamicLcrCommunications");
        sql.set("LastCommunication", lastCommunicationTimestamp); 
        sql.append("WHERE DeviceId").eq(deviceId);
        int rowsAffected = jdbcTemplate.update(sql);
        if (rowsAffected == 0) {
            log.error("Failed to update row in DynamicLcrCommunications for device " + deviceId + ", timestamp: "
                    + lastCommunicationTimestamp);
        }
        return rowsAffected > 0;
    }
    
    /**
     * Attempts to update an existing row for the device with the last communication time, runime, or relay runtime
     * values specified. If no entry exists for this device in the table, the update will fail. This method does not 
     * check that the specified timestamp is newer than the current last communication time for the device - this 
     * should be checked beforehand.
     * @return True if the operation succeeded.
     */
    public boolean updateRelayRuntimeAndComms(int deviceId, Map<String, Object> setValues) {
        log.trace("Updating row in DynamicLcrCommunications for device " + deviceId);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE DynamicLcrCommunications");
        sql.set(setValues);
        sql.append("WHERE DeviceId").eq(deviceId);
        int rowsAffected = jdbcTemplate.update(sql);
        if (rowsAffected == 0) {
            log.error("Failed to update row in DynamicLcrCommunications for device " + deviceId);
        }
        return rowsAffected > 0;
    }
    
    /**
     * Compares DeviceRelayCommunicationTimes to a new timestamp and returns a result object that shows whether the
     * last communication time, the last runtime, or the last relay runtime for the specified relay should be updated
     * to the new timestamp.
     */
    private LcrCommunicationsComparison compareNewTimestamp(DeviceRelayCommunicationTimes oldTimes, int relay,
                                                            Instant newTimestamp) {
        Instant oldCommunicationTime = oldTimes.getLastCommunicationTime();
        boolean isCommNewer = (oldCommunicationTime == null || oldCommunicationTime.isBefore(newTimestamp));
        Instant oldRuntime = oldTimes.getLastNonZeroRuntime();
        boolean isRuntimeNewer = (oldRuntime == null || oldRuntime.isBefore(newTimestamp));
        Instant oldRelayRuntime = oldTimes.getRelayLastNonZeroRuntime();
        boolean isRelayRuntimeNewer = (oldRelayRuntime == null || oldRelayRuntime.isBefore(newTimestamp));
        return new LcrCommunicationsComparison(isCommNewer, isRuntimeNewer, isRelayRuntimeNewer);
    }
    
    /**
     * Builds a map of column name/value pairs to be inserted for an update.
     */
    private Map<String, Object> buildSetValuesForUpdate(LcrCommunicationsComparison comparison, int relay, Instant newTimestamp) {
        Map<String, Object> setValues = new HashMap<>();
        if (comparison.isCommNewer) {
            setValues.put("LastCommunication", newTimestamp);
        }
        if (comparison.isRuntimeNewer) {
            setValues.put("LastNonZeroRuntime", newTimestamp);
        }
        if (comparison.isRelayRuntimeNewer) {
            setValues.put(RELAY_COLUMNS.get(relay), newTimestamp);
        }
        return setValues;
    }
    
    /**
     * @throws IllegalArgumentException if the specified relay is not supported.
     */
    private void validateRelay(int relay) {
        if (!RELAY_COLUMNS.containsKey(relay)) {
            throw new IllegalArgumentException("Relay " + relay + "not supported.");
        }
    }
    
    /**
     * A simple data object representing the result of a comparison between DeviceRelayCommunicationTimes from the
     * database, and a new timestamp.
     */
    private static final class LcrCommunicationsComparison {
        private final boolean isCommNewer;
        private final boolean isRuntimeNewer;
        private final boolean isRelayRuntimeNewer;
        
        public LcrCommunicationsComparison(boolean isCommNewer, boolean isRuntimeNewer, boolean isRelayRuntimeNewer) {
            this.isCommNewer = isCommNewer;
            this.isRuntimeNewer = isRuntimeNewer;
            this.isRelayRuntimeNewer = isRelayRuntimeNewer;
        }
        
        public boolean isNeedsUpdate() {
            return isCommNewer || isRuntimeNewer || isRelayRuntimeNewer;
        }
    }
}
