package com.cannontech.common.exception;

public class NoControlPointException extends RuntimeException {

    public NoControlPointException(String message) {
        super(message);
    }

    public NoControlPointException(String message, Throwable cause) {
        super(message, cause);
    }

}
