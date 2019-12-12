package com.cannontech.common.rfn.message.node;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class NodeWiFiComm implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private RfnIdentifier deviceRfnIdentifier; // can't be null
    
    private RfnIdentifier gatewayRfnIdentifier; // null indicates no primary gateway
    
    private NodeWiFiCommStatus nodeWiFiCommStatus; // null indicates unknown Communication Status
    
    private long wiFiCommStatusTimestamp; // Node Communication Status obtained at

    public RfnIdentifier getDeviceRfnIdentifier() {
        return deviceRfnIdentifier;
    }

    public void setDeviceRfnIdentifier(RfnIdentifier deviceRfnIdentifier) {
        this.deviceRfnIdentifier = deviceRfnIdentifier;
    }

    public RfnIdentifier getGatewayRfnIdentifier() {
        return gatewayRfnIdentifier;
    }

    public void setGatewayRfnIdentifier(RfnIdentifier gatewayRfnIdentifier) {
        this.gatewayRfnIdentifier = gatewayRfnIdentifier;
    }

    public NodeWiFiCommStatus getNodeWiFiCommStatus() {
        return nodeWiFiCommStatus;
    }

    public void setNodeWiFiCommStatus(NodeWiFiCommStatus nodeWiFiCommStatus) {
        this.nodeWiFiCommStatus = nodeWiFiCommStatus;
    }

    public long getWiFiCommStatusTimestamp() {
        return wiFiCommStatusTimestamp;
    }

    public void setWiFiCommStatusTimestamp(long wiFiCommStatusTimestamp) {
        this.wiFiCommStatusTimestamp = wiFiCommStatusTimestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deviceRfnIdentifier == null) ? 0 : deviceRfnIdentifier.hashCode());
        result = prime * result + ((gatewayRfnIdentifier == null) ? 0 : gatewayRfnIdentifier.hashCode());
        result = prime * result + ((nodeWiFiCommStatus == null) ? 0 : nodeWiFiCommStatus.hashCode());
        result = prime * result + (int) (wiFiCommStatusTimestamp ^ (wiFiCommStatusTimestamp >>> 32));
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
        NodeWiFiComm other = (NodeWiFiComm) obj;
        if (deviceRfnIdentifier == null) {
            if (other.deviceRfnIdentifier != null)
                return false;
        } else if (!deviceRfnIdentifier.equals(other.deviceRfnIdentifier))
            return false;
        if (gatewayRfnIdentifier == null) {
            if (other.gatewayRfnIdentifier != null)
                return false;
        } else if (!gatewayRfnIdentifier.equals(other.gatewayRfnIdentifier))
            return false;
        if (nodeWiFiCommStatus != other.nodeWiFiCommStatus)
            return false;
        if (wiFiCommStatusTimestamp != other.wiFiCommStatusTimestamp)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
                .format("NodeWiFiComm [deviceRfnIdentifier=%s, gatewayRfnIdentifier=%s, nodeWiFiCommStatus=%s, wiFiCommStatusTimestamp=%s]",
                        deviceRfnIdentifier,
                        gatewayRfnIdentifier,
                        nodeWiFiCommStatus,
                        wiFiCommStatusTimestamp);
    }
    
}
