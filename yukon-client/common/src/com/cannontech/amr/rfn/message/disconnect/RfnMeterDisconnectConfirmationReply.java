package com.cannontech.amr.rfn.message.disconnect;

import java.io.Serializable;

public class RfnMeterDisconnectConfirmationReply implements Serializable {

    private static final long serialVersionUID = 1L;
    private RfnMeterDisconnectConfirmationReplyType replyType = RfnMeterDisconnectConfirmationReplyType.TIMEOUT;
    
    public boolean isSuccess() {
        return replyType == RfnMeterDisconnectConfirmationReplyType.SUCCESS;
    }

    public void setReplyType(RfnMeterDisconnectConfirmationReplyType replyType) {
        this.replyType = replyType;
    }
    
    public RfnMeterDisconnectConfirmationReplyType getReplyType() {
        return replyType;
    }
    
    @Override
    public String toString() {
        return "RfnMeterDisconnectConfirmationReply [replyType=" + replyType + "]";
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
        RfnMeterDisconnectConfirmationReply other = (RfnMeterDisconnectConfirmationReply) obj;
        if (replyType == null) {
            if (other.replyType != null)
                return false;
        } else if (!replyType.equals(other.replyType))
            return false;
        return true;
    }
}