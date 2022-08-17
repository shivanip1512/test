package com.cannontech.dr.pxwhite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data for a single channel + trait combo, which may contain many values with different timestamps.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PxWhiteDeviceTimeSeriesDataResult {
    private final String tag;
    private final String trait;
    private final List<PxWhiteTimeSeriesPoint> values;
    
    @JsonCreator
    public PxWhiteDeviceTimeSeriesDataResult(@JsonProperty("tag") String tag,
                                             @JsonProperty("trait") String trait,
                                             @JsonProperty("values") List<PxWhiteTimeSeriesPoint> values) {
        
        this.tag = tag;
        this.trait = trait;
        this.values = values;
    }

    public String getTag() {
        return tag;
    }

    public String getTrait() {
        return trait;
    }

    public List<PxWhiteTimeSeriesPoint> getValues() {
        return values;
    }
}
