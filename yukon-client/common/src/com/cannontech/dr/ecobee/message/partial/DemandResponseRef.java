package com.cannontech.dr.ecobee.message.partial;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Identifies an ecobee demand response event. Used to cancel events.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public final class DemandResponseRef {
    private final String demandResponseRef;

    @JsonCreator
    public DemandResponseRef(@JsonProperty("demandResponseRef") String demandResponseRef) {
        this.demandResponseRef = demandResponseRef;
    }
    
    public String getDemandResponseRef() {
        return demandResponseRef;
    }
}
