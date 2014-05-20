package com.cannontech.common.rfn.service;

public enum NetworkManagerError {
    
    NO_NODE(1024),
    NO_GATEWAY(1025),
    FAILURE(1026),
    TIMEOUT(1027), // Yukon specific timeout
    ;

    private final Integer errorCode;

    private NetworkManagerError(int errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
