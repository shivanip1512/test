package com.cannontech.dr.pxwhite.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxWhiteEnum {
    private final String id;
    private final Integer value;
    
    @JsonCreator
    public PxWhiteEnum(@JsonProperty("s") String id, @JsonProperty("v") Integer value) {
        this.id = id;
        this.value = value;
    }
    
    @JsonProperty("s")
    public String getId() {
        return id;
    }
    
    @JsonProperty("v")
    public Integer getValue() {
        return value;
    }
}
