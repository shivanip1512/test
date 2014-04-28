package com.cannontech.dr.ecobee;

/**
 * Thrown when a communication problem prevents an Ecobee API query from completing successfully.
 */
public class EcobeeCommunicationException extends EcobeeException {
    
    public EcobeeCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
