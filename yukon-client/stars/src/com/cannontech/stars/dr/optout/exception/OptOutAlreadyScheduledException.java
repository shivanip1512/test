package com.cannontech.stars.dr.optout.exception;

public class OptOutAlreadyScheduledException extends OptOutException {

    private String serialNumber;
    
    public OptOutAlreadyScheduledException(String serialNumber) {
        super("OptOutAlreadyScheduled");
        this.serialNumber = serialNumber;
    }

    @Override
    public String getMessage() {
        return "Device "+ serialNumber + "is already scheduled for an opt out.";
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    

}
