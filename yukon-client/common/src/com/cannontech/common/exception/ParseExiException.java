package com.cannontech.common.exception;

/**
 * This exception is thrown whenever an EXI (Efficient XML Interchange)
 * operation has a problem encoding or decoding EXI data.
 */
public class ParseExiException extends RuntimeException {

    public ParseExiException(String message) {
        super(message);
    }

    public ParseExiException(String message, Throwable cause) {
        super(message, cause);
    }

}
