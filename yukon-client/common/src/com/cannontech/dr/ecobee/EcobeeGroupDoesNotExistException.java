package com.cannontech.dr.ecobee;


public class EcobeeGroupDoesNotExistException extends Exception {
    
    public EcobeeGroupDoesNotExistException(String setName) {
        this(setName, "");
    }
    
    public EcobeeGroupDoesNotExistException(String setName, String message) {
        super("No Ecobee management set named " + setName + " was found. " + message);
    }
    
    public EcobeeGroupDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
