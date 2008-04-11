package com.cannontech.stars.dr.thermostat.model;

import java.util.Date;

import com.cannontech.common.util.CtiUtilities;

/**
 * Model object which represents a manual thermostat event
 */
public class ThermostatManualEvent {

    public static final int DEFAULT_TEMPERATURE = 72;
    
    private Integer eventId;
    private Integer thermostatId;

    // Default temp to 72F
    private Integer previousTemperature = DEFAULT_TEMPERATURE;
    private boolean holdTemperature = false;
    private ThermostatMode mode = ThermostatMode.DEFAULT;
    private ThermostatFanState fanState = ThermostatFanState.DEFAULT;
    private Date date;
    private boolean runProgram = false;

    // Fahrenheit by default
    private String temperatureUnit = CtiUtilities.FAHRENHEIT_CHARACTER;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getThermostatId() {
        return thermostatId;
    }

    public void setThermostatId(Integer thermostatId) {
        this.thermostatId = thermostatId;
    }

    /**
     * Method to get the temperature based on the current temperature unit
     * @return Tempurature in correct units
     */
    public Integer getPreviousTemperatureForUnit() {

        if ("c".equalsIgnoreCase(temperatureUnit)) {
            return getPreviousTemperatureCelsius();
        } else {
            return getPreviousTemperature();
        }

    }

    /**
     * Method to get the temperature in celsius
     * @return Tempuratre in celsius
     */
    public Integer getPreviousTemperatureCelsius() {

        long celsiusTemp = CtiUtilities.convertTemperature(previousTemperature,
                                                           CtiUtilities.FAHRENHEIT_CHARACTER,
                                                           CtiUtilities.CELSIUS_CHARACTER);
        return (int)celsiusTemp;
    }

    public Integer getPreviousTemperature() {
        return previousTemperature;
    }

    public void setPreviousTemperature(Integer previousTemperature) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public boolean isRunProgram() {
        return runProgram;
    }

    public void setRunProgram(boolean runProgram) {
        this.runProgram = runProgram;
    }

}
