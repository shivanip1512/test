package com.cannontech.amr.crf.message;

public class CrfMeterReadReply {
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