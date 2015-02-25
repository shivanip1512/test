package com.cannontech.dr.estimatedload;

public final class LmDataNotFoundException extends EstimatedLoadException {

    private final int programId;

    public LmDataNotFoundException(int programId) {
        this.programId = programId;
    }

    public int getProgramId() {
        return programId;
    }
}
