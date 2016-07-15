package com.cannontech.common.rfn.message.datastreaming.gateway;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * A query for gateway's data streaming information.
 * You can use it to query one gateway or a list of gateways.
 * 
 * JMS Queue name:
 *     com.eaton.eas.yukon.networkmanager.dataStreaming.request
 */
public class GatewayDataStreamingInfoRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Set<RfnIdentifier> gatewayRfnIdentifiers;

    public Set<RfnIdentifier> getGatewayRfnIdentifiers() {
        return gatewayRfnIdentifiers;
    }

    public void setGatewayRfnIdentifiers(Set<RfnIdentifier> gatewayRfnIdentifiers) {
        this.gatewayRfnIdentifiers = gatewayRfnIdentifiers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gatewayRfnIdentifiers == null) ? 0 : gatewayRfnIdentifiers.hashCode());
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
        GatewayDataStreamingInfoRequest other = (GatewayDataStreamingInfoRequest) obj;
        if (gatewayRfnIdentifiers == null) {
            if (other.gatewayRfnIdentifiers != null) {
                return false;
            }
        } else if (!gatewayRfnIdentifiers.equals(other.gatewayRfnIdentifiers)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GatewayDataStreamingInfoRequest [gatewayRfnIdentifiers=" + gatewayRfnIdentifiers + "]";
    }
    
}
