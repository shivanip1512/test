package com.cannontech.amr.rfn.message.read;

import java.io.Serializable;

/**
 * JMS Queue name: JMSReplyTo of com.cannontech.amr.rfn.message.RfnMeterReadRequest
 */
public class RfnMeterReadDataReply implements Serializable {

    private static final long serialVersionUID = 1L;

    private RfnMeterReadingDataReplyType replyType = RfnMeterReadingDataReplyType.TIMEOUT;
    private RfnMeterReadingData data;

    public void setReplyType(RfnMeterReadingDataReplyType replyType) {
        this.replyType = replyType;
    }

    public RfnMeterReadingDataReplyType getReplyType() {
        return replyType;
    }

    public boolean isSuccess() {
        return replyType == RfnMeterReadingDataReplyType.OK;
    }

    public RfnMeterReadingData getData() {
        return data;
    }

    public void setData(RfnMeterReadingData data) {
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
        RfnMeterReadDataReply other = (RfnMeterReadDataReply) obj;
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
        return String.format("RfnMeterReadDataReply [data=%s, replyType=%s]", data, replyType);
    }

}