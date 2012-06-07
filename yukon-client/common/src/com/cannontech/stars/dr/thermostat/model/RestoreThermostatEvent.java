package com.cannontech.stars.dr.thermostat.model;

public class RestoreThermostatEvent extends ThermostatEvent {
    
    public ThermostatEventType getEventType() {
        return ThermostatEventType.RESTORE;
    }
}
