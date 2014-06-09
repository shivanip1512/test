package com.cannontech.dr.ecobee.message;

import com.cannontech.dr.ecobee.message.partial.DemandResponseRef;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class DrRestoreRequest {
    private final String operation = "cancel";
    private final DemandResponseRef demandResponseRef;
    
    public DrRestoreRequest(String demandResponseRef) {
        this(new DemandResponseRef(demandResponseRef));
    }

    @JsonCreator
    public DrRestoreRequest(@JsonProperty("demandResponse") DemandResponseRef demandResponseRef) {
        this.demandResponseRef = demandResponseRef;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public DemandResponseRef getDemandResponse() {
        return demandResponseRef;
    }
}
