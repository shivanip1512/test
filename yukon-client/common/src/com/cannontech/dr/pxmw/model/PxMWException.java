package com.cannontech.dr.pxmw.model;

public class PxMWException extends RuntimeException {
    
    public PxMWException(String message) {
        super(message);
    }
    
    public PxMWException(String message, Throwable cause) {
        super(message, cause);
    }
    
}