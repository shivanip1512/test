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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((demandResponseRef == null) ? 0 : demandResponseRef.hashCode());
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
        DrResponse other = (DrResponse) obj;
        if (demandResponseRef == null) {
            if (other.demandResponseRef != null) {
                return false;
            }
        } else if (!demandResponseRef.equals(other.demandResponseRef)) {
            return false;
        }
        return true;
    }
}
