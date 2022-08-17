package com.cannontech.common.rfn.message.metadata;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * JMS Queue name: yukon.qr.obj.common.rfn.MetadataRequest
 */
public class RfnMetadataRequest implements Serializable, RfnIdentifyingMessage {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    
    public RfnMetadataRequest(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    @Override
    public String toString() {
        return String.format("RfnMetadataRequest [rfnIdentifier=%s]", rfnIdentifier);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
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
        RfnMetadataRequest other = (RfnMetadataRequest) obj;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        return true;
    }

}