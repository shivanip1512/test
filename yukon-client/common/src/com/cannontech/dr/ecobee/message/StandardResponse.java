package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This response is used for a variety of Ecobee operations.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public final class StandardResponse extends BaseResponse {
    private final boolean success;
    
    @JsonCreator
    public StandardResponse(@JsonProperty("success") boolean success, @JsonProperty("status") Status status) {
        super(status);
        this.success = success;
    }
    
    public boolean getSuccess() {
        return success;
    }
}
