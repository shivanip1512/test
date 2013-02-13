package com.cannontech.common.exception;

public class ParseExiException extends RuntimeException {

    public ParseExiException(String message) {
        super(message);
    }

    public ParseExiException(String message, Throwable cause) {
        super(message, cause);
    }

}
