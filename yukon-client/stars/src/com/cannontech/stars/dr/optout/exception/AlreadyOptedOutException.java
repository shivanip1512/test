package com.cannontech.stars.dr.optout.exception;

/**
 * Exception thrown when the device is not opted out and it was expected to be
 */
public class AlreadyOptedOutException extends OptOutException {

    String message;

    public AlreadyOptedOutException(String serialNumber) {
        super("DeviceAlreadyOptedOut");
        message = "Device " + serialNumber + " is already opted out.";
    }
    
    public AlreadyOptedOutException(int inventoryId) {
        super("DeviceAlreadyOptedOut");
        message = "Inventory with id " + inventoryId + " is already opted out";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
