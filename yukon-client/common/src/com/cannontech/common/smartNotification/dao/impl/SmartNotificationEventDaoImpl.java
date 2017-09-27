package com.cannontech.common.smartNotification.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
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
    private ChunkingSqlTemplate chunkingTemplate;
    
    @PostConstruct
    public void init() {
        chunkingTemplate = new ChunkingSqlTemplate(jdbcTemplate);
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
    
    private final class NameValue {
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
}
