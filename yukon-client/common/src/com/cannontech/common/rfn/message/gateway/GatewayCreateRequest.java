package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

/**
 * Message sent from Yukon to  NM to request a gateway creation.
 * 
 * JMS queue name: yukon.qr.obj.common.rfn.GatewayUpdateRequest
 */
public class GatewayCreateRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private GatewaySaveData data;
    
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
        GatewayCreateRequest other = (GatewayCreateRequest) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("GatewayCreateRequest [data=%s]", data);
    }
    
}