package com.cannontech.amr.rfn.message.demandReset;

import com.cannontech.common.rfn.service.NetworkManagerError;

public enum RfnMeterDemandResetReplyType {
    OK,
    NO_NODE(NetworkManagerError.NO_NODE.getErrorCode()),
    NO_GATEWAY(NetworkManagerError.NO_GATEWAY.getErrorCode()),
    FAILURE(NetworkManagerError.FAILURE.getErrorCode()),
    TIMEOUT(NetworkManagerError.TIMEOUT.getErrorCode()), // Yukon specific timeout
    ;

    private final Integer errorCode;

    private RfnMeterDemandResetReplyType() {
        errorCode = null;
    }

    private RfnMeterDemandResetReplyType(int errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
