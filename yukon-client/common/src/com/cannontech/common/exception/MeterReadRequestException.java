package com.cannontech.common.exception;

//this exception class is also recognized by the FriendlyExceptionResolver
public class MeterReadRequestException extends RuntimeException {
    
    private static final String defaultMsg = "Unable to send read request to meter.";

    public MeterReadRequestException() {
        // generic error msg
        super(defaultMsg);
    }
    
    public MeterReadRequestException(Throwable cause) {
        super(defaultMsg, cause);
    }
}
