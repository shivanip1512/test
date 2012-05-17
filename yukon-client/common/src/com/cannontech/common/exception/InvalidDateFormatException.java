package com.cannontech.common.exception;

public class InvalidDateFormatException extends IllegalArgumentException {

    public InvalidDateFormatException(String message) {
        super(message);
    }

    public InvalidDateFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}