package com.cannontech.yukon.api.stars.model;

import java.util.Collection;

import com.cannontech.common.temperature.Temperature;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;

public class ManualThermostatSetting {
    private Collection<String> serialNumbers;
    private ThermostatMode thermostatMode;
    private ThermostatFanState fanState;
    private Temperature heatTemperature;
    private Temperature coolTemperature;
    private boolean holdTemperature = false;
    private boolean autoModeCommand = false;
    
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
    
    public Temperature getHeatTemperature() {
        return heatTemperature;
    }
    public void setHeatTemperature(Temperature heatTemperature) {
        this.heatTemperature = heatTemperature;
    }

    public Temperature getCoolTemperature() {
        return coolTemperature;
    }
    public void setCoolTemperature(Temperature coolTemperature) {
        this.coolTemperature = coolTemperature;
    }

    public boolean isHoldTemperature() {
        return holdTemperature;
    }
    public void setHoldTemperature(boolean holdTemperature) {
        this.holdTemperature = holdTemperature;
    }
    
    public boolean isAutoModeCommand() {
        return autoModeCommand;
    }
    public void setAutoModeCommand(boolean autoModeCommand) {
        this.autoModeCommand = autoModeCommand;
    }
}