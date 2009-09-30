package com.cannontech.common.device.commands;


public class RetryStrategy {

    private int retryCount;
    private Integer stopRetryAfterHoursCount;
    private Integer turnOffQueuingAfterRetryCount;
    
    public RetryStrategy(int retryCount, Integer stopRetryAfterHoursCount, Integer turnOffQueuingAfterRetryCount) {
        this.retryCount = retryCount;
        this.stopRetryAfterHoursCount = stopRetryAfterHoursCount;
        this.turnOffQueuingAfterRetryCount = turnOffQueuingAfterRetryCount;
    }
    
    public static RetryStrategy noRetryStrategy() {
        return new RetryStrategy(0, null, null);
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    public Integer getStopRetryAfterHoursCount() {
        return stopRetryAfterHoursCount;
    }
    public Integer getTurnOffQueuingAfterRetryCount() {
        return turnOffQueuingAfterRetryCount;
    }
}
