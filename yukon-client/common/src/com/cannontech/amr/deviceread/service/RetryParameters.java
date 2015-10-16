package com.cannontech.amr.deviceread.service;

public class RetryParameters {

    private int queuedTries;
    private int nonQueuedTries;
    private int stopRetryAfterHoursCount;

    public RetryParameters(int queuedTries, int nonQueuedTries, int stopRetryAfterHoursCount) {

        this.queuedTries = queuedTries;
        this.nonQueuedTries = nonQueuedTries;
        this.stopRetryAfterHoursCount = stopRetryAfterHoursCount;
    }

    public int getQueuedTries() {
        return queuedTries;
    }

    public int getNonQueuedTries() {
        return nonQueuedTries;
    }

    public int getStopRetryAfterHoursCount() {
        return stopRetryAfterHoursCount;
    }
}