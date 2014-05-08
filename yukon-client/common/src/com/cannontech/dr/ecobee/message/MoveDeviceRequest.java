package com.cannontech.dr.ecobee.message;

public final class MoveDeviceRequest {
    private final String operation = "assign";
    private final String setPath;
    private final String thermostats;
    
    /**
     * @param setPath A full management set path (not just the set name). Should begin with the root "/".
     */
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
