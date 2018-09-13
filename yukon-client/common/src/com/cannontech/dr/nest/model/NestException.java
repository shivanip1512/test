package com.cannontech.dr.nest.model;


public class NestException extends RuntimeException {
    
    public NestException(String message) {
        super(message);
    }

    public NestException(String message, Throwable cause) {
        super(message, cause);
    }
}