package com.cannontech.amr.deviceread.dao;

public enum DeviceAttributeReadErrorType {
    NO_STRATEGY,
    NO_ATTRIBUTE,
    NO_POINT,
    COMMUNICATION(1027),
    UNKNOWN(1026), 
    EXCEPTION(1026),
    ;
    

    private final Integer errorCode;

    private DeviceAttributeReadErrorType() {
        errorCode = null;
    }

    private DeviceAttributeReadErrorType(int errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
