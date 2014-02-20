package com.cannontech.dr.assetavailability.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.dr.assetavailability.AllRelayCommunicationTimes;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.DeviceCommunicationTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DynamicLcrCommunicationsDaoImpl implements DynamicLcrCommunicationsDao {
    private static final Logger log = YukonLogManager.getLogger(DynamicLcrCommunicationsDaoImpl.class);
    private static final Function<Integer, Integer> integerIdentity = Functions.identity();
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private ChunkingMappedSqlTemplate chunkingMappedSqlTemplate;
    
    @PostConstruct
    public void init() {
        chunkingMappedSqlTemplate = new ChunkingMappedSqlTemplate(jdbcTemplate);
    }
    
    @Override
    public Map<Integer, DeviceCommunicationTimes> findTimes(Collection<Integer> deviceIds) {
        log.debug("FindTimes begin.");
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
        log.debug("FindTimes end.");
        return results;
    }
    
    @Override
    public Map<Integer, AllRelayCommunicationTimes> findAllRelayCommunicationTimes(Collection<Integer> deviceIds) {
        log.debug("FindAllRelayCommunicationTimes begin.");
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
        
        log.debug("FindAllRelayCommunicationTimes end.");
        return results;
    }
    
    @Override
    @Transactional
    public void insertData(AssetAvailabilityPointDataTimes times) {
        insertIfAbsent(times.getDeviceId());
        update(times);
    }
    
    private boolean insertIfAbsent(int deviceId) {
        SqlStatementBuilder selectSql = new SqlStatementBuilder();
        selectSql.append("SELECT COUNT(*)");
        selectSql.append("FROM DynamicLcrCommunications");
        selectSql.append("WHERE DeviceId").eq(deviceId);
        int rowsPresent = jdbcTemplate.queryForInt(selectSql);
        
        if(rowsPresent == 0) {
            //No entry for this device yet. Insert one now.
            SqlStatementBuilder insertSql = new SqlStatementBuilder();
            SqlParameterSink sink = insertSql.insertInto("DynamicLcrCommunications");
            sink.addValue("DeviceId", deviceId);
            
            int rowsAffected = jdbcTemplate.update(insertSql);
            return rowsAffected > 0;
        }
        return true;
    }
    
    private boolean update(AssetAvailabilityPointDataTimes times) {
        Map<Integer, Instant> relayRuntimes = times.getRelayRuntimes();
        Instant lastCommunicationTime = times.getLastCommunicationTime();
        Instant lastNonZeroRuntime = times.getLastNonZeroRuntime();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE DynamicLcrCommunications");
        sql.append("SET");
        setValue(sql, "LastCommunication", lastCommunicationTime, true);
        setValue(sql, "LastNonZeroRuntime", lastNonZeroRuntime, true);
        setValue(sql, "Relay1Runtime", relayRuntimes.get(1), true);
        setValue(sql, "Relay2Runtime", relayRuntimes.get(2), true);
        setValue(sql, "Relay3Runtime", relayRuntimes.get(3), true);
        setValue(sql, "Relay4Runtime", relayRuntimes.get(4), false);
        sql.append("WHERE DeviceId").eq(times.getDeviceId());
        
        int rowsAffected = jdbcTemplate.update(sql);
        return rowsAffected != 0;
    }
    
    private void setValue(SqlStatementBuilder sql, String columnName, Instant time, boolean includeComma) {
        sql.append(columnName).append("= (");
        sql.append("CASE");
        sql.append("WHEN COALESCE(").append(columnName).append(", CAST('01-JAN-1900' AS DATE))");
        sql.lt(time).append("THEN").appendArgument(time);
        sql.append("ELSE").append(columnName);
        sql.append("END)");
        if(includeComma) {
            sql.append(",");
        }
    }
}
