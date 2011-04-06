package com.cannontech.stars.dr.optout.exception;

/**
 * Exception thrown when the device is not opted out and it was expected to be
 */
public class AlreadyOptedOutException extends OptOutException {

    String message;

    public AlreadyOptedOutException(String serialNumber) {
        super("OverrideAlreadyActive");
        message = "An override on device " + serialNumber + " is already active.";
    }
    
    public AlreadyOptedOutException(int inventoryId) {
        super("OverrideAlreadyActive");
        message = "An override on device with id " + inventoryId + " is already active.";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
