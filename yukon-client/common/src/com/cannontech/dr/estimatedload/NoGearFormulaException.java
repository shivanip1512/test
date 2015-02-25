package com.cannontech.dr.estimatedload;

public final class NoGearFormulaException extends EstimatedLoadException {
    private final int gearId;
    
    public NoGearFormulaException(int gearId) {
        this.gearId = gearId;
    }
    
    public int getGearId() {
        return gearId;
    }
}
