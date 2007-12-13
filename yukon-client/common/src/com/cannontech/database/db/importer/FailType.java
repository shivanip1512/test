package com.cannontech.database.db.importer;

public enum FailType {
    
    FAIL_INVALID_DATA("Invalid Data"), 
    FAIL_COMMUNICATION("Communication Error"), 
    FAIL_DATABASE("Database Problem");
    
    private String errorString;
    
    FailType(String errorString){
        this.errorString = errorString;
    }

    public String getErrorString() {
        return errorString;
    }
}