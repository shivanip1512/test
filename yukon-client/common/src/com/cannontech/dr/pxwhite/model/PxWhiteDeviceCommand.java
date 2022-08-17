package com.cannontech.dr.pxwhite.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A wrapper for sending commands to PX White devices.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PxWhiteDeviceCommand {
    private final String value;
    
    @JsonCreator
    public PxWhiteDeviceCommand(@JsonProperty("v") String value) {
        this.value = value;
    }
    
    @JsonProperty("v")
    public String getValue() {
        return value;
    }
}
