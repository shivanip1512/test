package com.cannontech.amr.deviceread.dao;

import com.cannontech.common.rfn.service.NetworkManagerError;

public enum DeviceAttributeReadErrorType {
	
    NO_STRATEGY,
    NO_ATTRIBUTE,
    NO_POINT,
    COMMUNICATION(NetworkManagerError.TIMEOUT.getErrorCode()),
    UNKNOWN(NetworkManagerError.FAILURE.getErrorCode()), 
    EXCEPTION(NetworkManagerError.FAILURE.getErrorCode()),
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
