package com.cannontech.amr.rfn.service.pointmapping.icd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WaterNodePointDefinition extends PointDefinition {
    
    @JsonProperty("Metric ID")
    public Integer metricId;
}