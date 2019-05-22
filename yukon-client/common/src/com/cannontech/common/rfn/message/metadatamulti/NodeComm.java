package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class NodeComm implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private RfnIdentifier meterRfnIdentifier;
    
    private RfnIdentifier gatewayRfnIdentifier;
    
    private NodeCommStatus nodeCommStatus; // Node Communication Status
    
    private long nodeCommStatusTimestamp; // Node Communication Status obtained at

    public RfnIdentifier getMeterRfnIdentifier() {
        return meterRfnIdentifier;
    }

    public void setMeterRfnIdentifier(RfnIdentifier meterRfnIdentifier) {
        this.meterRfnIdentifier = meterRfnIdentifier;
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
            prime * result + ((gatewayRfnIdentifier == null) ? 0 : gatewayRfnIdentifier.hashCode());
        result =
            prime * result + ((meterRfnIdentifier == null) ? 0 : meterRfnIdentifier.hashCode());
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
        if (gatewayRfnIdentifier == null) {
            if (other.gatewayRfnIdentifier != null)
                return false;
        } else if (!gatewayRfnIdentifier.equals(other.gatewayRfnIdentifier))
            return false;
        if (meterRfnIdentifier == null) {
            if (other.meterRfnIdentifier != null)
                return false;
        } else if (!meterRfnIdentifier.equals(other.meterRfnIdentifier))
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
            .format("NodeComm [meterRfnIdentifier=%s, gatewayRfnIdentifier=%s, nodeCommStatus=%s, nodeCommStatusTimestamp=%s]",
                    meterRfnIdentifier,
                    gatewayRfnIdentifier,
                    nodeCommStatus,
                    nodeCommStatusTimestamp);
    }
}
