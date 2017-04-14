package com.cannontech.common.device.streaming.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.streaming.dao.DeviceBehaviorDao;
import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorReport;
import com.cannontech.common.device.streaming.model.BehaviorReportStatus;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.DataStreamingMetricStatus;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class DeviceBehaviorDaoImpl implements DeviceBehaviorDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DatabaseVendorResolver dbVendorResolver;
    private final static Logger log = YukonLogManager.getLogger(DeviceBehaviorDaoImpl.class);
    private final static RowMapper<Map.Entry<Integer, NameValue>> rowMapperBehaviorReportIdToReportValues =
        new RowMapper<Entry<Integer, NameValue>>() {
            @Override
            public Entry<Integer, NameValue> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer behaviorReportId = rs.getInt("BehaviorReportId");
                String name = rs.getString("Name");
                String value = rs.getString("Value");
                return Maps.immutableEntry(behaviorReportId, new NameValue(name, value));
            }
        };
    private final static RowMapper<Map.Entry<Integer, NameValue>> rowMapperBehaviorIdToBehaviorValues =
        new RowMapper<Entry<Integer, NameValue>>() {
            @Override
            public Entry<Integer, NameValue> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer behaviorId = rs.getInt("BehaviorId");
                String name = rs.getString("Name");
                String value = rs.getString("Value");
                return Maps.immutableEntry(behaviorId, new NameValue(name, value));
            }
        };

    @Override
    public Map<Integer, Integer> getDeviceIdsToBehaviorIdMap(BehaviorType type, List<BuiltInAttribute> attributes,
            Integer interval, Integer behaviorId) {

        Map<Integer, Integer> resultMap = new HashMap<>();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT dbm.behaviorId, dbm.deviceId");
        sql.append("FROM Behavior b");
        if (attributes != null) {
            sql.append("JOIN BehaviorValue behaviorAttributes ON b.BehaviorId=behaviorAttributes.BehaviorId");
        }
        if (interval != null) {
            sql.append("JOIN BehaviorValue behaviorInterval ON b.BehaviorId=behaviorInterval.BehaviorId ");
        }
        sql.append("JOIN DeviceBehaviorMap dbm ON b.BehaviorId=dbm.BehaviorId");
        sql.append("WHERE b.BehaviorType").eq_k(type);
        if (interval != null) {
            sql.append("AND behaviorInterval.name like '%interval%'");
            sql.append("AND behaviorInterval.value").eq(String.valueOf(interval));
        }
        if (attributes != null) {
            sql.append("AND behaviorAttributes.name like '%attribute%'");
            sql.append("AND behaviorAttributes.value").in_k(attributes);
        }
        if (behaviorId != null) {
            sql.append("AND b.BehaviorId").eq(behaviorId);
        }

        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Integer key = rs.getInt("deviceId");
                Integer value = rs.getInt("behaviorId");
                resultMap.put(key, value);
            }
        });

        return resultMap;
    }
    

    
    @Override
    @Transactional
    public void deleteUnusedBehaviors() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM Behavior");
        sql.append("WHERE BehaviorId NOT IN");
        sql.append("  (SELECT BehaviorId");
        sql.append("   FROM DeviceBehaviorMap)");

        jdbcTemplate.update(sql);
    }
    
    @Override
    @Transactional
    public void assignBehavior(int behaviorId,  BehaviorType type, List<Integer> deviceIds, boolean deleteUnusedBehaviors) {
        log.debug("Assigning behavior behaviorId="+behaviorId+ ", devices=" + deviceIds);

        List<List<Integer>> ids = Lists.partition(deviceIds, ChunkingSqlTemplate.DEFAULT_SIZE);
        ids.forEach(idBatch -> {
            unassignBehaviorForBatch(type, idBatch);
            
            //assign devices
            SqlStatementBuilder insertSql = new SqlStatementBuilder();
            insertSql.append("INSERT INTO DeviceBehaviorMap");
            insertSql.append("(DeviceId, BehaviorId)");
            insertSql.append("values");
            insertSql.append("(?, ?)");
            
            jdbcTemplate.batchUpdate(insertSql.toString(), new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, idBatch.get(i));
                    ps.setInt(2, behaviorId);
                }

                @Override
                public int getBatchSize() {
                    return idBatch.size();
                }
            });
        });
        
        if (deleteUnusedBehaviors) {
            deleteUnusedBehaviors();
        }
    }

    @Override
    @Transactional
    public void unassignBehavior(BehaviorType type, List<Integer> deviceIds) {
        log.debug("Devices to ussign=" + deviceIds.size());

        List<List<Integer>> ids = Lists.partition(deviceIds, ChunkingSqlTemplate.DEFAULT_SIZE);
        ids.forEach(idBatch -> {
            unassignBehaviorForBatch(type, idBatch);
        });
        
        deleteUnusedBehaviors();
    }
    
    @Override
    public List<Behavior> getAllBehaviorsByType(BehaviorType type) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT BehaviorId, BehaviorType");
        sql.append("FROM Behavior");
        sql.append("WHERE BehaviorType").eq_k(type);

        List<Behavior> behaviors = jdbcTemplate.query(sql, new YukonRowMapper<Behavior>() {
            @Override
            public Behavior mapRow(YukonResultSet rs) throws SQLException {
                return getBehaviorFromResultSet(rs);
            }
        });

        addBehaviorValuesToBehaviors(behaviors);
        return behaviors;
    }
    
    @Override
    @Transactional
    public int saveBehavior(Behavior behavior) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT BehaviorId");
        sql.append("FROM Behavior");
        sql.append("WHERE BehaviorId").eq(behavior.getId());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            SqlParameterSink params = updateCreateSql.update("Behavior");
            initParameterSink(behavior, params);
            updateCreateSql.append("WHERE BehaviorId").eq(behavior.getId());
            deleteBehaviorValues(behavior.getId());
        } catch (EmptyResultDataAccessException e) {
            behavior.setId(nextValueHelper.getNextValue("Behavior"));
            SqlParameterSink params = updateCreateSql.insertInto("Behavior");
            initParameterSink(behavior, params);
        }

        jdbcTemplate.update(updateCreateSql);
        saveBehaviorValues(behavior.getId(), behavior.getValuesMap());

        return behavior.getId();
    }

    @Override
    @Transactional
    public void deleteBehavior(int behaviorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM Behavior");
        sql.append("WHERE BehaviorId").eq(behaviorId);

        jdbcTemplate.update(sql);
    }
    
    @Override
    @Transactional
    public void deleteBehaviorReport(int behaviorReportId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM BehaviorReport");
        sql.append("WHERE BehaviorReportId").eq(behaviorReportId);

        jdbcTemplate.update(sql);
    }

    @Override
    public Behavior findBehaviorByDeviceIdAndType(int deviceId, BehaviorType type) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Behavior.BehaviorId, BehaviorType");
        sql.append("FROM Behavior");
        sql.append("JOIN DeviceBehaviorMap ON DeviceBehaviorMap.BehaviorId = Behavior.BehaviorId");
        sql.append("WHERE BehaviorType").eq_k(type);
        sql.append("AND DeviceId").eq(deviceId);
        try {
            Behavior behavior = jdbcTemplate.queryForObject(sql, new YukonRowMapper<Behavior>() {
                @Override
                public Behavior mapRow(YukonResultSet rs) throws SQLException {
                    Behavior behavior = new Behavior();
                    behavior.setId(rs.getInt("BehaviorId"));
                    behavior.setType(rs.getEnum("BehaviorType", BehaviorType.class));
                    return behavior;
                }
            });
            Map<String, String> values = getBehaviorValuesByBehaviorId(behavior.getId());
            behavior.setValues(values);
            return behavior;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public Iterable<DiscrepancyInfo> findDiscrepancies(BehaviorType type, Integer deviceId) {
        Map<Integer, DiscrepancyInfo> discrepancyForDeviceId = new HashMap<>();
        findDiscrepanciesByNoBehavior(type, discrepancyForDeviceId, deviceId);
        findDiscrepanciesByFailedBehaviorReport(type, discrepancyForDeviceId, deviceId);
        findDiscrepanciesByComparingChannelsAndIntervals(type, discrepancyForDeviceId, deviceId);
        addBehaviorValuesToBehaviors(discrepancyForDeviceId.values()
                                                           .stream()
                                                           .filter(d -> d.getBehavior() != null)
                                                           .map(d -> d.getBehavior())
                                                           .collect(Collectors.toList()));
        addBehaviorReportValuesToBehaviorReports(discrepancyForDeviceId.values()
                                                                       .stream()
                                                                       .filter(d -> d.getBehaviorReport() != null)
                                                                       .map(d -> d.getBehaviorReport())
                                                                       .collect(Collectors.toList()));
        addLastCommunicatedTimeToDiscrepancyInfos(discrepancyForDeviceId);
        return discrepancyForDeviceId.values();
    }
    
    private void addLastCommunicatedTimeToDiscrepancyInfos(Map<Integer, DiscrepancyInfo> discrepancyForDeviceId) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT PAObjectID, max(timestamp) as lastCommunicated");
                sql.append("FROM POINT p");
                sql.append("JOIN DYNAMICPOINTDISPATCH dpd on p.POINTID = dpd.POINTID");
                sql.append("WHERE p.paobjectid").in(subList);
                sql.append("GROUP BY PAObjectID");
                return sql;
            }
        };

        template.query(sqlGenerator, discrepancyForDeviceId.keySet(), new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int deviceId = rs.getInt("PAObjectID");
                DiscrepancyInfo discrepancy = discrepancyForDeviceId.get(deviceId);
                discrepancy.setLastCommunicated(rs.getInstant("lastCommunicated"));
            }
        });
    }

    /**
     * Return database vendor specific SQL.
     */
    private String right(String string, int numberOfCharacters) {
        if (dbVendorResolver.getDatabaseVendor().isOracle()) {
            return "substr(" + string + ",-" + numberOfCharacters + ")";
        } else {
            return "right(" + string + "," + numberOfCharacters + ")";
        }
    }
   
    /**
     * Return database vendor specific SQL.
     */
    private String leftEq(String string1, String string2) {
        if (dbVendorResolver.getDatabaseVendor().isOracle()) {
            return "substr(" + string1 + ", 1, 11)=substr(" + string2 + ", 1, 11)";
        } else {
            return "substring(" + string1 + ", 1, 11)=substring(" + string2 + ", 1, 11)";
        }
    }
    /**
     * Finds discrepancies that do not have behavior, but device is globally enabled AND any channels are enabled.
     * Populates discrepancyForDeviceId with result.
     */
    private void findDiscrepanciesByNoBehavior(BehaviorType type, Map<Integer, DiscrepancyInfo> discrepancyForDeviceId, Integer deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT br.BehaviorReportId, DeviceId, BehaviorType, BehaviorStatus, TimeStamp");
        sql.append("FROM BehaviorReport br");
        sql.append("JOIN BehaviorReportValue ge on br.BehaviorReportId=ge.BehaviorReportId");
        sql.append("    AND ge.Name").eq(STREAMING_ENABLED_STRING);
        sql.append("    AND ge.Value").eq(Boolean.TRUE.toString());
        sql.append("JOIN BehaviorReportValue ce on br.BehaviorReportId=ce.BehaviorReportId");
        sql.append("    AND ").append(right("ce.Name", 8)).eq(ENABLED_STRING);
        sql.append("    AND ce.Value").eq(Boolean.TRUE.toString());
        sql.append("WHERE BehaviorType").eq_k(type);
        if (deviceId != null) {
            sql.append("AND br.DeviceId").eq(deviceId);
        }
        sql.append("AND NOT exists (");
        sql.append("    SELECT *");
        sql.append("    FROM DeviceBehaviorMap dbm");
        sql.append("    JOIN Behavior b on dbm.BehaviorId = b.BehaviorId");
        sql.append("    WHERE b.BehaviorType").eq_k(type);
        sql.append("    AND dbm.DeviceId = br.DeviceId)");
                     
        jdbcTemplate.query(sql, new DiscrepancyCallback(discrepancyForDeviceId));
    }
    
    /**
     * Finds discrepancies that do not have behavior report values.
     * Has behavior, but device is not globally enabled AND Has behavior, but no reported behavior.
     * Populates discrepancyForDeviceId map with the result.
     */
    private void findDiscrepanciesByFailedBehaviorReport(BehaviorType type, Map<Integer, DiscrepancyInfo> discrepancyForDeviceId, Integer deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT b.BehaviorId, b.BehaviorType, dbm.DeviceId, br.BehaviorReportId, br.BehaviorType, br.BehaviorStatus, br.TimeStamp");
        sql.append("FROM DeviceBehaviorMap dbm");
        sql.append("JOIN Behavior b on b.BehaviorId = dbm.BehaviorId");
        sql.append("    AND b.BehaviorType").eq_k(type);
        if (deviceId != null) {
            sql.append("AND dbm.DeviceId").eq(deviceId);
        }
        sql.append("LEFT JOIN BehaviorReport br on dbm.DeviceId = br.DeviceId");
        sql.append("WHERE NOT exists (");
        sql.append("    SELECT *");
        sql.append("    FROM BehaviorReport br");
        sql.append("    JOIN BehaviorReportValue brv on br.BehaviorReportId = brv.BehaviorReportId");
        sql.append("    WHERE br.BehaviorType=b.BehaviorType");
        sql.append("        AND br.DeviceId=dbm.DeviceId");
        sql.append("        AND brv.name").eq(STREAMING_ENABLED_STRING);
        sql.append("        AND brv.value").eq(Boolean.TRUE.toString());
        sql.append("    )");
        jdbcTemplate.query(sql, new DiscrepancyCallback(discrepancyForDeviceId));
    }
    
    /**
     * Finds discrepancies by comparing channels and intervals.
     * Populates discrepancyForDeviceId with the result.
     */
    private void findDiscrepanciesByComparingChannelsAndIntervals(BehaviorType type, Map<Integer, DiscrepancyInfo> discrepancyForDeviceId, Integer deviceId) {
        SqlStatementBuilder sql1 = new SqlStatementBuilder();
        //Has behavior, but an expected channel is disabled OR has a bad status OR its interval does not match.
        sql1.append("SELECT dbm.DeviceId, b.BehaviorId, b.BehaviorType, br.BehaviorReportId, br.BehaviorType, br.BehaviorStatus, br.TimeStamp");
        sql1.append("FROM DeviceBehaviorMap dbm");
        sql1.append("JOIN Behavior b on dbm.BehaviorId = b.BehaviorId");
        sql1.append("AND b.BehaviorType").eq_k(type);
        if (deviceId != null) {
            sql1.append("AND dbm.DeviceId").eq(deviceId);
        }
        sql1.append("JOIN behaviorvalue a on b.BehaviorId = a.BehaviorId");
        sql1.append("   AND ").append(right("a.name", 10)).eq(ATTRIBUTE_STRING);
        sql1.append("JOIN behaviorvalue i on b.BehaviorId = i.BehaviorId");
        sql1.append("   AND ").append(right("i.name", 9)).eq(INTERVAL_STRING);
        sql1.append("   AND ").append(leftEq("a.name","i.name"));
        sql1.append("JOIN BehaviorReport br on dbm.DeviceId=br.DeviceId");
        sql1.append("JOIN behaviorreportvalue ra on br.BehaviorReportId = ra.BehaviorReportId");
        sql1.append("   AND ").append(right("ra.name", 10)).eq(ATTRIBUTE_STRING);
        sql1.append("JOIN behaviorreportvalue ri on br.BehaviorReportId = ri.BehaviorReportId");
        sql1.append("   AND ").append(right("ri.name", 9)).eq(INTERVAL_STRING);
        sql1.append("   AND ").append(leftEq("ra.name", "ri.name"));
        sql1.append("JOIN behaviorreportvalue re on br.BehaviorReportId = re.BehaviorReportId");
        sql1.append("   AND ").append(right("re.name", 8)).eq(ENABLED_STRING);
        sql1.append("   AND ").append(leftEq("ra.name", "re.name"));
        sql1.append("JOIN behaviorreportvalue rs on br.BehaviorReportId = rs.BehaviorReportId");
        sql1.append("   AND ").append(right("rs.name", 7)).eq(STATUS_STRING);
        sql1.append("   AND ").append(leftEq("ra.name", "rs.name"));
        sql1.append("WHERE br.BehaviorType=b.BehaviorType");
        sql1.append("AND a.value=ra.value");
        sql1.append("AND (re.value").neq(Boolean.TRUE.toString());
        sql1.append("     OR rs.value").neq_k(DataStreamingMetricStatus.OK);
        sql1.append("     OR ri.Value<>i.Value)");

        jdbcTemplate.query(sql1, new DiscrepancyCallback(discrepancyForDeviceId));
        
        //Has behavior, but an unexpected channel is enabled.
        SqlStatementBuilder sql2 = new SqlStatementBuilder();
        sql2.append("SELECT dbm.DeviceId, b.BehaviorId, b.BehaviorType, br.BehaviorReportId, br.BehaviorType, br.BehaviorStatus, br.TimeStamp");
        sql2.append("FROM DeviceBehaviorMap dbm");
        sql2.append("JOIN Behavior b on dbm.BehaviorId = b.BehaviorId");
        sql2.append("JOIN BehaviorReport br on br.DeviceId = dbm.DeviceId");
        sql2.append("JOIN behaviorreportvalue ra on br.BehaviorReportId = ra.BehaviorReportId");
        sql2.append("JOIN behaviorreportvalue re on ").append(leftEq("ra.name", "re.name"));
        sql2.append("AND br.BehaviorReportId = re.BehaviorReportId");
        sql2.append("WHERE b.BehaviorType").eq_k(type);
        if (deviceId != null) {
            sql2.append("AND br.DeviceId").eq(deviceId);
        }
        sql2.append("AND ").append(right("ra.name", 10)).eq(ATTRIBUTE_STRING);
        sql2.append("AND ").append(right("re.name", 8)).eq(ENABLED_STRING);
        sql2.append("AND re.Value").eq(Boolean.TRUE.toString());
        sql2.append("AND not exists (");
        sql2.append("   SELECT *");
        sql2.append("   FROM BehaviorValue");
        sql2.append("   WHERE behaviorid=b.behaviorid");
        sql2.append("   AND ").append(right("name", 10)).eq(ATTRIBUTE_STRING);
        sql2.append("   AND value=ra.value)");
        
        jdbcTemplate.query(sql2, new DiscrepancyCallback(discrepancyForDeviceId));
    }

    private static class DiscrepancyCallback implements YukonRowCallbackHandler {
        private Map<Integer, DiscrepancyInfo> discrepancyForDeviceId;

        DiscrepancyCallback(Map<Integer, DiscrepancyInfo> discrepancyForDeviceId) {
            this.discrepancyForDeviceId = discrepancyForDeviceId;
        }

        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            DiscrepancyInfo discrepancy = new DiscrepancyInfo();
            discrepancy.setBehaviorReport(getBehaviorReportFromResultSet(rs));
            if (discrepancy.getBehaviorReport().getStatus() == null) {
                // behavior report doesn't exist
                discrepancy.getBehaviorReport().setStatus(BehaviorReportStatus.FAILED);
            }
            try {
                discrepancy.setBehavior(getBehaviorFromResultSet(rs));
            } catch (SQLException e) {
                // behavior doesn't exist
            }
            int deviceId = rs.getInt("deviceId");
            discrepancy.setDeviceId(deviceId);
            discrepancyForDeviceId.put(deviceId, discrepancy);
        }
    }

    @Override
    public Behavior getBehaviorById(int behaviorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT BehaviorId, BehaviorType");
        sql.append("FROM Behavior");
        sql.append("WHERE BehaviorId").eq(behaviorId);

        Behavior behavior = jdbcTemplate.queryForObject(sql, new YukonRowMapper<Behavior>() {
            @Override
            public Behavior mapRow(YukonResultSet rs) throws SQLException {
                Behavior behavior = new Behavior();
                behavior.setId(behaviorId);
                behavior.setType(rs.getEnum("BehaviorType", BehaviorType.class));
                return behavior;
            }
        });

        Map<String, String> values = getBehaviorValuesByBehaviorId(behaviorId);
        behavior.setValues(values);
        return behavior;
    }
    
    //TODO: save only if newer?
    @Override
    @Transactional
    public int saveBehaviorReport(BehaviorReport report) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT BehaviorReportId");
        sql.append("FROM BehaviorReport");
        sql.append("WHERE deviceId").eq(report.getDeviceId());
        sql.append("AND BehaviorType").eq_k(report.getType());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            report.setId(jdbcTemplate.queryForInt(sql));
            SqlParameterSink params = updateCreateSql.update("BehaviorReport");
            initParameterSink(report, params);
            updateCreateSql.append("WHERE BehaviorReportId").eq(report.getId());
            deleteBehaviorReportValues(report.getId());
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("BehaviorReport");
            report.setId(nextValueHelper.getNextValue("BehaviorReport"));
            initParameterSink(report, params);
        }
        jdbcTemplate.update(updateCreateSql);
        saveBehaviorReportValues(report.getId(), report.getValuesMap());

        return report.getId();
    }
    
    @Override
    public Map<Integer, Behavior> getBehaviorsByType(BehaviorType type) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Behavior.BehaviorId, BehaviorType, DeviceId");
        sql.append("FROM Behavior");
        sql.append("JOIN DeviceBehaviorMap ON DeviceBehaviorMap.BehaviorId = Behavior.BehaviorId");
        sql.append("WHERE BehaviorType").eq_k(type);
        
        Map<Integer, Behavior> behaviors = new HashMap<>();
        jdbcTemplate.query(sql,  new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                Behavior report = new Behavior();
                report.setId(rs.getInt("BehaviorId"));
                report.setType(rs.getEnum("BehaviorType", BehaviorType.class));
                int deviceId = rs.getInt("deviceId");
                behaviors.put(deviceId, report);
            }
        });

       addBehaviorValuesToBehaviors(behaviors.values().stream().distinct().collect(Collectors.toList()));
       return behaviors;
    }
    
    @Override
    public Map<Integer, Behavior> getBehaviorsByTypeAndDeviceIds(BehaviorType type,
            List<Integer> deviceIds) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT Behavior.BehaviorId, BehaviorType, DeviceId");
                sql.append("FROM Behavior");
                sql.append("JOIN DeviceBehaviorMap ON DeviceBehaviorMap.BehaviorId = Behavior.BehaviorId");
                sql.append("WHERE BehaviorType").eq_k(type);
                sql.append("AND DeviceId").in(subList);
                return sql;
            }
        };

        Map<Integer, Behavior> behaviors = new HashMap<>();
        template.query(sqlGenerator,deviceIds, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                Behavior report = new Behavior();
                report.setId(rs.getInt("BehaviorId"));
                report.setType(rs.getEnum("BehaviorType", BehaviorType.class));
                int deviceId = rs.getInt("deviceId");
                behaviors.put(deviceId, report);
            }
        });

       addBehaviorValuesToBehaviors(behaviors.values().stream().distinct().collect(Collectors.toList()));
       return behaviors;
    }
  
    private void addBehaviorValuesToBehaviors(List<Behavior> behaviors) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT BehaviorId, Name, Value");
                sql.append("FROM BehaviorValue");
                sql.append("WHERE BehaviorId").in(subList);
                return sql;
            }
        };

        Multimap<Integer, NameValue> values = template.multimappedQuery(sqlGenerator,
            behaviors.stream().map(behavior -> behavior.getId()).collect(Collectors.toList()),
            rowMapperBehaviorIdToBehaviorValues, Functions.identity());
        for(Behavior behavior: behaviors){
            Collection<NameValue> reportValues =  values.get(behavior.getId());
            Iterator<NameValue> it = reportValues.iterator();
            while(it.hasNext()){
                NameValue value = it.next();
                behavior.addValue(value.name, value.value);
            }
        }
    }

    @Override
    public Map<Integer, BehaviorReport> getBehaviorReportsByTypeAndDeviceIds(BehaviorType type,
            List<Integer> deviceIds) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT BehaviorReportId, DeviceId, BehaviorType, BehaviorStatus, TimeStamp");
                sql.append("FROM BehaviorReport");
                sql.append("WHERE BehaviorType").eq_k(type);
                sql.append("AND DeviceId").in(subList);
                return sql;
            }
        };

        List<BehaviorReport> reports = template.query(sqlGenerator, deviceIds, new YukonRowMapper<BehaviorReport>() {
            @Override
            public BehaviorReport mapRow(YukonResultSet rs) throws SQLException {
                return getBehaviorReportFromResultSet(rs);
            }
        });

        addBehaviorReportValuesToBehaviorReports(reports);
        return Maps.uniqueIndex(reports, c -> c.getDeviceId());
    }

    @Override
    public Map<Integer, BehaviorReport> getBehaviorReportsByType(BehaviorType type) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT BehaviorReportId, DeviceId, BehaviorType, BehaviorStatus, TimeStamp");
        sql.append("FROM BehaviorReport");
        sql.append("WHERE BehaviorType").eq_k(type);

        List<BehaviorReport> reports = new ArrayList<>();

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                reports.add(getBehaviorReportFromResultSet(rs));
            }
        });

        addBehaviorReportValuesToBehaviorReports(reports);
        return Maps.uniqueIndex(reports, c -> c.getDeviceId());
    }
       
    private static BehaviorReport getBehaviorReportFromResultSet(YukonResultSet rs) throws SQLException {
        BehaviorReport report = new BehaviorReport();
        report.setId(rs.getInt("BehaviorReportId"));
        report.setType(rs.getEnum("BehaviorType", BehaviorType.class));
        report.setDeviceId(rs.getInt("DeviceId"));
        report.setStatus(rs.getEnum("BehaviorStatus", BehaviorReportStatus.class));
        report.setTimestamp(rs.getInstant("TimeStamp"));
        return report;
    }
    
    private static Behavior getBehaviorFromResultSet(YukonResultSet rs) throws SQLException {
        Behavior behavior = new Behavior();
        behavior.setId(rs.getInt("BehaviorId"));
        behavior.setType(rs.getEnum("BehaviorType", BehaviorType.class));
        return  behavior;
    }
    
    //populates reports with report values
    private void addBehaviorReportValuesToBehaviorReports(List<BehaviorReport> reports) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT BehaviorReportId, Name, Value");
                sql.append("FROM BehaviorReportValue");
                sql.append("WHERE BehaviorReportId").in(subList);
                return sql;
            }
        };

        Multimap<Integer, NameValue> values = template.multimappedQuery(sqlGenerator,
            reports.stream().map(report -> report.getId()).collect(Collectors.toList()),
            rowMapperBehaviorReportIdToReportValues, Functions.identity());
        for (BehaviorReport report : reports) {
            Collection<NameValue> reportValues = values.get(report.getId());
            Iterator<NameValue> it = reportValues.iterator();
            while (it.hasNext()) {
                NameValue value = it.next();
                report.addValue(value.name, value.value);
            }
        }
    }
    
    @Override
    @Transactional
    public void updateBehaviorReportStatus(BehaviorType type, BehaviorReportStatus status, List<Integer> deviceIds) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        SqlParameterSink params = updateSql.update("BehaviorReport");
        params.addValue("BehaviorStatus", status);
        params.addValue("TimeStamp", new Instant());
        updateSql.append("WHERE BehaviorType").eq_k(type);
        updateSql.append("AND DeviceId").in(deviceIds);
        jdbcTemplate.update(updateSql);
    }

    private void initParameterSink(BehaviorReport report, SqlParameterSink params){
        params.addValue("BehaviorReportId", report.getId());
        params.addValue("DeviceId", report.getDeviceId());
        params.addValue("BehaviorType", report.getType());
        params.addValue("BehaviorStatus", report.getStatus());
        params.addValue("TimeStamp", report.getTimestamp());
    }
    
    private void initParameterSink(Behavior behavior, SqlParameterSink params){
        params.addValue("BehaviorId", behavior.getId());
        params.addValue("BehaviorType", behavior.getType());
    }
    
    private Map<String, String> getBehaviorValuesByBehaviorId(int behaviorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Name, Value");
        sql.append("FROM BehaviorValue");
        sql.append("WHERE BehaviorId").eq(behaviorId);
        
        Map<String, String> nameValueMap = new HashMap<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                nameValueMap.put(rs.getString("Name"), rs.getString("Value"));
            }
        });
        
        return nameValueMap;
    }
            
    private static class NameValue{
        public NameValue(String name, String value) {
            this.name = name;
            this.value = value;
        }
        String name;
        String value;
    }
    
    private void saveBehaviorValues(int behaviorId, Map<String, String> values) {
        values.forEach((name, value) -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.insertInto("BehaviorValue");
            params.addValue("BehaviorId", behaviorId);
            params.addValue("Name", name);
            params.addValue("Value", value);

            jdbcTemplate.update(sql);
        });
    }

    private void deleteBehaviorValues(int behaviorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM BehaviorValue");
        sql.append("WHERE BehaviorId").eq(behaviorId);

        jdbcTemplate.update(sql);
    }

    private void deleteBehaviorReportValues(int behaviorReportId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM BehaviorReportValue");
        sql.append("WHERE BehaviorReportId").eq(behaviorReportId);

        jdbcTemplate.update(sql);
    }

    private void saveBehaviorReportValues(int behaviorReportId, Map<String, String> values) {
        values.forEach((name, value) -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.insertInto("BehaviorReportValue");
            params.addValue("BehaviorReportId", behaviorReportId);
            params.addValue("Name", name);
            params.addValue("Value", value);

            jdbcTemplate.update(sql);
        });
    }
    
    private void unassignBehaviorForBatch(BehaviorType type, List<Integer> deviceIds) {
        
        log.debug("Batch=" + deviceIds.size());
        
        SqlStatementBuilder innerSql = new SqlStatementBuilder();
        innerSql.append("SELECT BehaviorId FROM Behavior WHERE BehaviorType").eq_k(type);
        
        // unassign devices
        SqlStatementBuilder unassignSql = new SqlStatementBuilder();
        unassignSql.append("DELETE FROM DeviceBehaviorMap");
        unassignSql.append("WHERE BehaviorId").in(innerSql);
        unassignSql.append("AND deviceId").in(deviceIds);

        jdbcTemplate.update(unassignSql);
    }
}
