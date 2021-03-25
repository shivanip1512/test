package com.cannontech.dr.pxmw.model.v1;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWTimeSeriesResultV1 {
    public String tag;
    public String trait;
    public List<PxMWTimeSeriesValueV1> values;

    @JsonCreator
    public PxMWTimeSeriesResultV1(@JsonProperty("tag") String tag, @JsonProperty("trait") String trait,
            @JsonProperty("values") List<PxMWTimeSeriesValueV1> values) {
        this.tag = tag;
        this.trait = trait;
        this.values = values;
    }

    @JsonProperty("tag")
    public String getTag() {
        return tag;
    }

    @JsonProperty("trait")
    public String getTrait() {
        return trait;
    }

    @JsonProperty("values")
    public List<PxMWTimeSeriesValueV1> getValues() {
        return values;
    }
}
