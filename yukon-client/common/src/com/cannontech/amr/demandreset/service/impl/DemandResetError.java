package com.cannontech.amr.demandreset.service.impl;


public enum DemandResetError {
   
    NO_POINT(281),
    NO_TIMESTAMP(282),
    TIMESTAMP_OUT_OF_RANGE(283), 
    ;

    private final Integer errorCode;

    private DemandResetError(int errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
