package com.cannontech.web.support.waterNode.fileUploadDao.impl;

public class BatteryNodeFileParsingException extends RuntimeException {
    public BatteryNodeFileParsingException(int rowLengthErrors) {
        super(String.valueOf(rowLengthErrors));
    }
}
