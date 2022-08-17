package com.cannontech.dr.pxwhite.model;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data containing a single value and timestamp.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PxWhiteTimeSeriesPoint {
    private final Instant timestamp;
    private final String value;
    
    @JsonCreator
    public PxWhiteTimeSeriesPoint(@JsonProperty("t") Instant timestamp,
                                  @JsonProperty("v") String value) {
        
        this.timestamp = timestamp;
        this.value = value;
    }

    @JsonProperty("t")
    public Instant getTimestamp() {
        return timestamp;
    }

    @JsonProperty("v")
    public String getValue() {
        return value;
    }
}
