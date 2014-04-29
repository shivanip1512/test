package com.cannontech.dr.ecobee.message;

public final class MoveDeviceRequest {
    private final String operation = "assign";
    private final String setPath;
    private final String thermostats;
    
    public MoveDeviceRequest(String serialNumber, String setPath) {
        thermostats = serialNumber;
        this.setPath = setPath;
    }
    
    public String getOperation() {
        return operation;
    }

    
    public String getSetPath() {
        return setPath;
    }
    
    public String getThermostats() {
        return thermostats;
    }
}
