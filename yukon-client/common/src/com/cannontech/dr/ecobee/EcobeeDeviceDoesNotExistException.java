package com.cannontech.dr.ecobee;


public class EcobeeDeviceDoesNotExistException extends Exception {

    public EcobeeDeviceDoesNotExistException(String serialNumber) {
        this(serialNumber, "");
    }
    
    public EcobeeDeviceDoesNotExistException(String serialNumber, String message) {
        super("No Ecobee device with serial number " + serialNumber + " was found. " + message);
    }
    
    public EcobeeDeviceDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
