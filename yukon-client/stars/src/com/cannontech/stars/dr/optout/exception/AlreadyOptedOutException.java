package com.cannontech.stars.dr.optout.exception;

/**
 * Exception thrown when the device is not opted out and it was expected to be
 */
public class AlreadyOptedOutException extends OptOutException {

    String message;

    public AlreadyOptedOutException(String serialNumber) {
        super("OverrideAlreadyActive");
        message = "Device " + serialNumber + " is already opted out.";
    }
    
    public AlreadyOptedOutException(int inventoryId) {
        super("OverrideAlreadyActive");
        message = "Inventory with id " + inventoryId + " is already opted out";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
