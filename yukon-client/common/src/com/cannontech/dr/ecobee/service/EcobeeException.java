package com.cannontech.dr.ecobee.service;

public class EcobeeException extends Exception {
    
    public EcobeeException(String message) {
        super(message);
    }
    
    public EcobeeException(String message, Throwable cause) {
        super(message, cause);
    }
}
