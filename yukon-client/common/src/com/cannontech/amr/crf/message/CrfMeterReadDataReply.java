package com.cannontech.amr.crf.message;

import java.io.Serializable;

/**
 * JMS Queue name: JMSReplyTo of com.cannontech.amr.crf.message.CrfMeterReadRequest
 */
public class CrfMeterReadDataReply implements Serializable {

    private static final long serialVersionUID = 1L;

    private CrfMeterReadingDataReplyType replyType = CrfMeterReadingDataReplyType.TIMEOUT;
    private CrfMeterReadingData data;

    public void setReplyType(CrfMeterReadingDataReplyType replyType) {
        this.replyType = replyType;
    }

    public CrfMeterReadingDataReplyType getReplyType() {
        return replyType;
    }

    public boolean isSuccess() {
        return replyType == CrfMeterReadingDataReplyType.OK;
    }

    public CrfMeterReadingData getData() {
        return data;
    }

    public void setData(CrfMeterReadingData data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
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
        CrfMeterReadDataReply other = (CrfMeterReadDataReply) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (replyType == null) {
            if (other.replyType != null)
                return false;
        } else if (!replyType.equals(other.replyType))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("CrfMeterReadDataReply [data=%s, replyType=%s]", data, replyType);
    }

}