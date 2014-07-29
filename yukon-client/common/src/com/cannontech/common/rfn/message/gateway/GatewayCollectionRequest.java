package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/** 
 * Request from Yukon to NM to collect data from a gateway.
 * 
 * JMS queue name: yukon.qr.obj.common.rfn.GatewayActionRequest
 */
public class GatewayCollectionRequest implements RfnIdentifyingMessage, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    private Set<DataType> types;
    
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public Set<DataType> getTypes() {
        return types;
    }
    
    public void setTypes(Set<DataType> types) {
        this.types = types;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + ((types == null) ? 0 : types.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GatewayCollectionRequest other = (GatewayCollectionRequest) obj;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        if (types == null) {
            if (other.types != null)
                return false;
        } else if (!types.equals(other.types))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("GatewayCollectionRequest [rfnIdentifier=%s, types=%s]", rfnIdentifier, types);
    }
    
}