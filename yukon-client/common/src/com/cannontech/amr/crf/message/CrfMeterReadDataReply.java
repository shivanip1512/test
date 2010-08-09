package com.cannontech.amr.crf.message;

import java.io.Serializable;

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
}