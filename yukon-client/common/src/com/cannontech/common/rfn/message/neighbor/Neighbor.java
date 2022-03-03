package com.cannontech.common.rfn.message.neighbor;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class Neighbor implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier rfnIdentifier; // could be null
    
    private String nodeSerialNumber; // could be null
    
    private NeighborData neighborData; // neighborMacAddress is in NeighborData

    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public String getNodeSerialNumber() {
        return nodeSerialNumber;
    }

    public void setNodeSerialNumber(String nodeSerialNumber) {
        this.nodeSerialNumber = nodeSerialNumber;
    }

    public NeighborData getNeighborData() {
        return neighborData;
    }

    public void setNeighborData(NeighborData neighborData) {
        this.neighborData = neighborData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((neighborData == null) ? 0 : neighborData.hashCode());
        result = prime * result + ((nodeSerialNumber == null) ? 0 : nodeSerialNumber.hashCode());
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
        Neighbor other = (Neighbor) obj;
        if (neighborData == null) {
            if (other.neighborData != null)
                return false;
        } else if (!neighborData.equals(other.neighborData))
            return false;
        if (nodeSerialNumber == null) {
            if (other.nodeSerialNumber != null)
                return false;
        } else if (!nodeSerialNumber.equals(other.nodeSerialNumber))
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
        return String.format("Neighbor [rfnIdentifier=%s, nodeSerialNumber=%s, neighborData=%s]", rfnIdentifier, nodeSerialNumber,
                neighborData);
    }
}
