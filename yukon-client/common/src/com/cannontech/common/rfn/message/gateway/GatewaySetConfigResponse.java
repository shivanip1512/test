package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * Message sent from NM to Yukon on a temporary queue as a result of a
 * {@link GatewaySetConfigRequest}
 * Each config item has its own result
 */
public class GatewaySetConfigResponse implements RfnIdentifyingMessage, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    
    // Config results, maps the items in GatewaySetConfigRequest
    private GatewayConfigResult routeColorResult;
    private GatewayConfigResult ipv6PrefixResult;
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }    
    
    public GatewayConfigResult getRouteColorResult() {
        return routeColorResult;
    }

    public void setRouteColorResult(GatewayConfigResult routeColorResult) {
        this.routeColorResult = routeColorResult;
    }

    public GatewayConfigResult getIpv6PrefixResult() {
        return ipv6PrefixResult;
    }

    public void setIpv6PrefixResult(GatewayConfigResult ipv6PrefixResult) {
        this.ipv6PrefixResult = ipv6PrefixResult;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipv6PrefixResult == null) ? 0 : ipv6PrefixResult.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
        result = prime * result + ((routeColorResult == null) ? 0 : routeColorResult.hashCode());
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
        GatewaySetConfigResponse other = (GatewaySetConfigResponse) obj;
        if (ipv6PrefixResult != other.ipv6PrefixResult) {
            return false;
        }
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null) {
                return false;
            }
        } else if (!rfnIdentifier.equals(other.rfnIdentifier)) {
            return false;
        }
        if (routeColorResult != other.routeColorResult) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "GatewaySetConfigResponse [rfnIdentifier=" + rfnIdentifier + ", routeColorResult="
               + routeColorResult + ", ipv6PrefixResult=" + ipv6PrefixResult + "]";
    }
    
}