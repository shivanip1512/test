package com.cannontech.web.support.waterNode.fileUploadDao.impl;

public class BatteryNodeBadIntervalEndException extends RuntimeException{
    String message;
    public BatteryNodeBadIntervalEndException(String intervalStartDate, String intervalEndDate) {
        message = intervalStartDate + " to " + intervalEndDate;
    }

    public String getMessage() {
        return message;
    }
}
