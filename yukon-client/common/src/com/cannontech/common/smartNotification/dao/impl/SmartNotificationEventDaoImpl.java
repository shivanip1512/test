package com.cannontech.common.smartNotification.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
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
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
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
    public void markEventsAsProcessed(List<SmartNotificationEvent> events, Instant processedTime) {
        if (!events.isEmpty()) {
            List<Integer> eventsIds = events.stream().map(e -> e.getEventId()).collect(Collectors.toList());
            class Updater implements SqlFragmentGenerator<Integer> {
                private Instant processedTime;

                public Updater(Instant processedTime) {
                    this.processedTime = processedTime;
                }

                @Override
                public SqlFragmentSource generate(List<Integer> eventsIds) {
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    sql.append("UPDATE SmartNotificationEvent");
                    sql.append("SET ProcessedTime").eq(processedTime);
                    sql.append("WHERE EventId").in(eventsIds);
                    return sql;
                }
            }
            chunkingTemplate.update(new Updater(processedTime), eventsIds);
        }
    }

    @Override
    public Map<Integer, SmartNotificationEvent> getEvents(SmartNotificationEventType type, Range<Instant> range) {
        Map<Integer, SmartNotificationEvent> results = new HashMap<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventId, Timestamp");
        sql.append("FROM  SmartNotificationEvent sne");
        sql.append("WHERE EventType").eq_k(type);
        sql.append("AND ProcessedTime IS NULL");
      //  sql.appendTimeStampClause(range, "sne");
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                Integer id = rs.getInt("EventId");
                Instant timestamp = rs.getInstant("Timestamp");
                results.put(id, new SmartNotificationEvent(timestamp));
            }
        });
        addParameters(results);
        return results;
    }

    private void addParameters(Map<Integer, SmartNotificationEvent> results) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT EventId, Name, Value");
                sql.append("FROM SmartNotificationEvent");
                sql.append("WHERE EventId").in(subList);
                return sql;
            }
        };
        class NameValue {
            public NameValue(String name, String value) {
                this.name = name;
                this.value = value;
            }

            String name;
            String value;
        }

        Multimap<Integer, NameValue> values = template.multimappedQuery(sqlGenerator, results.keySet(), rs -> {
            Integer behaviorId = rs.getInt("EventId");
            String name = rs.getString("Name");
            String value = rs.getString("Value");
            return Maps.immutableEntry(behaviorId, new NameValue(name, value));
        }, Functions.identity());

        results.keySet().forEach(eventId -> {
            values.get(eventId).forEach(param -> {
                results.get(eventId).addParameters(param.name, param.value);
            });
        });
    }
}
