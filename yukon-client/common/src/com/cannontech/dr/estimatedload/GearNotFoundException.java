package com.cannontech.dr.estimatedload;

public final class GearNotFoundException extends EstimatedLoadException {

    private final int programId;
    private final int gearNumber;

    public GearNotFoundException(int programId, int gearNumber) {
        this.programId = programId;
        this.gearNumber = gearNumber;
    }

    public int getProgramId() {
        return programId;
    }
    
    public int getGearNumber() {
        return gearNumber;
    }
    
}
