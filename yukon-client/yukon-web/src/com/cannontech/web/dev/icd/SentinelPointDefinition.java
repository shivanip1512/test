package com.cannontech.web.dev.icd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SentinelPointDefinition extends ModelPointDefinition {
    
    @JsonProperty("Itron name")
    public String itronName;
}