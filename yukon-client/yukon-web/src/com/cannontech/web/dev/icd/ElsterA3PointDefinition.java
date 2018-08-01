package com.cannontech.web.dev.icd;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ElsterA3PointDefinition extends ModelPointDefinition {
            
    @JsonProperty("Other names")
    public List<String> otherNames;
}