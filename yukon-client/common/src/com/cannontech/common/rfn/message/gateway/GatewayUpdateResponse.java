package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * Message sent from NM to Yukon as a response to a gateway create, edit, or delete request.
 */
public class GatewayUpdateResponse implements RfnIdentifyingMessage, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private GatewayUpdateResult result;
    private RfnIdentifier rfnIdentifier;
    
    public GatewayUpdateResult getResult() {
        return result;
    }
    
    public void setResult(GatewayUpdateResult result) {
        this.result = result;
    }
    
    /**
     * Can return <code>null</code> if a create was attempted and the creation failed.
     */
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public boolean isSuccess() {
        return result == GatewayUpdateResult.SUCCESSFUL;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.result == null) ? 0 : this.result.hashCode());
        result = prime * result
                + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
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
        GatewayUpdateResponse other = (GatewayUpdateResponse) obj;
        if (result != other.result)
            return false;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("GatewayUpdateResponse [result=%s, rfnIdentifier=%s]", result, rfnIdentifier);
    }
    
}