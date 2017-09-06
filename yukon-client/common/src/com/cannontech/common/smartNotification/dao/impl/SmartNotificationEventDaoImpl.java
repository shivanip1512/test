package com.cannontech.common.smartNotification.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
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
    public void save(SmartNotificationEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("SmartNotificationEvent");
        int id = nextValueHelper.getNextValue("SmartNotificationEvent");
        params.addValue("EventId", nextValueHelper.getNextValue("SmartNotificationEvent"));
        params.addValue("Type", event.getType());
        jdbcTemplate.update(sql);
        saveEventParameters(id, event.getParameters());
    }
    
    private void saveEventParameters(int id, Map<String, Object> params) {
        List<List<Object>> values = new ArrayList<>();
        params.forEach((k, v) -> {
            values.add(Lists.newArrayList(id, k, v));
        });
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.batchInsertInto("SmartNotificationEventParameter")
           .columns("EventId", "Name", "Value")
           .values(values);
        jdbcTemplate.yukonBatchUpdate(sql);
    }
}
