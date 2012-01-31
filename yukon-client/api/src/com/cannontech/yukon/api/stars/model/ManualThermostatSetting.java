package com.cannontech.yukon.api.stars.model;

import java.util.Collection;

import com.cannontech.common.temperature.Temperature;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;

public class ManualThermostatSetting {
    private Collection<String> serialNumbers;
    private ThermostatMode thermostatMode;
    private ThermostatFanState fanState;
    private Temperature temperature;
    private boolean holdTemperature = false;
    
    public Collection<String> getSerialNumbers() {
        return serialNumbers;
    }
    public void setSerialNumbers(Collection<String> serialNumbers) {
        this.serialNumbers = serialNumbers;
    }
    
    public ThermostatMode getThermostatMode() {
        return thermostatMode;
    }
    public void setThermostatMode(ThermostatMode thermostatMode) {
        this.thermostatMode = thermostatMode;
    }
    
    public ThermostatFanState getFanState() {
        return fanState;
    }
    public void setFanState(ThermostatFanState fanState) {
        this.fanState = fanState;
    }
    
    public Temperature getTemperature() {
        return temperature;
    }
    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public boolean isHoldTemperature() {
        return holdTemperature;
    }
    public void setHoldTemperature(boolean holdTemperature) {
        this.holdTemperature = holdTemperature;
    }
}