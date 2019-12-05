package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.node.NodeComm;

public class GatewayNodes implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private RfnIdentifier gatewayRfnIdentifier;
    
    // Map each device (deviceRfnIdentifier) for the gateway
    //  to its communication object (i.e., NodeComm)
    private Map<RfnIdentifier, NodeComm> nodeComms;

    public RfnIdentifier getGatewayRfnIdentifier() {
        return gatewayRfnIdentifier;
    }

    public void setGatewayRfnIdentifier(RfnIdentifier gatewayRfnIdentifier) {
        this.gatewayRfnIdentifier = gatewayRfnIdentifier;
    }

    public Map<RfnIdentifier, NodeComm> getNodeComms() {
        return nodeComms;
    }

    public void setNodeComms(Map<RfnIdentifier, NodeComm> nodeComms) {
        this.nodeComms = nodeComms;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((gatewayRfnIdentifier == null) ? 0 : gatewayRfnIdentifier.hashCode());
        result = prime * result + ((nodeComms == null) ? 0 : nodeComms.hashCode());
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
        GatewayNodes other = (GatewayNodes) obj;
        if (gatewayRfnIdentifier == null) {
            if (other.gatewayRfnIdentifier != null)
                return false;
        } else if (!gatewayRfnIdentifier.equals(other.gatewayRfnIdentifier))
            return false;
        if (nodeComms == null) {
            if (other.nodeComms != null)
                return false;
        } else if (!nodeComms.equals(other.nodeComms))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("GatewayNodes [gatewayRfnIdentifier=%s, nodeComms=%s]",
                             gatewayRfnIdentifier,
                             nodeComms);
    }
}
