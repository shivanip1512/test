package com.cannontech.common.rfn.message.network;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

public class RfnNeighborDataReply implements RfnIdentifyingMessage, Serializable {
    private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;
    private RfnNeighborDataReplyType replyType;
    private Set<NeighborData> neighborData;

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public RfnNeighborDataReplyType getReplyType() {
        return replyType;
    }

    public void setReplyType(RfnNeighborDataReplyType replyType) {
        this.replyType = replyType;
    }

    public Set<NeighborData> getNeighborData() {
        return neighborData;
    }

    public void setNeighborData(Set<NeighborData> neighborData) {
        this.neighborData = neighborData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((neighborData == null) ? 0 : neighborData.hashCode());
        result = prime * result + ((replyType == null) ? 0 : replyType.hashCode());
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
        RfnNeighborDataReply other = (RfnNeighborDataReply) obj;
        if (neighborData == null) {
            if (other.neighborData != null)
                return false;
        } else if (!neighborData.equals(other.neighborData))
            return false;
        if (replyType != other.replyType)
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
        return String
            .format("RfnNeighborDataReply [rfnIdentifier=%s, replyType=%s, neighborData=%s]",
                    rfnIdentifier,
                    replyType,
                    neighborData);
    }

}
