package com.cannontech.stars.dr.optout.exception;

/**
 * Exception thrown when the device is not opted out and it was expected to be
 */
public class AlreadyOptedOutException extends OptOutException {

    String serialNumber;

    public AlreadyOptedOutException(String serialNumber) {
        super("DeviceAlreadyOptedOut");
        this.serialNumber = serialNumber;
    }

    @Override
    public String getMessage() {
        return "Device " + serialNumber + " is already opted out.";
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

}
