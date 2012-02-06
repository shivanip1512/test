package com.cannontech.stars.dr.thermostat.model;

import com.cannontech.common.temperature.Temperature;

/**
 * Model object which represents a manual thermostat event
 */
public class ThermostatManualEvent extends CustomerThermostatEventBase {

    public static final Temperature DEFAULT_TEMPERATURE = Temperature.fromFahrenheit(72);

    // Default temp to 72F
    private Temperature previousHeatTemperature = Temperature.fromFahrenheit(72);
    private Temperature previousCoolTemperature = Temperature.fromFahrenheit(72);
    private boolean holdTemperature = false;
    private ThermostatMode mode = ThermostatMode.OFF;
    private ThermostatFanState fanState = ThermostatFanState.AUTO;
    private boolean runProgram = false;
    private boolean autoModeEnabledCommand = false;

    public Temperature getPreviousHeatTemperature() {
        return previousHeatTemperature;
    }
    public void setPreviousHeatTemperature(Temperature previousHeatTemperature) {
        this.previousHeatTemperature = previousHeatTemperature;
    }
    
    public Temperature getPreviousCoolTemperature() {
        return previousCoolTemperature;
    }
    public void setPreviousCoolTemperature(Temperature previousCoolTemperature) {
        this.previousCoolTemperature = previousCoolTemperature;
    }
    
    public boolean isHoldTemperature() {
        return holdTemperature;
    }
    public void setHoldTemperature(boolean holdTemperature) {
        this.holdTemperature = holdTemperature;
    }

    public ThermostatMode getMode() {
        return mode;
    }
    public void setMode(ThermostatMode mode) {
        this.mode = mode;
    }
    public String getModeString() {
        if (mode == null) {
            return "";
        }

        return mode.getValue();
    }

    public ThermostatFanState getFanState() {
        return fanState;
    }
    public void setFanState(ThermostatFanState fanState) {
        this.fanState = fanState;
    }
    public String getFanStateString() {
        if (fanState == null) {
            return "";
        }

        return fanState.getValue();
    }

    public boolean isRunProgram() {
        return runProgram;
    }
    public void setRunProgram(boolean runProgram) {
        this.runProgram = runProgram;
    }
    
    public boolean isAutoModeEnabledCommand() {
        return autoModeEnabledCommand;
    }
    public void setAutoModeEnabledCommand(boolean autoModeEnabledCommand) {
        this.autoModeEnabledCommand = autoModeEnabledCommand;
    }
}