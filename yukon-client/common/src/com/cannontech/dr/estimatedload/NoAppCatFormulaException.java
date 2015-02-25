package com.cannontech.dr.estimatedload;

public final class NoAppCatFormulaException extends EstimatedLoadException {
    private final int applianceCategoryId;
    
    public NoAppCatFormulaException(int applianceCategoryId) {
        this.applianceCategoryId = applianceCategoryId;
    }
    
    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }
}
