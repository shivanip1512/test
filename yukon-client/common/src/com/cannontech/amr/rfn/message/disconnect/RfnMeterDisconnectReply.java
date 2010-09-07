package com.cannontech.amr.rfn.message.disconnect;

import java.io.Serializable;

public class RfnMeterDisconnectReply implements Serializable {

    private static final long serialVersionUID = 1L;
    private RfnMeterDisconnectReplyType replyType = RfnMeterDisconnectReplyType.TIMEOUT;
    
    public boolean isSuccess() {
        return replyType == RfnMeterDisconnectReplyType.SUCCESS;
    }

    public void setReplyType(RfnMeterDisconnectReplyType replyType) {
        this.replyType = replyType;
    }
    
    public RfnMeterDisconnectReplyType getReplyType() {
        return replyType;
    }
    
    @Override
    public String toString() {
        return "RfnMeterDisconnectReplyType [replyType=" + replyType + "]";
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
        RfnMeterDisconnectReply other = (RfnMeterDisconnectReply) obj;
        if (replyType == null) {
            if (other.replyType != null)
                return false;
        } else if (!replyType.equals(other.replyType))
            return false;
        return true;
    }
}