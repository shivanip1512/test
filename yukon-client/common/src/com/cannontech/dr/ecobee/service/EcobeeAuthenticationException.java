package com.cannontech.dr.ecobee.service;

public class EcobeeAuthenticationException extends EcobeeException {
    
    public EcobeeAuthenticationException(String userName, String message) {
        super("Ecobee login failed for user: " + userName + ". " + message);
    }
    
    public EcobeeAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
