package com.cannontech.encryption.impl;

public class ItronSecurityException extends Exception {
    
    public ItronSecurityException(String message) {
        super(message);
    }
    
    public ItronSecurityException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
