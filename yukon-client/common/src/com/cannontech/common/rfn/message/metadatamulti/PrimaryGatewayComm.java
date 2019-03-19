package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class PrimaryGatewayComm implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;
        // Yukon should be able to get gwSN, gwName and ipAddress
        // at Yukon side with the gateway RfnIdentifier, 
    
    private CommStatusType commStatusType;
        // "ready" or "not ready" is per gateway
    
    private long commStatusTimestamp;

    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public CommStatusType getCommStatusType() {
        return commStatusType;
    }

    public void setCommStatusType(CommStatusType commStatusType) {
        this.commStatusType = commStatusType;
    }

    public long getCommStatusTimestamp() {
        return commStatusTimestamp;
    }

    public void setCommStatusTimestamp(long commStatusTimestamp) {
        this.commStatusTimestamp = commStatusTimestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (commStatusTimestamp ^ (commStatusTimestamp >>> 32));
        result = prime * result + ((commStatusType == null) ? 0 : commStatusType.hashCode());
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
        PrimaryGatewayComm other = (PrimaryGatewayComm) obj;
        if (commStatusTimestamp != other.commStatusTimestamp)
            return false;
        if (commStatusType != other.commStatusType)
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
            .format("PrimaryGatewayComm [rfnIdentifier=%s, commStatusType=%s, commStatusTimestamp=%s]",
                    rfnIdentifier,
                    commStatusType,
                    commStatusTimestamp);
    }
}