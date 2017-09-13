package com.cannontech.common.smartNotification.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

/**
 * This dao handles saving and loading Smart Notification events.
 */
public class SmartNotificationEventDaoImpl implements SmartNotificationEventDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    
    @Transactional
    @Override
    public void save(SmartNotificationEventType type, List<SmartNotificationEvent> events) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        Map<Integer, Map<String, Object>> params = new HashMap<>();
        List<List<Object>> values = events.stream()
                .map(event -> {
                    int id = nextValueHelper.getNextValue("SmartNotificationEvent");
                    params.put(id, event.getParameters());
                List<Object> row = Lists.newArrayList(id, type, event.getTimestamp());
                    return row;
                }).collect(Collectors.toList());
        
        sql.batchInsertInto("SmartNotificationEvent")
           .columns("EventId", "Type", "Timestamp")
           .values(values);
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
        sql.batchInsertInto("SmartNotificationEventParam")
           .columns("EventId", "Name", "Value")
           .values(values);
        jdbcTemplate.yukonBatchUpdate(sql);
    }
    
    @Override
    public Map<Integer, SmartNotificationEvent> getEvents(SmartNotificationEventType type, Range<Instant> range){
        Map<Integer, SmartNotificationEvent> results = new HashMap<>();
       /* SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventId, Timestamp");
        sql.append("FROM  SmartNotificationEvent sne");
        sql.append("WHERE EventType").eq_k(type);
        sql.append("AND ProcessedTime IS NULL");
        RawPointHistoryDaoImpl.appendTimeStampClause(sql, range, "sne");
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                Integer id = rs.getInt("EventId");
                Instant timestamp = rs.getInstant("Timestamp");
                results.put(id, new SmartNotificationEvent(type, timestamp) {
                    @Override
                    public Map<String, Object> getParameters() {
                        return null;
                    }
                });
            }
        });*/
        return results; 
    } 
}
