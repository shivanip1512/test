package com.cannontech.stars.dr.thermostat.model;

import org.joda.time.Instant;

public class ThermostatEvent implements Comparable<ThermostatEvent>{
    private Integer eventId;
    private String userName;
    private Instant eventTime;
    private int thermostatId;
    private String thermostatName;

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
        return null;
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

    public void setThermostatName(String thermostatName) {
        this.thermostatName = thermostatName;
    }

    public String getThermostatName() {
        return thermostatName;
    }
}
