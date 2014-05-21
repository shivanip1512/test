package com.cannontech.dr.ecobee;

/**
 * Thrown when an Ecobee API authentication attempt fails.
 */
public class EcobeeAuthenticationException extends Exception {
    
    public EcobeeAuthenticationException(String userName, String message) {
        super("Ecobee login failed for user: " + userName + ". " + message);
    }
    
    public EcobeeAuthenticationException(String message) {
        super(message);
    }
    
    public EcobeeAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
