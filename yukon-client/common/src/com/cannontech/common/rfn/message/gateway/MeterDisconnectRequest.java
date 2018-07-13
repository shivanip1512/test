package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

public class MeterDisconnectRequest implements RfnIdentifyingMessage, Serializable {

private RfnIdentifier rfnIdentifier;
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
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
        GatewayDataRequest other = (GatewayDataRequest) obj;
        if (rfnIdentifier == null) {
            if (other.getRfnIdentifier() != null) {
                return false;
            }
        } else if (!rfnIdentifier.equals(other.getRfnIdentifier())) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("MeterDisconnectRequest [rfnIdentifier=%s]", rfnIdentifier);
    }
    
}
