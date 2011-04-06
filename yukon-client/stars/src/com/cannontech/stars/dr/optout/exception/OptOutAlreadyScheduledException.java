package com.cannontech.stars.dr.optout.exception;

public class OptOutAlreadyScheduledException extends OptOutException {

    private String serialNumber;
    
    public OptOutAlreadyScheduledException(String serialNumber) {
        super("OverrideAlreadyScheduled");
        this.serialNumber = serialNumber;
    }

    @Override
    public String getMessage() {
        return "An override on device "+ serialNumber + "is already scheduled.";
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    

}
