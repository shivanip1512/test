package com.cannontech.dr.ecobee.message;

public final class RegisterDeviceRequest {
    private final String operation = "register";
    private final String thermostats;
    
    public RegisterDeviceRequest(String serialNumber) {
        thermostats = serialNumber;
    }
    
    public String getThermostats() {
        return thermostats;
    }
    
    public String getOperation() {
        return operation;
    }
}
