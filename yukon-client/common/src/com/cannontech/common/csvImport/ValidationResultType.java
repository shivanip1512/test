package com.cannontech.common.csvImport;

/**
 * Represents the possible results of row validation. While there are many specific outcomes,
 * each is broadly considered either successful or failed.
 */
public enum ValidationResultType {
    VALID(true),
    INVALID_NULL(false),
    MISSING_REQUIRED(false),
    MISSING_GROUPED(false),
    MISSING_VALUE_DEPENDENT(false),
    BAD_TYPE(false),
    ;
    
    private boolean isSuccess;
    
    private ValidationResultType(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    
    public boolean isSuccess() {
        return isSuccess;
    }
    
    public boolean isFailed() {
        return !isSuccess;
    }
}
