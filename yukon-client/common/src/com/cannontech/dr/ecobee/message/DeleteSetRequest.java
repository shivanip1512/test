package com.cannontech.dr.ecobee.message;

public class DeleteSetRequest {
    private final String operation = "remove";
    private final String setPath;
    
    public DeleteSetRequest(String setName) {
        this.setPath = "/" + setName;
    }
    
    public String getSetPath() {
        return setPath;
    }
    
    public String getOperation() {
        return operation;
    }
}
