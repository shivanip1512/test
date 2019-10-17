package com.cannontech.web.support.waterNode.fileUploadDao.impl;

public class BatteryNodeBadIntervalEndException extends RuntimeException {
    public BatteryNodeBadIntervalEndException(String intervalStartDate, String intervalEndDate) {
        super(intervalStartDate + " to " + intervalEndDate);
    }
}
