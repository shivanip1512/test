package com.cannontech.common.rfn.message.network;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

public class RfnParentReply implements RfnIdentifyingMessage, Serializable {

    private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;
    private RfnParentReplyType replyType;
    private ParentData parentData;

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public RfnParentReplyType getReplyType() {
        return replyType;
    }

    public void setReplyType(RfnParentReplyType replyType) {
        this.replyType = replyType;
    }

    public ParentData getParentData() {
        return parentData;
    }

    public void setParentData(ParentData parentData) {
        this.parentData = parentData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((parentData == null) ? 0 : parentData.hashCode());
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
        RfnParentReply other = (RfnParentReply) obj;
        if (parentData == null) {
            if (other.parentData != null)
                return false;
        } else if (!parentData.equals(other.parentData))
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
        return String.format("RfnParentReply [rfnIdentifier=%s, replyType=%s, parentData=%s]",
                             rfnIdentifier,
                             replyType,
                             parentData);
    }

}
