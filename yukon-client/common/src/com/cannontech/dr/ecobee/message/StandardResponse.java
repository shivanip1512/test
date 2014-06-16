package com.cannontech.dr.ecobee.message;

import com.cannontech.dr.ecobee.message.partial.Status;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (success ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StandardResponse other = (StandardResponse) obj;
        if (success != other.success) {
            return false;
        }
        return true;
    }
}
