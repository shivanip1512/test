package com.cannontech.dr.ecobee.message;

import com.cannontech.dr.ecobee.message.partial.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DrResponse extends BaseResponse {
    private final String demandResponseRef;
    
    @JsonCreator
    public DrResponse(@JsonProperty("demandResponseRef") String demandResponseRef, 
                      @JsonProperty("status") Status status) {
        super(status);
        this.demandResponseRef = demandResponseRef;
    }
    
    public String getDemandResponseRef() {
        return demandResponseRef;
    }
}
