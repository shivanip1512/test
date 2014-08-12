package com.cannontech.common.rfn.service;

import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetReplyType;

/**
 *  This enum lists error codes from error-code.xml with the category of Network Manager.
 */
public enum NetworkManagerError {
    NO_NODE(RfnMeterDemandResetReplyType.NO_NODE.getErrorCode()),
    NO_GATEWAY(RfnMeterDemandResetReplyType.NO_GATEWAY.getErrorCode()),
    FAILURE(RfnMeterDemandResetReplyType.FAILURE.getErrorCode()),
    TIMEOUT(RfnMeterDemandResetReplyType.TIMEOUT.getErrorCode()), // Yukon specific timeout
    ;

    private final Integer errorCode;

    private NetworkManagerError(int errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
