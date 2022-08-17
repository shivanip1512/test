package com.cannontech.common.smartNotification.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventData;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.stars.scheduledDataImport.AssetImportResultType;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * This dao handles saving and loading Smart Notification events.
 */
public class SmartNotificationEventDaoImpl implements SmartNotificationEventDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DatabaseVendorResolver databaseConnectionVendorResolver;

    private ChunkingSqlTemplate chunkingTemplate;
    
    private static final YukonRowMapper<SmartNotificationEventData> createDeviceDataMonitorEventDetailRowMapper =
            createRowMapper(SmartNotificationEventType.DEVICE_DATA_MONITOR);
    private static final YukonRowMapper<SmartNotificationEventData> createInfrastructureWarningEventDetailRowMapper =
            createRowMapper(SmartNotificationEventType.INFRASTRUCTURE_WARNING);
    private static final YukonRowMapper<SmartNotificationEventData> createWatchdogWarningEventDetailRowMapper =
            createRowMapperWatchdog(SmartNotificationEventType.YUKON_WATCHDOG);
    private static final YukonRowMapper<SmartNotificationEventData> createAssetImportEventDetailRowMapper =
            createAssetImportRowMapper(SmartNotificationEventType.ASSET_IMPORT);

    public static final Comparator<SmartNotificationEventData> fileFailureCountComparator = 
            Comparator.comparingInt(SmartNotificationEventData::getFileErrorCount);
   
    @PostConstruct
    public void init() {
        chunkingTemplate = new ChunkingSqlTemplate(jdbcTemplate);
    }
    
    private static YukonRowMapper<SmartNotificationEventData> createAssetImportRowMapper(
            final SmartNotificationEventType smartNotificationEventType) {
        final YukonRowMapper<SmartNotificationEventData> mapper = new YukonRowMapper<SmartNotificationEventData>() {
            @Override
            public SmartNotificationEventData mapRow(YukonResultSet rs) throws SQLException {
                SmartNotificationEventData row = new SmartNotificationEventData();
                row.setEventId(rs.getInt("EventId"));
                row.setTimestamp(rs.getInstant("Timestamp"));
                row.setJobGroupId(rs.getInt("jobGroupId"));
                row.setJobName(rs.getString("jobName"));
                row.setFileSuccessCount(rs.getInt("successFileCount"));
                if (StringUtils.isNoneBlank(rs.getString("filesWithError"))) {
                    StringTokenizer tokenizer = new StringTokenizer(rs.getString("filesWithError"), ",");
                    row.setFileErrorCount(tokenizer.countTokens());
                }
                return row;
            }
        };
        return mapper;
    }

    private static YukonRowMapper<SmartNotificationEventData> createRowMapper(final SmartNotificationEventType smartNotificationEventType) {
        final YukonRowMapper<SmartNotificationEventData> mapper = new YukonRowMapper<SmartNotificationEventData>() {
            @Override
            public SmartNotificationEventData mapRow(YukonResultSet rs) throws SQLException {
                SmartNotificationEventData row = new SmartNotificationEventData();
                row.setEventId(rs.getInt("EventId"));
                row.setTimestamp(rs.getInstant("Timestamp"));
                row.setDeviceId(rs.getInt("DeviceId"));
                row.setDeviceName(rs.getString("DeviceName"));
                row.setStatus(rs.getString("Status"));
                if (smartNotificationEventType == SmartNotificationEventType.INFRASTRUCTURE_WARNING) {
                    row.setSeverity(rs.getString("Severity"));
                    row.setArgument1(rs.getString("Argument1"));
                    row.setArgument2(rs.getString("Argument2"));
                    row.setArgument3(rs.getString("Argument3"));
                    row.setType(rs.getString("DeviceType"));
                }
                return row;
            }
        };
        return mapper;
    }

    private static YukonRowMapper<SmartNotificationEventData> createRowMapperWatchdog(final SmartNotificationEventType smartNotificationEventType) {
        final YukonRowMapper<SmartNotificationEventData> mapper = new YukonRowMapper<SmartNotificationEventData>() {
            @Override
            public SmartNotificationEventData mapRow(YukonResultSet rs) throws SQLException {
                SmartNotificationEventData row = new SmartNotificationEventData();
                row.setEventId(rs.getInt("EventId"));
                row.setTimestamp(rs.getInstant("Timestamp"));
                row.setStatus(rs.getString("Status"));
                row.setWarningType(rs.getEnum("WarningType", WatchdogWarningType.class).getName());
                return row;
            }
        };
        return mapper;
    }
    private static final YukonRowMapper<SmartNotificationEvent> eventMapper = r -> {
        Integer id = r.getInt("EventId");
        Instant timestamp = r.getInstant("Timestamp");
        SmartNotificationEvent event = new SmartNotificationEvent(id, timestamp);
        event.setGroupProcessTime(r.getInstant("GroupProcessTime"));
        event.setImmediateProcessTime(r.getInstant("ImmediateProcessTime"));
        return event;
    };
    
    private static class ProcessedTimeUpdater implements SqlFragmentGenerator<Integer> {
        private Instant processedTime;
        private String columnName;
        
        public ProcessedTimeUpdater(Instant processedTime, SmartNotificationFrequency frequency) {
            this.processedTime = processedTime;
            columnName = frequency == SmartNotificationFrequency.COALESCING ? "GroupProcessTime" : "ImmediateProcessTime";
        }

        @Override
        public SqlFragmentSource generate(List<Integer> eventsIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("UPDATE SmartNotificationEvent");
            sql.append("SET ").append(columnName).eq(processedTime);
            sql.append("WHERE EventId").in(eventsIds);
            sql.append("AND ").append(columnName).append(" IS NULL");
            return sql;
        }
    }
    
    private final static class NameValue {
        public NameValue(String name, String value) {
            this.name = name;
            this.value = value;
        }
        String name;
        String value;
    }

    @Override
    public void deleteAllEvents() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM SmartNotificationEvent");
        jdbcTemplate.update(sql);
    }
    
    @Transactional
    @Override
    public void save(SmartNotificationEventType type, List<SmartNotificationEvent> events) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        Map<Integer, Map<String, Object>> params = new HashMap<>();
        List<List<Object>> values = events.stream().map(event -> {
            event.setEventId(nextValueHelper.getNextValue("SmartNotificationEvent"));
            params.put(event.getEventId(), event.getParameters());
            List<Object> row = Lists.newArrayList(event.getEventId(), type, event.getTimestamp());
            return row;
        }).collect(Collectors.toList());

        sql.batchInsertInto("SmartNotificationEvent").columns("EventId", "Type", "Timestamp").values(values);
        jdbcTemplate.yukonBatchUpdate(sql);
        saveEventParameters(params);
    }

    private void saveEventParameters(Map<Integer, Map<String, Object>> params) {
        List<List<Object>> values = new ArrayList<>();
        params.forEach((id, param) -> {
            param.forEach((k, v) -> {
                values.add(Lists.newArrayList(id, k, v));
            });
        });

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.batchInsertInto("SmartNotificationEventParam").columns("EventId", "Name", "Value").values(values);
        jdbcTemplate.yukonBatchUpdate(sql);
    }

    @Override
    public void markEventsAsProcessed(List<SmartNotificationEvent> events, Instant processedTime,
            SmartNotificationFrequency... frequency) {
        if (!events.isEmpty()) {
            List<Integer> eventsIds = events.stream().map(e -> e.getEventId()).collect(Collectors.toList());
            Arrays.asList(frequency).forEach(f -> chunkingTemplate.update(new ProcessedTimeUpdater(processedTime, f), eventsIds));
        }
    }

    @Override
    public List<SmartNotificationEvent> getUnprocessedEvents(SmartNotificationEventType type) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventId, Timestamp, GroupProcessTime, ImmediateProcessTime");
        sql.append("FROM  SmartNotificationEvent sne");
        sql.append("WHERE Type").eq_k(type);
        sql.append("AND (GroupProcessTime IS NULL OR ImmediateProcessTime IS NULL)");
        List<SmartNotificationEvent> events = jdbcTemplate.query(sql, eventMapper);
        if (!events.isEmpty()) {
            addParameters(events);
        }
        return events;
    }

    private void addParameters(List<SmartNotificationEvent> events) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT EventId, Name, Value");
                sql.append("FROM SmartNotificationEventParam");
                sql.append("WHERE EventId").in(subList);
                return sql;
            }
        };

        List<Integer> eventIds = events.stream().map(e -> e.getEventId()).collect(Collectors.toList());
        Multimap<Integer, NameValue> values = template.multimappedQuery(sqlGenerator, eventIds, rs -> {
            Integer eventId = rs.getInt("EventId");
            String name = rs.getString("Name");
            String value = rs.getString("Value");
            return Maps.immutableEntry(eventId, new NameValue(name, value));
        }, Functions.identity());

        for(SmartNotificationEvent event: events){
            Collection<NameValue> eventParams = values.get(event.getEventId());
            eventParams.forEach(param -> {
                event.addParameters(param.name, param.value);
            });
        }  
    }
    
    @Override
    public SearchResults<SmartNotificationEventData> getDeviceDataMonitorEventData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction, Range<DateTime> dateRange, int monitorId) {
        DateTime from = dateRange.getMin().withZone(timeZone);
        DateTime to = dateRange.getMax().withZone(timeZone);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        if (sortBy == null) {
            sortBy = SortBy.TIMESTAMP;
        }
        if (direction == null) {
            direction = Direction.desc;
        }
        
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        boolean isOracle = databaseVendor.isOracle();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT sne.Timestamp As Timestamp, sne.EventId, sne.Type, snep.paoId As DeviceId, snep.MonitorState As Status, snep.MonitorId, ypo.PAOName As DeviceName ");
        sql.append("FROM SmartNotificationEvent sne");
        sql.append("    INNER JOIN ("); 
        sql.append("        SELECT * FROM (");
        sql.append("            SELECT EventId, Name, Value FROM SmartNotificationEventParam");
        sql.append("        ) snep");
        if (isOracle) {
            sql.append("        PIVOT ( Max(Value) FOR Name IN ('paoId' AS paoId, 'MonitorState' AS MonitorState, 'MonitorId' AS MonitorId)) P");
        } 
        else {
            sql.append("        PIVOT ( Max(Value) FOR Name IN (paoId, MonitorState, MonitorId)) P ");
        }            
        sql.append("        ) snep ON sne.EventId = snep.EventId");
        sql.append("JOIN YukonPAObject ypo ON snep.paoId = ypo.PAObjectID");
        sql.append("WHERE sne.Type").eq_k(SmartNotificationEventType.DEVICE_DATA_MONITOR);
        sql.append("    AND sne.Timestamp").gte(from);
        sql.append("    AND sne.Timestamp").lt(to);
        sql.append("    AND snep.MonitorId").eq_k(monitorId);
        sql.append("ORDER BY").append(sortBy.getDbString()).append(direction);
        
        PagingResultSetExtractor<SmartNotificationEventData> rse = new PagingResultSetExtractor<>(start, count, createDeviceDataMonitorEventDetailRowMapper);
        jdbcTemplate.query(sql, rse);
        SearchResults<SmartNotificationEventData> retVal = new SearchResults<>();
        retVal.setBounds(start, count, getDeviceDataMonitorEventDetailCount(from, to, monitorId));
        retVal.setResultList(rse.getResultList());
        return retVal;
    }
    
    @Override
    public int getDeviceDataMonitorEventDetailCount(DateTime from, DateTime to, int monitorId) {
        
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        boolean isOracle = databaseVendor.isOracle();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM SmartNotificationEvent sne");
        sql.append("    INNER JOIN ("); 
        sql.append("        SELECT * FROM (");
        sql.append("            SELECT EventId, Name, Value FROM SmartNotificationEventParam");
        sql.append("        ) snep");
        if (isOracle) {
            sql.append("        PIVOT ( Max(Value) FOR Name IN ('paoId' AS paoId, 'MonitorState' AS MonitorState, 'MonitorId' AS MonitorId)) P");
        } 
        else {
            sql.append("        PIVOT ( Max(Value) FOR Name IN (paoId, MonitorState, MonitorId)) P ");
        }            
        sql.append("        ) snep ON sne.EventId = snep.EventId");
        sql.append("JOIN YukonPAObject ypo ON snep.paoId = ypo.PAObjectID");
        sql.append("WHERE sne.Type").eq_k(SmartNotificationEventType.DEVICE_DATA_MONITOR);
        sql.append("    AND sne.Timestamp").gte(from);
        sql.append("    AND sne.Timestamp").lt(to);
        sql.append("    AND snep.MonitorId").eq_k(monitorId);
        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public SearchResults<SmartNotificationEventData> getInfrastructureWarningEventData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction, Range<DateTime> dateRange, List<PaoType> typeFilter) {
        DateTime from = dateRange.getMin().withZone(timeZone);
        DateTime to = dateRange.getMax().withZone(timeZone);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        if (sortBy == null) {
            sortBy = SortBy.TIMESTAMP;
        }
        if (direction == null) {
            direction = Direction.desc;
        }
        
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        boolean isOracle = databaseVendor.isOracle();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT sne.Timestamp As Timestamp, sne.EventId, sne.Type, snep.paoId As DeviceId, snep.WarningType As Status, snep.WarningSeverity As Severity, "
                + "snep.Argument1 As Argument1, snep.Argument2 As Argument2, snep.Argument3 As Argument3, ypo.Type AS DeviceType, ypo.PAOName As DeviceName");
        sql.append("FROM SmartNotificationEvent sne");
        sql.append("    INNER JOIN ("); 
        sql.append("        SELECT * FROM (");
        sql.append("            SELECT EventId, Name, Value FROM SmartNotificationEventParam");
        sql.append("        ) snep");
        if (isOracle) {
            sql.append("        PIVOT ( Max(Value) FOR Name IN ('paoId' AS paoId, 'WarningType' AS WarningType, 'WarningSeverity' As WarningSeverity, "
                    + "'Argument1' As Argument1, 'Argument2' As Argument2, 'Argument3' As Argument3)) P");
        } 
        else {
            sql.append("        PIVOT ( Max(Value) FOR Name IN (paoId, WarningType, WarningSeverity, Argument1, Argument2, Argument3)) P");
        }
        sql.append("        ) snep ON sne.EventId = snep.EventId");
        sql.append("JOIN YukonPAObject ypo ON snep.paoId = ypo.PAObjectID");
        sql.append("WHERE sne.Type").eq_k(SmartNotificationEventType.INFRASTRUCTURE_WARNING);
        sql.append("    AND Timestamp").gte(from);
        sql.append("    AND Timestamp").lt(to);
        sql.append("    AND ypo.Type").in(typeFilter);
        sql.append("ORDER BY").append(sortBy.getDbString()).append(direction);
        
        PagingResultSetExtractor<SmartNotificationEventData> rse = new PagingResultSetExtractor<>(start, count, createInfrastructureWarningEventDetailRowMapper);
        jdbcTemplate.query(sql, rse);
        SearchResults<SmartNotificationEventData> retVal = new SearchResults<>();
        retVal.setBounds(start, count, getInfrastructureWarningEventDetailCount(from, to, typeFilter));
        retVal.setResultList(rse.getResultList());
        return retVal;
    }
    
    @Override
    public int getInfrastructureWarningEventDetailCount(DateTime from, DateTime to, List<PaoType> typeFilter) {
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        boolean isOracle = databaseVendor.isOracle();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM SmartNotificationEvent sne");
        sql.append("    INNER JOIN ("); 
        sql.append("        SELECT * FROM (");
        sql.append("            SELECT EventId, Name, Value FROM SmartNotificationEventParam");
        sql.append("        ) snep");
        if (isOracle) {
            sql.append("        PIVOT ( Max(Value) FOR Name IN ('paoId' AS paoId, 'WarningType' AS WarningType)) P");
        } 
        else {
            sql.append("        PIVOT ( Max(Value) FOR Name IN (paoId, WarningType)) P");
        }
        sql.append("        ) snep ON sne.EventId = snep.EventId");
        sql.append("JOIN YukonPAObject ypo ON snep.paoId = ypo.PAObjectID");
        sql.append("WHERE sne.Type").eq_k(SmartNotificationEventType.INFRASTRUCTURE_WARNING);
        sql.append("    AND Timestamp").gte(from);
        sql.append("    AND Timestamp").lt(to);
        sql.append("    AND ypo.Type").in(typeFilter);
        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public List<SmartNotificationEvent> getEventsByTypeAndDate(SmartNotificationEventType eventType, Range<Instant> range) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventId, Timestamp, GroupProcessTime, ImmediateProcessTime");
        sql.append("FROM  SmartNotificationEvent sne");
        sql.append("WHERE Type").eq_k(eventType);
        sql.append("AND Timestamp").gte(range.getMin());
        sql.append("AND Timestamp").lt(range.getMax());
        List<SmartNotificationEvent> events = jdbcTemplate.query(sql, eventMapper);
        if (!events.isEmpty()) {
            addParameters(events);
        }
        return events;
    }

    @Override
    public SearchResults<SmartNotificationEventData> getWatchdogWarningEventData(DateTimeZone timeZone, PagingParameters paging, SortBy sortBy, Direction direction, Range<DateTime> dateRange) {
        DateTime from = dateRange.getMin().withZone(timeZone);
        DateTime to = dateRange.getMax().withZone(timeZone);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        if (sortBy == null) {
            sortBy = SortBy.TIMESTAMP;
        }
        if (direction == null) {
            direction = Direction.desc;
        }
        
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        boolean isOracle = databaseVendor.isOracle();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT sne.Timestamp As Timestamp, sne.EventId, sne.Type, snep.WarningType As WarningType, snep.Status As Status ");
        sql.append("FROM SmartNotificationEvent sne");
        sql.append("    INNER JOIN ("); 
        sql.append("        SELECT * FROM (");
        sql.append("            SELECT EventId, Name, Value FROM SmartNotificationEventParam");
        sql.append("        ) snep");
        if (isOracle) {
            sql.append("        PIVOT ( Max(Value) FOR Name IN ('WarningType' AS WarningType, 'Status' As Status)) P");
        } 
        else {
            sql.append("        PIVOT ( Max(Value) FOR Name IN (WarningType, Status)) P");
        }
        sql.append("        ) snep ON sne.EventId = snep.EventId ");
        sql.append("WHERE sne.Type").eq_k(SmartNotificationEventType.YUKON_WATCHDOG);
        sql.append("    AND Timestamp").gte(from);
        sql.append("    AND Timestamp").lt(to);
        sql.append("ORDER BY").append(sortBy.getDbString()).append(direction);
        
        PagingResultSetExtractor<SmartNotificationEventData> rse = new PagingResultSetExtractor<>(start, count, createWatchdogWarningEventDetailRowMapper);
        jdbcTemplate.query(sql, rse);
        SearchResults<SmartNotificationEventData> retVal = new SearchResults<>();
        retVal.setBounds(start, count, getWatchdogWarningEventDetailCount(from, to));
        retVal.setResultList(rse.getResultList());
        return retVal;
    }

    @Override
    public int getWatchdogWarningEventDetailCount(DateTime from, DateTime to) {
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        boolean isOracle = databaseVendor.isOracle();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM SmartNotificationEvent sne");
        sql.append("    INNER JOIN ("); 
        sql.append("        SELECT * FROM (");
        sql.append("            SELECT EventId, Name, Value FROM SmartNotificationEventParam");
        sql.append("        ) snep");
        if (isOracle) {
            sql.append("        PIVOT ( Max(Value) FOR Name IN ('WarningType' AS WarningType)) P");
        } 
        else {
            sql.append("        PIVOT ( Max(Value) FOR Name IN (WarningType)) P");
        }
        sql.append("        ) snep ON sne.EventId = snep.EventId ");
        sql.append("WHERE sne.Type").eq_k(SmartNotificationEventType.YUKON_WATCHDOG);
        sql.append("    AND Timestamp").gte(from);
        sql.append("    AND Timestamp").lt(to);
        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public SearchResults<SmartNotificationEventData> getAssetImportEventData(DateTimeZone timeZone,
            PagingParameters paging, SortBy sortBy, Direction direction, Range<DateTime> dateRange,
            AssetImportResultType assetImportResultType) {
        DateTime from = dateRange.getMin().withZone(timeZone);
        DateTime to = dateRange.getMax().withZone(timeZone);

        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();

        if (sortBy == null) {
            sortBy = SortBy.TIMESTAMP;
        }
        if (direction == null) {
            direction = Direction.desc;
        }

        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        boolean isOracle = databaseVendor.isOracle();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(
            "SELECT sne.Timestamp, sne.EventId, snep.jobGroupId, snep.jobName, snep.successFileCount, snep.filesWithError");
        sql.append("FROM SmartNotificationEvent sne");
        sql.append("    INNER JOIN (");
        sql.append("        SELECT * FROM (");
        sql.append("            SELECT EventId, Name, Value FROM SmartNotificationEventParam");
        sql.append("        ) snep");
        if (isOracle) {
            sql.append(
                "        PIVOT ( Max(Value) FOR Name IN ('jobGroupId' AS jobGroupId, 'jobName' AS jobName, 'importType' AS importType, 'successFileCount' AS successFileCount, 'filesWithError' AS filesWithError)) P");
        } else {
            sql.append(
                "        PIVOT ( Max(Value) FOR Name IN (jobGroupId, jobName, successFileCount, filesWithError)) P ");
        }
        sql.append("        ) snep ON sne.EventId = snep.EventId");
        sql.append("WHERE sne.Type").eq_k(SmartNotificationEventType.ASSET_IMPORT);
        sql.append("    AND sne.Timestamp").gte(from);
        sql.append("    AND sne.Timestamp").lt(to);
        if (sortBy != SortBy.FILE_ERROR_COUNT) {
            sql.append("ORDER BY").append(sortBy.getDbString()).append(direction);
        }
        PagingResultSetExtractor<SmartNotificationEventData> rse =
            new PagingResultSetExtractor<>(start, count, createAssetImportEventDetailRowMapper);
        jdbcTemplate.query(sql, rse);

        List<SmartNotificationEventData> filteredResults = rse.getResultList();
        if (AssetImportResultType.IMPORTS_WITH_ERRORS == assetImportResultType) {
            filteredResults = rse.getResultList().stream()
                                                 .filter(result -> result.getFileErrorCount() > 0)
                                                 .collect(Collectors.toList());
        }
        if (sortBy == SortBy.FILE_ERROR_COUNT) {
            if (direction == Direction.asc) {
                filteredResults.sort(fileFailureCountComparator);
            } else {
                filteredResults.sort(fileFailureCountComparator.reversed());
            }
        }
        SearchResults<SmartNotificationEventData> retVal = new SearchResults<>();
        retVal.setBounds(start, count, getAssetImportEventCount(from, to, assetImportResultType));
        retVal.setResultList(filteredResults);
        return retVal;
    }

    @Override
    public int getAssetImportEventCount(DateTime from, DateTime to, AssetImportResultType assetImportResultType) {
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        boolean isOracle = databaseVendor.isOracle();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT sne.EventId, snep.filesWithError");
        sql.append("FROM SmartNotificationEvent sne");
        sql.append("    INNER JOIN (");
        sql.append("        SELECT * FROM (");
        sql.append("            SELECT EventId, Name, Value FROM SmartNotificationEventParam");
        sql.append("        ) snep");
        if (isOracle) {
            sql.append(
                "        PIVOT ( Max(Value) FOR Name IN ('jobGroupId' AS jobGroupId, 'jobName' AS jobName, 'importType' AS importType, 'successFileCount' AS successFileCount, 'filesWithError' AS filesWithError)) P");
        } else {
            sql.append(
                "        PIVOT ( Max(Value) FOR Name IN (jobGroupId, jobName, successFileCount, filesWithError)) P ");
        }
        sql.append("        ) snep ON sne.EventId = snep.EventId");
        sql.append("WHERE sne.Type").eq_k(SmartNotificationEventType.ASSET_IMPORT);
        sql.append("    AND sne.Timestamp").gte(from);
        sql.append("    AND sne.Timestamp").lt(to);
        List<SmartNotificationEventData> list =
            jdbcTemplate.query(sql, new YukonRowMapper<SmartNotificationEventData>() {
                @Override
                public SmartNotificationEventData mapRow(YukonResultSet rs) throws SQLException {
                    SmartNotificationEventData eventData = new SmartNotificationEventData();
                    eventData.setEventId(rs.getInt("EventId"));
                    if (StringUtils.isNoneBlank(rs.getString("filesWithError"))) {
                        StringTokenizer tokenizer = new StringTokenizer(rs.getString("filesWithError"));
                        eventData.setFileErrorCount(tokenizer.countTokens());
                    }
                    return eventData;
                }
            });
        List<SmartNotificationEventData> filteredList = list;
        if (AssetImportResultType.IMPORTS_WITH_ERRORS == assetImportResultType) {
            filteredList = list.stream().filter(result -> result.getFileErrorCount() > 0)
                                        .collect(Collectors.toList());
        }
        return filteredList.size();
    }
}
