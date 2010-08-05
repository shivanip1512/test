package com.cannontech.amr.crf.message;

import java.io.Serializable;

public class CrfMeterReadReply implements Serializable{
    
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
    
}