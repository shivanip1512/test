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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((demandResponseRef == null) ? 0 : demandResponseRef.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
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
        DrRestoreRequest other = (DrRestoreRequest) obj;
        if (demandResponseRef == null) {
            if (other.demandResponseRef != null) {
                return false;
            }
        } else if (!demandResponseRef.equals(other.demandResponseRef)) {
            return false;
        }
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        return true;
    }
}
