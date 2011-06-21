package com.cannontech.thirdparty.digi.dao.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.thirdparty.IntegrationType;
import com.cannontech.thirdparty.digi.dao.ZigbeeControlEventDao;

public class ZigbeeControlEventDaoImpl implements ZigbeeControlEventDao {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public void createNewEventMapping(int eventId, int groupId, Instant startTime) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink p = sql.insertInto("ZBControlEvent");
        p.addValue("ZBControlEventId", eventId);
        p.addValue("IntegrationType", IntegrationType.DIGI);
        p.addValue("GroupId", groupId);
        p.addValue("StartTime", startTime);
        // DeviceCount will be added after the Digi Call
        
        // LMControlHistoryId will be set after Dispatch acknowledges us
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void associateControlHistory(int eventId, int controlHistoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE ZBControlEvent");
        sql.append("SET LMControlHistoryId").eq(controlHistoryId);
        sql.append("WHERE ZBControlEventId").eq(eventId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public int findCurrentEventId(int groupId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT i.ZBControlEventId");
        sql.append("FROM (");
        sql.append(  "SELECT ZBControlEventId, ROW_NUMBER() OVER (ORDER BY StartTime desc) rn");
        sql.append(  "FROM ZBControlEvent");
        sql.append(  "WHERE GroupId").eq(groupId);
        sql.append(") i");
        sql.append("WHERE i.rn = 1");

        try {
            return yukonJdbcTemplate.queryForInt(sql);           
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Digi Event not found for Group with ID: " + groupId);
        }
    }
    
    @Override
    public void insertDeviceControlEvent(int eventId, int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink p = sql.insertInto("ZBControlEventDevice");
        p.addValue("ZBControlEventId", eventId);
        p.addValue("DeviceId", deviceId);
        p.addValue("DeviceAck","N");
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateDeviceAck(boolean ack, int eventId, int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE ZBControlEventDevice");
        sql.append("SET DeviceAck").eq(ack);
        sql.append("WHERE ZBControlEventId").eq(eventId);
        sql.append(  "AND DeviceId").eq(deviceId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateDeviceStartTime(Instant startTime, int eventId, int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE ZBControlEventDevice");
        sql.append("SET StartTime").eq(startTime);
        sql.append("WHERE ZBControlEventId").eq(eventId);
        sql.append(  "AND DeviceId").eq(deviceId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateDeviceStopTime(Instant stopTime, int eventId, int deviceId, boolean canceled) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE ZBControlEventDevice");
        sql.append("SET StopTime").eq(stopTime);
        sql.append(", canceled").eq( canceled ? "Y":"N" );
        sql.append("WHERE ZBControlEventId").eq(eventId);
        sql.append(  "AND DeviceId").eq(deviceId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
