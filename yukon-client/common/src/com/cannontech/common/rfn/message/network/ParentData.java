package com.cannontech.common.rfn.message.network;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class ParentData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier;
    private String nodeSN;
    private String nodeMacAddress;

    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public String getNodeSN() {
        return nodeSN;
    }

    public void setNodeSN(String nodeSN) {
        this.nodeSN = nodeSN;
    }

    public String getNodeMacAddress() {
        return nodeMacAddress;
    }

    public void setNodeMacAddress(String nodeMacAddress) {
        this.nodeMacAddress = nodeMacAddress;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nodeMacAddress == null) ? 0 : nodeMacAddress.hashCode());
        result = prime * result + ((nodeSN == null) ? 0 : nodeSN.hashCode());
        result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
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
        ParentData other = (ParentData) obj;
        if (nodeMacAddress == null) {
            if (other.nodeMacAddress != null)
                return false;
        } else if (!nodeMacAddress.equals(other.nodeMacAddress))
            return false;
        if (nodeSN == null) {
            if (other.nodeSN != null)
                return false;
        } else if (!nodeSN.equals(other.nodeSN))
            return false;
        if (rfnIdentifier == null) {
            if (other.rfnIdentifier != null)
                return false;
        } else if (!rfnIdentifier.equals(other.rfnIdentifier))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return String
            .format("ParentData [rfnIdentifier=%s, nodeSN=%s, nodeMacAddress=%s]",
                    rfnIdentifier,
                    nodeSN,
                    nodeMacAddress);
    }

}
