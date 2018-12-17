package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ElsterA3PointDefinition extends ModelPointDefinition {
            
    @JsonProperty("Other names")
    public List<String> otherNames;
}