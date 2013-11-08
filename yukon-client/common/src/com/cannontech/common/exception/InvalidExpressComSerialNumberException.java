package com.cannontech.common.exception;

public final class InvalidExpressComSerialNumberException extends RuntimeException {

    public InvalidExpressComSerialNumberException(String message) {
        super(message);
    }

    public InvalidExpressComSerialNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}
