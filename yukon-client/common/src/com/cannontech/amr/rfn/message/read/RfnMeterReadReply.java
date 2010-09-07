package com.cannontech.amr.rfn.message.read;

import java.io.Serializable;

/**
 * JMS Queue name: JMSReplyTo of com.cannontech.amr.rfn.message.RfnMeterReadRequest
 */
public class RfnMeterReadReply implements Serializable {

    private static final long serialVersionUID = 1L;
    private RfnMeterReadingReplyType replyType = RfnMeterReadingReplyType.TIMEOUT;

    public boolean isSuccess() {
        return replyType == RfnMeterReadingReplyType.OK;
    }

    public void setReplyType(RfnMeterReadingReplyType replyType) {
        this.replyType = replyType;
    }

    public RfnMeterReadingReplyType getReplyType() {
        return replyType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        RfnMeterReadReply other = (RfnMeterReadReply) obj;
        if (replyType == null) {
            if (other.replyType != null)
                return false;
        } else if (!replyType.equals(other.replyType))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RfnMeterReadReply [replyType=" + replyType + "]";
    }

}