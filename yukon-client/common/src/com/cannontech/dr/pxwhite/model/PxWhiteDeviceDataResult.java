package com.cannontech.dr.pxwhite.model;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data for a single channel on a single device.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PxWhiteDeviceDataResult {
    private final String tag;
    private final Instant time;
    private final String value;
    private final String nValue;
    
    @JsonCreator
    public PxWhiteDeviceDataResult(@JsonProperty("tag") String tag,
                                   @JsonProperty("time") Instant time,
                                   @JsonProperty("value") String value,
                                   @JsonProperty("nvalue") String nValue) {
        this.tag = tag;
        this.time = time;
        this.value = value;
        this.nValue = nValue;
    }

    public String getTag() {
        return tag;
    }

    public Instant getTime() {
        return time;
    }

    public String getValue() {
        return value;
    }
    
    @JsonProperty("nvalue")
    public String getNValue() {
        return nValue;
    }
    
}
