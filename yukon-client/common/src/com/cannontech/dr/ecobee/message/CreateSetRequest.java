package com.cannontech.dr.ecobee.message;

public final class CreateSetRequest {
    private final String operation = "add";
    private final String setName;
    private final String parentPath = "/";
    
    public CreateSetRequest(String setName) {
        this.setName = setName;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getSetName() {
        return setName;
    }
    
    public String getParentPath() {
        return parentPath;
    }
}
