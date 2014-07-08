package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SetRequest {
protected final String operation;
    
    @JsonCreator
    public SetRequest(@JsonProperty("operation") String operation) {
        this.operation = operation;
    }
    
    public String getOperation() {
        return operation;
    }
  
}
