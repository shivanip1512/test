package com.cannontech.common.exception;

/**
 * This exception is thrown whenever an operation has a problem encoding or decoding data.
 */
public class ParseException extends RuntimeException {

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
