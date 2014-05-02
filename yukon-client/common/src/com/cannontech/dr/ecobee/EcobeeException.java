package com.cannontech.dr.ecobee;

/**
 * Abstract base for Ecobee-specific exceptions.
 */
public class EcobeeException extends Exception {
    
    public EcobeeException() { }
    
    public EcobeeException(String message) {
        super(message);
    }
    
    public EcobeeException(String message, Throwable cause) {
        super(message, cause);
    }
}
