package com.cannontech.yukon.api.stars.model;

import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.google.common.collect.SetMultimap;

public class ThermostatSchedule {

    private String scheduleName;
    private String accountNumber;
    private SchedulableThermostatType schedulableThermostatType;
    private ThermostatScheduleMode thermostatScheduleMode;
    private SetMultimap<TimeOfWeek, SchedulePeriod> schedulePeriodContainer;
        
    public String getScheduleName() {
        return scheduleName;
    }
    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public SchedulableThermostatType getSchedulableThermostatType() {
        return schedulableThermostatType;
    }
    public void setSchedulableThermostatType(SchedulableThermostatType schedulableThermostatType) {
        this.schedulableThermostatType = schedulableThermostatType;
    }

    public ThermostatScheduleMode getThermostatScheduleMode() {
        return thermostatScheduleMode;
    }
    public void setThermostatScheduleMode(ThermostatScheduleMode thermostatScheduleMode) {
        this.thermostatScheduleMode = thermostatScheduleMode;
    }
    
    public SetMultimap<TimeOfWeek, SchedulePeriod> getSchedulePeriodContainer() {
        return schedulePeriodContainer;
    }
    public void setSchedulePeriodContainer(SetMultimap<TimeOfWeek, SchedulePeriod> schedulePeriodContainer) {
        this.schedulePeriodContainer = schedulePeriodContainer;
    }
}