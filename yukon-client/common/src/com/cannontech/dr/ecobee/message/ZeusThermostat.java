package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZeusThermostat {

    private String id;
    private String state;

    public ZeusThermostat() {
    }

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

}
