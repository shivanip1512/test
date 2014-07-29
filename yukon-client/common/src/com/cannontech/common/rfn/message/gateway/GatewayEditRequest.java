package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * Message sent from Yukon to NM to request a gateway edit.
 * 
 * JMS queue name: yukon.qr.obj.common.rfn.GatewayUpdateRequest
 */
public class GatewayEditRequest implements RfnIdentifyingMessage, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    private GatewaySaveData data;
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public GatewaySaveData getData() {
        return data;
    }
    
    public void setData(GatewaySaveData data) {
        this.data = data;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
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
        GatewayEditRequest other = (GatewayEditRequest) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
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
        return String.format("GatewayEditRequest [rfnIdentifier=%s, data=%s]", rfnIdentifier, data);
    }
    
}