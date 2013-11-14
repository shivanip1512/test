package com.cannontech.dr.estimatedload;

public final class LmDataNotFoundExceptionException extends EstimatedLoadException {

    private final int programId;

    public LmDataNotFoundExceptionException(int programId) {
        this.programId = programId;
    }

    public int getProgramId() {
        return programId;
    }
}
