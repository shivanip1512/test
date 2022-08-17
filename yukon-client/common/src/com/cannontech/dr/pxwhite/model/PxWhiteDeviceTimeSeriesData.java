package com.cannontech.dr.pxwhite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data for a single device, which may contain many values for multiple channels.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PxWhiteDeviceTimeSeriesData {
    private final String deviceId;
    private final List<PxWhiteDeviceTimeSeriesDataResult> results;
    
    @JsonCreator
    public PxWhiteDeviceTimeSeriesData(@JsonProperty("device_id") String deviceId,
                                       @JsonProperty("results") List<PxWhiteDeviceTimeSeriesDataResult> results) {
        
        this.deviceId = deviceId;
        this.results = results;
    }
    
    @JsonProperty("device_id")
    public String getDeviceId() {
        return deviceId;
    }
    
    public List<PxWhiteDeviceTimeSeriesDataResult> getResults() {
        return results;
    }
}
