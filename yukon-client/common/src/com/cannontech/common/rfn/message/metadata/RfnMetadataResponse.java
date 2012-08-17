package com.cannontech.common.rfn.message.metadata;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

public class RfnMetadataResponse implements RfnIdentifyingMessage, Serializable {
    
    private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;
    private RfnMetadataReplyType replyType = RfnMetadataReplyType.TIMEOUT;
    private Map<RfnMetadata, Object> metadata;
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public RfnMetadataReplyType getReplyType() {
        return replyType;
    }
    
    public void setReplyType(RfnMetadataReplyType replyType) {
        this.replyType = replyType;
    }

    public Map<RfnMetadata, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<RfnMetadata, Object> metadata) {
        this.metadata = metadata;
    }
    
    public boolean isSuccess() {
        return replyType == RfnMetadataReplyType.OK;
    }
    
    @Override
    public String toString() {
        return String.format("RfnMetadataResponse [rfnIdentifier=%s, replyType=%s, metadata=%s]", rfnIdentifier, replyType, metadata);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
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
        RfnMetadataResponse other = (RfnMetadataResponse) obj;
        if (metadata == null) {
            if (other.metadata != null)
                return false;
        } else if (!metadata.equals(other.metadata))
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
    
}