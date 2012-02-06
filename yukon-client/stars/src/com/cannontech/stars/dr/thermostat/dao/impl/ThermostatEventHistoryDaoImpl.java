package com.cannontech.stars.dr.thermostat.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatEventHistoryDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ManualThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.RestoreThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ScheduleThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatEventType;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;

public class ThermostatEventHistoryDaoImpl implements ThermostatEventHistoryDao, InitializingBean {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<ThermostatEvent> thermostatEventTemplate;
    private ThermostatService thermostatService;
    private AccountThermostatScheduleDao accountThermostatScheduleDao;
    
    @Override
    public List<ThermostatEvent> getEventsByThermostatIds(List<Integer> thermostatIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventId, EventType, UserName, EventTime, ThermostatId, ManualMode, ManualFan, ManualHold, ScheduleId, ScheduleMode, ManualCoolTemp, ManualHeatTemp");
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
        sql.append("SELECT EventId, EventType, UserName, EventTime, ThermostatId, ManualMode, ManualFan, ManualHold, ScheduleId, ScheduleMode, ManualCoolTemp, ManualHeatTemp");
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
        RestoreThermostatEvent event = new RestoreThermostatEvent();
        event.setUserName(user.getUsername());
        event.setThermostatId(thermostatId);
        event.setEventTime(new Instant());
        logEvent(event);
    }
    
    @Override
    public void logScheduleEvent(LiteYukonUser user, int thermostatId, int scheduleId, ThermostatScheduleMode scheduleMode){
        ScheduleThermostatEvent event = new ScheduleThermostatEvent();
        event.setUserName(user.getUsername());
        event.setThermostatId(thermostatId);
        event.setEventTime(new Instant());
        event.setScheduleId(scheduleId);
        event.setScheduleMode(scheduleMode);
        logEvent(event);
    }
    
    @Override
    public void logManualEvent(LiteYukonUser user, int thermostatId, ThermostatManualEvent event) {
        
        ManualThermostatEvent mte = new ManualThermostatEvent();
        mte.setUserName(user.getUsername());
        mte.setThermostatId(thermostatId);
        mte.setEventTime(new Instant());
        mte.setManualFan(event.getFanState());
        mte.setManualHold(event.isHoldTemperature());
        mte.setManualMode(event.getMode());
        mte.setManualCoolTemp(event.getPreviousCoolTemperature());
        mte.setManualHeatTemp(event.getPreviousHeatTemperature());
        logEvent(mte);
    }
    
    private void insertThermostatAndScheduleNames(List<ThermostatEvent> events) {
        for(ThermostatEvent event : events) {
            String thermostatName = thermostatService.getThermostatNameFromId(event.getThermostatId());
            event.setThermostatName(thermostatName);
            
            if(event instanceof ScheduleThermostatEvent){
                String scheduleName = null;
                ScheduleThermostatEvent scheduleEvent = (ScheduleThermostatEvent) event;
                try {
                    AccountThermostatSchedule ats = accountThermostatScheduleDao.getById(scheduleEvent.getScheduleId());
                    scheduleName = ats.getScheduleName();
                } catch(EmptyResultDataAccessException e) {
                    //leave schedule name as null string - schedule has been deleted
                }
                scheduleEvent.setScheduleName(scheduleName);
            }
        }
    }
    
    private final YukonRowMapper<ThermostatEvent> thermostatEventHistoryRowMapper = new YukonRowMapper<ThermostatEvent>() {
        @Override
        public ThermostatEvent mapRow(YukonResultSet rs) throws SQLException {
            ThermostatEvent retVal = null;
            
            ThermostatEventType eventType = rs.getEnum("eventType", ThermostatEventType.class);
            if(eventType == ThermostatEventType.SCHEDULE) {
                ScheduleThermostatEvent event = new ScheduleThermostatEvent();
                event.setScheduleId(rs.getInt("scheduleId"));
                event.setScheduleMode(rs.getEnum("scheduleMode", ThermostatScheduleMode.class));
                retVal = event;
            } else if(eventType == ThermostatEventType.MANUAL) {
                ManualThermostatEvent event = new ManualThermostatEvent();
                event.setManualCoolTemp(Temperature.fromFahrenheit(rs.getDouble("manualCoolTemp")));
                event.setManualHeatTemp(Temperature.fromFahrenheit(rs.getDouble("manualHeatTemp")));
                event.setManualMode(rs.getEnum("manualMode", ThermostatMode.class));
                event.setManualFan(rs.getEnum("manualFan", ThermostatFanState.class));
                event.setManualHold(rs.getEnum("manualHold", YNBoolean.class).getBoolean());
                retVal = event;
            } else if(eventType == ThermostatEventType.RESTORE) {
                retVal = new RestoreThermostatEvent();
            }
            retVal.setEventId(rs.getInt("eventId"));
            retVal.setUserName(rs.getString("userName"));
            retVal.setEventTime(rs.getInstant("eventTime"));
            retVal.setThermostatId(rs.getInt("thermostatId"));
            return retVal;
        }
    };
    
    private FieldMapper<ThermostatEvent> thermostatEventFieldMapper = new FieldMapper<ThermostatEvent>() {
        public void extractValues(MapSqlParameterSource msps, ThermostatEvent thermostatEvent) {
            msps.addValue("UserName", thermostatEvent.getUserName());
            msps.addValue("EventTime", thermostatEvent.getEventTime());
            msps.addValue("ThermostatId", thermostatEvent.getThermostatId());
            if(thermostatEvent instanceof ManualThermostatEvent) {
                ManualThermostatEvent manualEvent = (ManualThermostatEvent) thermostatEvent;
                msps.addValue("ManualCoolTemp", manualEvent.getManualCoolTemp().toFahrenheit().getValue());
                msps.addValue("ManualHeatTemp", manualEvent.getManualHeatTemp().toFahrenheit().getValue());
                msps.addValue("ManualFan", manualEvent.getManualFan());
                msps.addValue("ManualMode", manualEvent.getManualMode());
                msps.addValue("ManualHold", YNBoolean.valueOf(manualEvent.isManualHold()));
                msps.addValue("EventType", ThermostatEventType.MANUAL);
            } else if(thermostatEvent instanceof ScheduleThermostatEvent) {
                ScheduleThermostatEvent scheduleEvent = (ScheduleThermostatEvent) thermostatEvent;
                msps.addValue("ScheduleId", scheduleEvent.getScheduleId());
                msps.addValue("ScheduleMode", scheduleEvent.getScheduleMode());
                msps.addValue("EventType", ThermostatEventType.SCHEDULE);
            } else if(thermostatEvent instanceof RestoreThermostatEvent) {
                msps.addValue("EventType", ThermostatEventType.RESTORE);
            }
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
    
    @Autowired
    public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
        this.accountThermostatScheduleDao = accountThermostatScheduleDao;
    }
}
