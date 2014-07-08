package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class RegisterDeviceRequest extends DeviceRequest{
    private final String thermostats;
    
    @JsonCreator
    public RegisterDeviceRequest(@JsonProperty("thermostats") String serialNumber) {
        super("register");
        thermostats = serialNumber;
    }
    
    public String getThermostats() {
        return thermostats;
    }
    
}
