package com.cannontech.thirdparty.digi.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.thirdparty.IntegrationType;
import com.cannontech.thirdparty.digi.dao.ZigbeeControlEventDao;
import com.cannontech.thirdparty.digi.model.ZigbeeControlledDevice;

public class ZigbeeControlEventDaoImpl implements ZigbeeControlEventDao {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    private YukonRowMapper<ZigbeeControlledDevice> deviceControlEventRowMapper = new YukonRowMapper<ZigbeeControlledDevice>() {

        @Override
        public ZigbeeControlledDevice mapRow(YukonResultSet rs) throws SQLException {
            ZigbeeControlledDevice event = new ZigbeeControlledDevice();
            event.setEventId(rs.getInt("ZBControlEventId"));
            event.setDeviceName(rs.getString("DeviceName"));
            event.setLoadGroupName(rs.getString("LoadGroupName"));
            event.setAck("Y".equals(rs.getString("DeviceAck")));

            Date startTime = rs.getDate("StartTime");
            if (startTime != null) {
                event.setStart(new Instant(startTime));
            }            
            
            Date stopTime = rs.getDate("StopTime");
            if (stopTime != null) {
                event.setStop(new Instant(stopTime));
            }
            String canceled = rs.getString("Canceled");
            if (canceled != null) {
                event.setCanceled("Y".equals(canceled));
            } else {
                event.setCanceled(null);
            }

            return event;
        }
    };
    
    public List<ZigbeeControlledDevice> getDeviceEvents(Instant startDate, Instant stopDate) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT ZBCED.ZBControlEventId, YP.PaOName as DeviceName, YP2.PaoName as LoadGroupName, ZBCED.DeviceAck, ZBCED.StartTime, ZBCED.StopTime, ZBCED.Canceled");
        sql.append("FROM ZBControlEventDevice ZBCED");
        sql.append("JOIN YukonPaObject YP on YP.PaObjectID = ZBCED.DeviceId");
        sql.append("JOIN ZBControlEvent ZBCE on ZBCE.ZBControlEventId = ZBCED.ZBControlEventId");
        sql.append("JOIN YukonPAObject YP2 on YP2.PAObjectID = ZBCE.GroupId");
        sql.append("WHERE ZBCE.StartTime").gte(startDate);
        sql.append(  "AND ZBCE.StartTime").lte(stopDate);
        sql.append("ORDER BY ZBCED.ZBControlEventId, LoadGroupName");
        return yukonJdbcTemplate.query(sql, deviceControlEventRowMapper);           
    }
    
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
        
        SqlParameterSink params = sql.update("ZBControlEventDevice");
        params.addValue("DeviceAck", YNBoolean.valueOf(ack));
        
        sql.append("WHERE ZBControlEventId").eq(eventId);
        sql.append(  "AND DeviceId").eq(deviceId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateDeviceStartTime(Instant startTime, int eventId, int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update("ZBControlEventDevice");
        params.addValue("StartTime", startTime);

        sql.append("WHERE ZBControlEventId").eq(eventId);
        sql.append(  "AND DeviceId").eq(deviceId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateDeviceStopTime(Instant stopTime, int eventId, int deviceId, boolean canceled) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update("ZBControlEventDevice");
        params.addValue("StopTime", stopTime);
        params.addValue("Canceled", YNBoolean.valueOf(canceled));

        sql.append("WHERE ZBControlEventId").eq(eventId);
        sql.append(  "AND DeviceId").eq(deviceId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
