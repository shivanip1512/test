package com.cannontech.common.rfn.message.device;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * RfnDeviceDeleteConfirmationReply is sent from NM to Yukon after NM deletes a device
 * and its associated node and data.
 * 
 * JMS queue name:
 * com.eaton.eas.yukon.networkmanager.RfnDeviceDeleteConfirmationReply
 */
public class RfnDeviceDeleteConfirmationReply implements RfnIdentifyingMessage, Serializable {

    private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;

    private RfnDeviceDeleteConfirmationReplyType replyType;

    private String replyMessage; // NM tries to give out the reason in case of failure.

    public boolean isSuccess() {
        return replyType == RfnDeviceDeleteConfirmationReplyType.SUCCESS;
    }

    public RfnDeviceDeleteConfirmationReplyType getReplyType() {
        return replyType;
    }

    public void setReplyType(RfnDeviceDeleteConfirmationReplyType replyType) {
        this.replyType = replyType;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((replyMessage == null) ? 0 : replyMessage.hashCode());
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
        RfnDeviceDeleteConfirmationReply other = (RfnDeviceDeleteConfirmationReply) obj;
        if (replyMessage == null) {
            if (other.replyMessage != null)
                return false;
        } else if (!replyMessage.equals(other.replyMessage))
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
        return String.format("RfnDeviceDeleteConfirmationReply [rfnIdentifier=%s, replyType=%s, replyMessage=%s]",
                rfnIdentifier,
                replyType,
                replyMessage);
    }
}
