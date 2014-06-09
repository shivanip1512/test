package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class RegisterDeviceRequest {
    private final String operation = "register";
    private final String thermostats;
    
    @JsonCreator
    public RegisterDeviceRequest(@JsonProperty("thermostats") String serialNumber) {
        thermostats = serialNumber;
    }
    
    public String getThermostats() {
        return thermostats;
    }
    
    public String getOperation() {
        return operation;
    }
}
