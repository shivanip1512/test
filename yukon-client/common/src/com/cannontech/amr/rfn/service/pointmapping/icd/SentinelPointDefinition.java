package com.cannontech.amr.rfn.service.pointmapping.icd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SentinelPointDefinition extends ModelPointDefinition {
    
    @JsonProperty("Itron name")
    public String itronName;
}