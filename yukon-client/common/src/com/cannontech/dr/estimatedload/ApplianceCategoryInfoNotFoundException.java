package com.cannontech.dr.estimatedload;

public final class ApplianceCategoryInfoNotFoundException extends EstimatedLoadException {
    private final int applianceCategoryId;
    
    public ApplianceCategoryInfoNotFoundException(int applianceCategoryId) {
        this.applianceCategoryId = applianceCategoryId;
    }
    
    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }
    
}
