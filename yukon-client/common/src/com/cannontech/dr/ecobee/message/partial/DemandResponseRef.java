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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((demandResponseRef == null) ? 0 : demandResponseRef.hashCode());
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
        DemandResponseRef other = (DemandResponseRef) obj;
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
