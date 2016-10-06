package com.cannontech.dr.honeywell;

/**
 * Thrown when a communication problem prevents an Ecobee API query from completing successfully.
 */
public class HoneywellCommunicationException extends RuntimeException {

    public HoneywellCommunicationException(String message) {
        super(message);
    }

    public HoneywellCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
