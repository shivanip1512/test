package com.cannontech.dr.ecobee.message;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZeusThermostat {

    @JsonProperty("id")
    private String serialNumber;
    @JsonProperty("state")
    private ZeusThermostatState state;

    @JsonProperty("tstatgroups")
    private List<ZeusGroup> zeusGroups;
    
    public ZeusThermostat() {
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public ZeusThermostatState getState() {
        return state;
    }

    public void setState(ZeusThermostatState state) {
        this.state = state;
    }

    public List<ZeusGroup> getZeusGroups() {
        return zeusGroups;
    }

    public void setZeusGroups(List<ZeusGroup> zeusGroups) {
        this.zeusGroups = zeusGroups;
    }
}
