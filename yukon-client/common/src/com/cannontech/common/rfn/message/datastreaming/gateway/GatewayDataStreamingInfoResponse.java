package com.cannontech.common.rfn.message.datastreaming.gateway;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * Response to DeviceDataStreamingInfoRequest.
 * 
 * JMS Queue name:
 *     com.eaton.eas.yukon.networkmanager.dataStreaming.response
 */
public class GatewayDataStreamingInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // null GatewayDataStreamingInfo value indicates an invalid gateway.
    private Map<RfnIdentifier, GatewayDataStreamingInfo> gatewayDataStreamingInfos;

    public Map<RfnIdentifier, GatewayDataStreamingInfo> getGatewayDataStreamingInfos() {
        return gatewayDataStreamingInfos;
    }

    public void setGatewayDataStreamingInfos(Map<RfnIdentifier, GatewayDataStreamingInfo> gatewayDataStreamingInfos) {
        this.gatewayDataStreamingInfos = gatewayDataStreamingInfos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gatewayDataStreamingInfos == null) ? 0 : gatewayDataStreamingInfos.hashCode());
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
        GatewayDataStreamingInfoResponse other = (GatewayDataStreamingInfoResponse) obj;
        if (gatewayDataStreamingInfos == null) {
            if (other.gatewayDataStreamingInfos != null) {
                return false;
            }
        } else if (!gatewayDataStreamingInfos.equals(other.gatewayDataStreamingInfos)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GatewayDataStreamingInfoResponse [gatewayDataStreamingInfos=" + gatewayDataStreamingInfos + "]";
    }
    
}
