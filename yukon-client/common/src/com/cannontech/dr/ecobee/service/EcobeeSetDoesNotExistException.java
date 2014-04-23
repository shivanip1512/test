package com.cannontech.dr.ecobee.service;

public class EcobeeSetDoesNotExistException extends EcobeeException {
    
    public EcobeeSetDoesNotExistException(String setName, String message) {
        super("No Ecobee management set named " + setName + " was found. " + message);
    }
    
    public EcobeeSetDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
