package com.cannontech.common.device.config.dao;

public class InvalidDeviceTypeException extends Exception {

    public InvalidDeviceTypeException(String string, Throwable cause) {
        super(string,cause);
    }

    public InvalidDeviceTypeException(String string) {
        super(string);
    }
}
