package com.cannontech.stars.dr.thermostat.model;

public class ScheduleThermostatEvent extends ThermostatEvent {
    
    private Integer scheduleId;
    private String scheduleName;
    private ThermostatScheduleMode scheduleMode;

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
    
    public ThermostatEventType getEventType() {
        return ThermostatEventType.SCHEDULE;
    }
}
