package com.cannontech.dr.ecobee;


public class EcobeeSetDoesNotExistException extends Exception {
    
    public EcobeeSetDoesNotExistException(String setName) {
        this(setName, "");
    }
    
    public EcobeeSetDoesNotExistException(String setName, String message) {
        super("No Ecobee management set named " + setName + " was found. " + message);
    }
    
    public EcobeeSetDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
