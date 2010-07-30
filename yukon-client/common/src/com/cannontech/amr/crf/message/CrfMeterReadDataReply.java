package com.cannontech.amr.crf.message;

import java.io.Serializable;

public class CrfMeterReadDataReply implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private CrfMeterReadingReplyType replyType = CrfMeterReadingReplyType.TIMEOUT;
    private CrfMeterReadingData data;
    
    public void setReplyType(CrfMeterReadingReplyType replyType) {
        this.replyType = replyType;
    }
    
    public CrfMeterReadingReplyType getReplyType() {
        return replyType;
    }
    
    public boolean isSuccess() {
        return replyType == CrfMeterReadingReplyType.OK;
    }

    public CrfMeterReadingData getData() {
        return data;
    }

    public void setData(CrfMeterReadingData data) {
        this.data = data;
    }
}