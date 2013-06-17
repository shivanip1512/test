package com.cannontech.message.util;

public class BadServerResponseException extends RuntimeException {
    private static final long serialVersionUID = -1945592074714506789L;
    
    public BadServerResponseException(String message) {
        super(message);
    }

    public BadServerResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadServerResponseException(Throwable cause) {
        super(cause);
    }
}