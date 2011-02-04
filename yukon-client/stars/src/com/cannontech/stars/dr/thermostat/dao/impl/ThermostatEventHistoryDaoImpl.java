package com.cannontech.stars.dr.thermostat.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.thermostat.dao.ThermostatEventHistoryDao;
import com.cannontech.stars.dr.thermostat.model.ThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatEventType;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;

public class ThermostatEventHistoryDaoImpl implements ThermostatEventHistoryDao, InitializingBean {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<ThermostatEvent> thermostatEventTemplate;
    private ThermostatService thermostatService;
    
    @Override
    public List<ThermostatEvent> getEventsByThermostatIds(List<Integer> thermostatIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventId, EventType, UserName, EventTime, ThermostatId, ManualTemp, ManualMode, ManualFan, ManualHold, ScheduleId, ScheduleMode");
        sql.append("FROM ThermostatEventHistory");
        sql.append("WHERE ThermostatId").in(thermostatIds);
        sql.append("ORDER BY EventTime DESC");
        
        List<ThermostatEvent> eventList = yukonJdbcTemplate.query(sql, thermostatEventHistoryRowMapper);
        insertThermostatAndScheduleNames(eventList);
        return eventList;
    }
    
    @Override
    public List<ThermostatEvent> getLastNEventsByThermostatIds(List<Integer> thermostatIds, int numberOfEvents) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventId, EventType, UserName, EventTime, ThermostatId, ManualTemp, ManualMode, ManualFan, ManualHold, ScheduleId, ScheduleMode");
        sql.append("FROM ThermostatEventHistory");
        sql.append("WHERE ThermostatId").in(thermostatIds);
        sql.append("ORDER BY EventTime DESC");
        
        List<ThermostatEvent> eventList = yukonJdbcTemplate.queryForLimitedResults(sql, thermostatEventHistoryRowMapper, numberOfEvents);
        insertThermostatAndScheduleNames(eventList);
        return eventList;
    }
    
    @Override
    public void logEvent(ThermostatEvent event) {
        thermostatEventTemplate.insert(event);
    }
    
    @Override
    public void logRestoreEvent(LiteYukonUser user, int thermostatId) {
        ThermostatEvent event = new ThermostatEvent();
        event.setUserName(user.getUsername());
        event.setThermostatId(thermostatId);
        event.setEventType(ThermostatEventType.RESTORE);
        event.setEventTime(new DateTime());
        logEvent(event);
    }
    
    @Override
    public void logScheduleEvent(LiteYukonUser user, int thermostatId, int scheduleId, ThermostatScheduleMode scheduleMode){
        ThermostatEvent event = new ThermostatEvent();
        event.setUserName(user.getUsername());
        event.setThermostatId(thermostatId);
        event.setEventType(ThermostatEventType.SCHEDULE);
        event.setEventTime(new DateTime());
        event.setScheduleId(scheduleId);
        event.setScheduleMode(scheduleMode);
        logEvent(event);
    }
    
    @Override
    public void logManualEvent(LiteYukonUser user, 
                                  int thermostatId, 
                                  int temperatureInF, 
                                  ThermostatMode thermostatMode, 
                                  ThermostatFanState fan, 
                                  boolean hold) {
        
        ThermostatEvent event = new ThermostatEvent();
        event.setUserName(user.getUsername());
        event.setThermostatId(thermostatId);
        event.setEventType(ThermostatEventType.MANUAL);
        event.setEventTime(new DateTime());
        event.setManualFan(fan);
        event.setManualHold(hold);
        event.setManualMode(thermostatMode);
        event.setManualTemp(temperatureInF);
        logEvent(event);
    }
    
    private void insertThermostatAndScheduleNames(List<ThermostatEvent> events) {
        for(ThermostatEvent event : events) {
            String thermostatName = thermostatService.getThermostatNameFromId(event.getThermostatId());
            event.setThermostatName(thermostatName);
            
            if(event.getEventType() == ThermostatEventType.SCHEDULE){
                String scheduleName = thermostatService.getAccountThermostatScheduleNameFromId(event.getScheduleId());
                event.setScheduleName(scheduleName);
            }
        }
    }
    
    private final YukonRowMapper<ThermostatEvent> thermostatEventHistoryRowMapper = new YukonRowMapper<ThermostatEvent>() {
        @Override
        public ThermostatEvent mapRow(YukonResultSet rs) throws SQLException {
            ThermostatEvent retVal = new ThermostatEvent();
            retVal.setEventId(rs.getInt("eventId"));
            retVal.setEventType(rs.getEnum("eventType", ThermostatEventType.class));
            retVal.setUserName(rs.getString("userName"));
            Date timestamp = rs.getDate("eventTime");
            retVal.setEventTime(new DateTime(timestamp));
            retVal.setThermostatId(rs.getInt("thermostatId"));
            retVal.setManualTemp(rs.getInt("manualTemp"));
            retVal.setManualMode(rs.getEnum("manualMode", ThermostatMode.class));
            retVal.setManualFan(rs.getEnum("manualFan", ThermostatFanState.class));
            retVal.setManualHold(rs.getEnum("manualHold", YNBoolean.class).getBoolean());
            retVal.setScheduleId(rs.getInt("scheduleId"));
            retVal.setScheduleMode(rs.getEnum("scheduleMode", ThermostatScheduleMode.class));
            return retVal;
        }
    };
    
    private FieldMapper<ThermostatEvent> thermostatEventFieldMapper = new FieldMapper<ThermostatEvent>() {
        public void extractValues(MapSqlParameterSource msps, ThermostatEvent thermostatEvent) {
            msps.addValue("EventType", thermostatEvent.getEventType());
            msps.addValue("UserName", thermostatEvent.getUserName());
            msps.addValue("EventTime", thermostatEvent.getEventTime().toDate());
            msps.addValue("ThermostatId", thermostatEvent.getThermostatId());
            msps.addValue("ManualTemp", thermostatEvent.getManualTemp());
            msps.addValue("ManualFan", thermostatEvent.getManualFan());
            msps.addValue("ManualMode", thermostatEvent.getManualMode());
            msps.addValue("ManualHold", YNBoolean.valueOf(thermostatEvent.isManualHold()));
            msps.addValue("ScheduleId", thermostatEvent.getScheduleId());
            msps.addValue("ScheduleMode", thermostatEvent.getScheduleMode());
        }
        public Number getPrimaryKey(ThermostatEvent thermostatEvent) {
            return thermostatEvent.getEventId();
        }
        public void setPrimaryKey(ThermostatEvent thermostatEvent, int value) {
            thermostatEvent.setEventId(value);
        }
    };
    
    public void afterPropertiesSet() throws Exception {
        thermostatEventTemplate = new SimpleTableAccessTemplate<ThermostatEvent>(yukonJdbcTemplate, nextValueHelper);
        thermostatEventTemplate.setTableName("ThermostatEventHistory");
        thermostatEventTemplate.setPrimaryKeyField("EventId");
        thermostatEventTemplate.setFieldMapper(thermostatEventFieldMapper);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setThermostatService(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
    }
}
