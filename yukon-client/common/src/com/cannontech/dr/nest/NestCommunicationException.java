package com.cannontech.dr.nest;

public class NestCommunicationException extends RuntimeException {

    public NestCommunicationException(String message) {
        super(message);
    }

    public NestCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
