package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudTimeSeriesResultV1 implements Serializable{
    private String tag;
    private String trait;
    private List<EatonCloudTimeSeriesValueV1> values;

    @JsonCreator
    public EatonCloudTimeSeriesResultV1(@JsonProperty("tag") String tag, @JsonProperty("trait") String trait,
            @JsonProperty("values") List<EatonCloudTimeSeriesValueV1> values) {
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
    public List<EatonCloudTimeSeriesValueV1> getValues() {
        return values;
    }
}
