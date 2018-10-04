package com.cannontech.dr.pxwhite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data for a single device, which may contain a single value for multiple channels.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PxWhiteDeviceData {
    private final String deviceId;
    private final List<PxWhiteDeviceDataResult> results;
    
    @JsonCreator
    public PxWhiteDeviceData(@JsonProperty("device_id") String deviceId,
                             @JsonProperty("results") List<PxWhiteDeviceDataResult> results) {
        
        this.deviceId = deviceId;
        this.results = results;
    }
    
    @JsonProperty("device_id")
    public String getDeviceId() {
        return deviceId;
    }

    public List<PxWhiteDeviceDataResult> getResults() {
        return results;
    }
    
}
