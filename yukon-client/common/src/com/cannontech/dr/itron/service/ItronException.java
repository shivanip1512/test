package com.cannontech.dr.itron.service;

public class ItronException extends RuntimeException {

    //comma delimited string of errors received from itron
    private String errors;
    
    public ItronException(String message, String errors) {
        super(message);
        this.errors = errors;
    }
    public ItronException(String message) {
        super(message);
    }

    public ItronException(String message, Throwable cause) {
        super(message, cause);
    }
}