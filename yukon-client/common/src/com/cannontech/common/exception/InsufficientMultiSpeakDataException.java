package com.cannontech.common.exception;

public class InsufficientMultiSpeakDataException extends RuntimeException {

    public InsufficientMultiSpeakDataException(String message) {
        super(message);
    }

    public InsufficientMultiSpeakDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
