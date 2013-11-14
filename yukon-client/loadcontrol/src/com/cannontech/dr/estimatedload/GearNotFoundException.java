package com.cannontech.dr.estimatedload;

public final class GearNotFoundException extends EstimatedLoadException {

    private final int programId;

    public GearNotFoundException(int programId) {
        this.programId = programId;
    }

    public int getProgramId() {
        return programId;
    }

}
