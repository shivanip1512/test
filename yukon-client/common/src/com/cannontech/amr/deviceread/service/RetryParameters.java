package com.cannontech.amr.deviceread.service;

import java.util.Date;

public class RetryParameters {
    private int retryCount;
    private Date stopRetryAfterDate;
    private Integer turnOffQueuingAfterRetryCount;

    public RetryParameters(int retryCount, Date stopRetryAfterDate,
            Integer turnOffQueuingAfterRetryCount) {
        this.retryCount = retryCount;
        this.stopRetryAfterDate = stopRetryAfterDate;
        this.turnOffQueuingAfterRetryCount = turnOffQueuingAfterRetryCount;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public Date getStopRetryAfterDate() {
        return stopRetryAfterDate;
    }

    public void setStopRetryAfterDate(Date stopRetryAfterDate) {
        this.stopRetryAfterDate = stopRetryAfterDate;
    }

    public Integer getTurnOffQueuingAfterRetryCount() {
        return turnOffQueuingAfterRetryCount;
    }

    public void setTurnOffQueuingAfterRetryCount(Integer turnOffQueuingAfterRetryCount) {
        this.turnOffQueuingAfterRetryCount = turnOffQueuingAfterRetryCount;
    }

    public static RetryParameters none() {
        return new RetryParameters(0, null, null);
    }
    
}