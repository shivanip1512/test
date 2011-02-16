package com.cannontech.stars.dr.thermostat.model;

import org.joda.time.Instant;

import com.cannontech.common.util.CtiUtilities;

public class ThermostatEvent implements Comparable<ThermostatEvent>{
    //used for all events
    private Integer eventId;
    private ThermostatEventType eventType;
    private String userName;
    private Instant eventTime;
    private int thermostatId;
    private String thermostatName;
    
    //only used for manual events
    private Integer manualTemp;
    private ThermostatMode manualMode;
    private ThermostatFanState manualFan;
    private boolean manualHold;
    
    //only used for schedule events
    private Integer scheduleId;
    private String scheduleName;
    private ThermostatScheduleMode scheduleMode;

    @Override
    public int compareTo(ThermostatEvent event) {
        return this.eventTime.compareTo(event.eventTime);
    }
    
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public ThermostatEventType getEventType() {
        return eventType;
    }

    public void setEventType(ThermostatEventType eventType) {
        this.eventType = eventType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public Integer getThermostatId() {
        return thermostatId;
    }

    public void setThermostatId(Integer thermostatId) {
        this.thermostatId = thermostatId;
    }

    public Integer getManualTemp() {
        return manualTemp;
    }
    
    public Integer getManualTempInC() {
        return CtiUtilities.convertTemperature(manualTemp, CtiUtilities.FAHRENHEIT_CHARACTER, CtiUtilities.CELSIUS_CHARACTER);
    }
    
    public void setManualTemp(Integer manualTemp) {
        this.manualTemp = manualTemp;
    }

    public ThermostatMode getManualMode() {
        return manualMode;
    }

    public void setManualMode(ThermostatMode manualMode) {
        this.manualMode = manualMode;
    }

    public ThermostatFanState getManualFan() {
        return manualFan;
    }

    public void setManualFan(ThermostatFanState manualFan) {
        this.manualFan = manualFan;
    }

    public boolean isManualHold() {
        return manualHold;
    }

    public void setManualHold(boolean manualHold) {
        this.manualHold = manualHold;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public ThermostatScheduleMode getScheduleMode() {
        return scheduleMode;
    }

    public void setScheduleMode(ThermostatScheduleMode scheduleMode) {
        this.scheduleMode = scheduleMode;
    }
    
    public String getScheduleName() {
        return scheduleName;
    }
    
    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public void setThermostatName(String thermostatName) {
        this.thermostatName = thermostatName;
    }

    public String getThermostatName() {
        return thermostatName;
    }
}
