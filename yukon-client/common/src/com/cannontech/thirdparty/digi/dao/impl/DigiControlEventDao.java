package com.cannontech.thirdparty.digi.dao.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class DigiControlEventDao {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    public void createNewEvent(int eventId, int groupId, Date startTime) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink p = sql.insertInto("DigiControlEvent");
        p.addValue("DigiEventId", eventId);
        p.addValue("GroupId", groupId);
        p.addValue("StartTime", startTime);
        // DeviceCount will be added after the Digi Call
        
        // LMControlHistoryId will be set after Dispatch acknowledges us
        
        yukonJdbcTemplate.update(sql);
    }
    
    public void updateDeviceCount(int eventId, int deviceCount) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE DigiControlEvent");
        sql.append("SET DeviceCount").eq(deviceCount);
        sql.append("WHERE DigiEventId").eq(eventId);
        
        yukonJdbcTemplate.update(sql);        
    }
    
    public void associateControlHistory(int eventId, int controlHistoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE DigiControlEvent");
        sql.append("SET LMControlHistoryId").eq(controlHistoryId);
        sql.append("WHERE DigiEventId").eq(eventId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    public int findCurrentEventId(int groupId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DigiEventId");
        sql.append("FROM DigiControlEvent");
        sql.append("WHERE GroupId").eq(groupId);
        sql.append("AND StartTime = (");
        sql.append("  SELECT MAX(StartTime)");
        sql.append("  FROM DigiControlEvent");
        sql.append("  WHERE GroupId").eq(groupId);
        sql.append("  )");
        
        try {
            return yukonJdbcTemplate.queryForInt(sql);           
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Digi Event not found for Group with ID: " + groupId);
        }
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
