package com.cannontech.amr.rfn.message.disconnect;

import java.io.Serializable;


public class RfnMeterDisconnectInitialReply implements Serializable {

    private static final long serialVersionUID = 1L;
    private RfnMeterDisconnectInitialReplyType replyType = RfnMeterDisconnectInitialReplyType.TIMEOUT;

    public boolean isSuccess() {
        return replyType == RfnMeterDisconnectInitialReplyType.OK;
    }

    public void setReplyType(RfnMeterDisconnectInitialReplyType replyType) {
        this.replyType = replyType;
    }

    public RfnMeterDisconnectInitialReplyType getReplyType() {
        return replyType;
    }

    @Override
    public String toString() {
        return "RfnMeterDisconnectInitialReply [replyType=" + replyType + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((replyType == null) ? 0 : replyType.hashCode());
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
        RfnMeterDisconnectInitialReply other = (RfnMeterDisconnectInitialReply) obj;
        if (replyType == null) {
            if (other.replyType != null)
                return false;
        } else if (!replyType.equals(other.replyType))
            return false;
        return true;
    }

}