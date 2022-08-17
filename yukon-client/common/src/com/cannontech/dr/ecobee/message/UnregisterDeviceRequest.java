package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UnregisterDeviceRequest extends DeviceRequest {
    private final String thermostats;
    
    @JsonCreator
    public UnregisterDeviceRequest(@JsonProperty("thermostats") String serialNumber) {
        super("unregister");
        thermostats = serialNumber;
    }
    
    public String getThermostats() {
        return thermostats;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((thermostats == null) ? 0 : thermostats.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UnregisterDeviceRequest other = (UnregisterDeviceRequest) obj;
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
