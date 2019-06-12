package com.cannontech.core.dao;

public class HoneywellProcessingException extends RuntimeException {

    public HoneywellProcessingException(String message) {
        super(message);
    }

    public HoneywellProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}
