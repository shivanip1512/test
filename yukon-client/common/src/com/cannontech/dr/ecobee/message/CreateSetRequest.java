package com.cannontech.dr.ecobee.message;

/**
 * Request to creates a new set with the specified name beneath the root set ("/").
 */
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
