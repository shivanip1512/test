package com.cannontech.web.support.waterNode.fileUploadDao.impl;

public class BatteryNodeFileParsingException extends RuntimeException{
    String message;
    public BatteryNodeFileParsingException(int rowLengthErrors) {
        message = String.valueOf(rowLengthErrors);
    }

    public String getMessage() {
        return message;
    }
}
