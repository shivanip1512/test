package com.cannontech.web.dev.icd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WaterNodePointDefinition extends PointDefinition {
    
    @JsonProperty("Metric ID")
    public Integer metricId;
}