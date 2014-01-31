package com.cannontech.dr.rfn.service.impl;

public final class RfnLcrSimulatorException extends Exception {

    private final String errorMessage;

    public RfnLcrSimulatorException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    
}
