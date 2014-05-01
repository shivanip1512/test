package com.cannontech.dr.ecobee.message;

import com.cannontech.dr.ecobee.service.EcobeeStatusCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base class for all Ecobee responses that contain a Status.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class BaseResponse {
    protected final Status status;
    
    @JsonCreator
    public BaseResponse(@JsonProperty("status") Status status) {
        this.status = status;
    }
    
    public Status getStatus() {
        return status;
    }
    
    //not part of the JSON serialization
    public boolean hasCode(EcobeeStatusCode code) {
        return status.getCode() == code.getCode();
    }
}
