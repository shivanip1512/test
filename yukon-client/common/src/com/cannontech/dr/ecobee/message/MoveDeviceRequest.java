package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class MoveDeviceRequest {
    private final String operation = "assign";
    private final String setPath;
    private final String thermostats;

    /**
     * @param setPath A full management set path (not just the set name). Should begin with the root "/".
     */
    @JsonCreator
    public MoveDeviceRequest(@JsonProperty("thermostats") String serialNumber, @JsonProperty("setPath") String setPath) {
        thermostats = serialNumber;
        this.setPath = setPath;
    }

    public String getOperation() {
        return operation;
    }

    public String getSetPath() {
        return setPath;
    }

    public String getThermostats() {
        return thermostats;
    }
}
