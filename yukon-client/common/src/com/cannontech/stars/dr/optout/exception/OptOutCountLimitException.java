package com.cannontech.stars.dr.optout.exception;

public class OptOutCountLimitException extends OptOutException {

    private String serialNumber;

    public OptOutCountLimitException(String serialNumber) {
        super("OverrideLimitReached");
        this.serialNumber = serialNumber;
    }

    @Override
    public String getMessage() {
        return "Device " + serialNumber + " has already reached its override limit.";
    }

}
