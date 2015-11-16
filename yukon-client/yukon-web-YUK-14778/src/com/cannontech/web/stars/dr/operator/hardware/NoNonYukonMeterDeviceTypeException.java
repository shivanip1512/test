package com.cannontech.web.stars.dr.operator.hardware;

public class NoNonYukonMeterDeviceTypeException extends RuntimeException {
    public NoNonYukonMeterDeviceTypeException(String message) {
        super(message);
    }

    public NoNonYukonMeterDeviceTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}