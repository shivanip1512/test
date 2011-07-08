package com.cannontech.stars.dr.thermostat.model;

import com.cannontech.common.temperature.FahrenheitTemperature;

/**
 * Model object which represents a manual thermostat event
 */
public class ThermostatManualEvent extends CustomerThermostatEventBase {

    public static final FahrenheitTemperature DEFAULT_TEMPERATURE = new FahrenheitTemperature(72);

    // Default temp to 72F
    private FahrenheitTemperature previousTemperature = DEFAULT_TEMPERATURE;
    private boolean holdTemperature = false;
    private ThermostatMode mode = ThermostatMode.OFF;
    private ThermostatFanState fanState = ThermostatFanState.AUTO;
    private boolean runProgram = false;

    public FahrenheitTemperature getPreviousTemperature() {
        return previousTemperature;
    }

    public void setPreviousTemperature(FahrenheitTemperature previousTemperature) {
        this.previousTemperature = previousTemperature;
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

}
