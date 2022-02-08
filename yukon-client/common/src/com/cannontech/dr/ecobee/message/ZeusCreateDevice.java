package com.cannontech.dr.ecobee.message;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeusCreateDevice {
    private ZeusThermostatState state;
    @JsonProperty("thermostat_ids") private List<String> thermostatIds;

    public ZeusCreateDevice() {
    }

    public ZeusCreateDevice(ZeusThermostatState state, List<String> thermostatIds) {
        this.state = state;
        this.thermostatIds = thermostatIds;
    }

    public ZeusThermostatState getState() {
        return state;
    }

    public void setState(ZeusThermostatState state) {
        this.state = state;
    }

    public List<String> getThermostatIds() {
        return thermostatIds;
    }

    public void setThermostatIds(List<String> thermostatIds) {
        this.thermostatIds = thermostatIds;
    }

}