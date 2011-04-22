package com.cannontech.thirdparty.digi.dao.impl;

import java.util.Date;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.thirdparty.model.ZigbeeEventAction;

public class DigiControlEventDao {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    public void createNewEventMapping(int eventId, int groupId, Date startTime) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink p = sql.insertInto("DigiControlEventMapping");
        p.addValue("EventId", eventId);
        p.addValue("GroupId", groupId);
        p.addValue("StartTime", startTime);
        // DeviceCount will be added after the Digi Call
        
        // LMControlHistoryId will be set after Dispatch acknowledges us
        
        yukonJdbcTemplate.update(sql);
    }
    
    public void updateDeviceCount(int eventId, int deviceCount) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE DigiControlEventMapping");
        sql.append("SET DeviceCount").eq(deviceCount);
        sql.append("WHERE EventId").eq(eventId);
        
        yukonJdbcTemplate.update(sql);        
    }
    
    public void associateControlHistory(int eventId, int controlHistoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE DigiControlEventMapping");
        sql.append("SET LMControlHistoryId").eq(controlHistoryId);
        sql.append("WHERE EventId").eq(eventId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    public int findCurrentEventId(int groupId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT i.EventId");
        sql.append("FROM (");
        sql.append(  "SELECT EventId, ROW_NUMBER() OVER (ORDER BY StartTime desc) rn");
        sql.append(  "FROM DigiControlEventMapping");
        sql.append(  "WHERE GroupId").eq(groupId);
        sql.append(") i");
        sql.append("WHERE i.rn = 1");

        try {
            return yukonJdbcTemplate.queryForInt(sql);           
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Digi Event not found for Group with ID: " + groupId);
        }
    }
    
    public void insertControlEvent(int eventId, Instant eventTime, int deviceId, ZigbeeEventAction action) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink p = sql.insertInto("ZBControlEvent");
        p.addValue("EventId", eventId);
        p.addValue("EventTime", eventTime);
        p.addValue("DeviceId", deviceId);
        p.addValue("Action", action);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
