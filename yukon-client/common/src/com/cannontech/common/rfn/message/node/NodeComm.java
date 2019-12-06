package com.cannontech.common.rfn.message.node;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class NodeComm implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private RfnIdentifier deviceRfnIdentifier; // can't be null
    
    private RfnIdentifier gatewayRfnIdentifier; // null indicates no primary gateway
    
    private NodeCommStatus nodeCommStatus; // null indicates unknown Communication Status
    
    private long nodeCommStatusTimestamp; // Node Communication Status obtained at

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

    public NodeCommStatus getNodeCommStatus() {
        return nodeCommStatus;
    }

    public void setNodeCommStatus(NodeCommStatus nodeCommStatus) {
        this.nodeCommStatus = nodeCommStatus;
    }

    public long getNodeCommStatusTimestamp() {
        return nodeCommStatusTimestamp;
    }

    public void setNodeCommStatusTimestamp(long nodeCommStatusTimestamp) {
        this.nodeCommStatusTimestamp = nodeCommStatusTimestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((deviceRfnIdentifier == null) ? 0 : deviceRfnIdentifier.hashCode());
        result =
            prime * result + ((gatewayRfnIdentifier == null) ? 0 : gatewayRfnIdentifier.hashCode());
        result = prime * result + ((nodeCommStatus == null) ? 0 : nodeCommStatus.hashCode());
        result =
            prime * result + (int) (nodeCommStatusTimestamp ^ (nodeCommStatusTimestamp >>> 32));
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
        NodeComm other = (NodeComm) obj;
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
        if (nodeCommStatus != other.nodeCommStatus)
            return false;
        if (nodeCommStatusTimestamp != other.nodeCommStatusTimestamp)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("NodeComm [deviceRfnIdentifier=%s, gatewayRfnIdentifier=%s, nodeCommStatus=%s, nodeCommStatusTimestamp=%s]",
                    deviceRfnIdentifier,
                    gatewayRfnIdentifier,
                    nodeCommStatus,
                    nodeCommStatusTimestamp);
    }

}
