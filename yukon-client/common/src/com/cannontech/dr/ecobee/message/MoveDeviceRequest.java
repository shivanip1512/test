package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class MoveDeviceRequest extends DeviceRequest{
    private final String setPath;
    private final String thermostats;

    /**
     * @param setPath A full management set path (not just the set name). Should begin with the root "/".
     */
    @JsonCreator
    public MoveDeviceRequest(@JsonProperty("thermostats") String serialNumber, @JsonProperty("setPath") String setPath) {
        super("assign");
        thermostats = serialNumber;
        this.setPath = setPath;
    }

    public String getSetPath() {
        return setPath;
    }

    public String getThermostats() {
        return thermostats;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((setPath == null) ? 0 : setPath.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + ((thermostats == null) ? 0 : thermostats.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MoveDeviceRequest other = (MoveDeviceRequest) obj;
        if (setPath == null) {
            if (other.setPath != null) {
                return false;
            }
        } else if (!setPath.equals(other.setPath)) {
            return false;
        }
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        if (thermostats == null) {
            if (other.thermostats != null) {
                return false;
            }
        } else if (!thermostats.equals(other.thermostats)) {
            return false;
        }
        return true;
    }
}
