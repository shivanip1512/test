package com.cannontech.dr.ecobee.message;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeusAddThermostat {
    @JsonProperty("id") String thermostatGroupId;
    ZeusThermostatState state;
    @JsonProperty("thermostat_ids") List<String> thermostatIds;

    public ZeusAddThermostat(String thermostatGroupId, ZeusThermostatState state, List<String> thermostatIds) {
        this.thermostatGroupId = thermostatGroupId;
        this.state = state;
        this.thermostatIds = thermostatIds;
    }

    public ZeusAddThermostat() {
    }

    public String getThermostatGroupId() {
        return thermostatGroupId;
    }

    public void setThermostatGroupId(String thermostatGroupId) {
        this.thermostatGroupId = thermostatGroupId;
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
