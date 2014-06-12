package com.cannontech.dr.ecobee.message;

import com.cannontech.dr.ecobee.message.partial.Status;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseResponse other = (BaseResponse) obj;
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        return true;
    }
}
