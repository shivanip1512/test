package com.cannontech.dr.ecobee.message;

/**
 * Request to delete a set. Assumes the set is under the root element ("/").
 */
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
