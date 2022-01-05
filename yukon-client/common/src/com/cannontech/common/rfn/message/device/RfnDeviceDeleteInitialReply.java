package com.cannontech.common.rfn.message.device;

import java.io.Serializable;

/**
 * RfnDeviceDeleteInitialReply is sent from NM to Yukon after NM receives a
 * RfnDeviceDeleteRequest from Yukon. The main purpose is to tell Yukon if its
 * request device is in NM database.
 * 
 * JMS queue name: reply-to/temporary of the queue through which
 * RfnDeviceDeleteRequest was sent.
 */

public class RfnDeviceDeleteInitialReply implements Serializable {

    private static final long serialVersionUID = 1L;

    private RfnDeviceDeleteInitialReplyType replyType;

    private String replyMessage; // NM gives out the reason in case of failure.

    public boolean isSuccess() {
        return replyType == RfnDeviceDeleteInitialReplyType.OK;
    }

    public RfnDeviceDeleteInitialReplyType getReplyType() {
        return replyType;
    }

    public void setReplyType(RfnDeviceDeleteInitialReplyType replyType) {
        this.replyType = replyType;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((replyMessage == null) ? 0 : replyMessage.hashCode());
        result = prime * result + ((replyType == null) ? 0 : replyType.hashCode());
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
        RfnDeviceDeleteInitialReply other = (RfnDeviceDeleteInitialReply) obj;
        if (replyMessage == null) {
            if (other.replyMessage != null)
                return false;
        } else if (!replyMessage.equals(other.replyMessage))
            return false;
        if (replyType != other.replyType)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnDeviceDeleteInitialReply [replyType=%s, replyMessage=%s]", replyType, replyMessage);
    }
}
