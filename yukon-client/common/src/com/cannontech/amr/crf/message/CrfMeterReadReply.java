package com.cannontech.amr.crf.message;

import java.io.Serializable;

/**
 * JMS Queue name: JMSReplyTo of com.cannontech.amr.crf.message.CrfMeterReadRequest
 */
public class CrfMeterReadReply implements Serializable {

    private static final long serialVersionUID = 1L;
    private CrfMeterReadingReplyType replyType = CrfMeterReadingReplyType.TIMEOUT;

    public boolean isSuccess() {
        return replyType == CrfMeterReadingReplyType.OK;
    }

    public void setReplyType(CrfMeterReadingReplyType replyType) {
        this.replyType = replyType;
    }

    public CrfMeterReadingReplyType getReplyType() {
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
        CrfMeterReadReply other = (CrfMeterReadReply) obj;
        if (replyType == null) {
            if (other.replyType != null)
                return false;
        } else if (!replyType.equals(other.replyType))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CrfMeterReadReply [replyType=" + replyType + "]";
    }

}