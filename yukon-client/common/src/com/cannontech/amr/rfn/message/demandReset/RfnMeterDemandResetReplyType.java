package com.cannontech.amr.rfn.message.demandReset;

public enum RfnMeterDemandResetReplyType {
    OK,
    NO_NODE,
    NO_GATEWAY,
    FAILURE,
    TIMEOUT, // Yukon specific timeout
}
