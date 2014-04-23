package com.cannontech.dr.ecobee.service;

public class EcobeeDeviceDoesNotExistException extends EcobeeException {

    public EcobeeDeviceDoesNotExistException(long serialNumber, String message) {
        super("No Ecobee device with serial number " + serialNumber + " was found. " + message);
    }
    
    public EcobeeDeviceDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
