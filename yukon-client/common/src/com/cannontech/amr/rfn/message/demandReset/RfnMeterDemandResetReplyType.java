package com.cannontech.amr.rfn.message.demandReset;

public enum RfnMeterDemandResetReplyType {
    OK,
    NO_NODE(1024),
    NO_GATEWAY(1025),
    FAILURE(1026),
    TIMEOUT(1027), // Yukon specific timeout
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
