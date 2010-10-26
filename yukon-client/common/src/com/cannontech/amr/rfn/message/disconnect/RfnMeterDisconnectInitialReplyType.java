package com.cannontech.amr.rfn.message.disconnect;

public enum RfnMeterDisconnectInitialReplyType {
    OK,
    NO_NODE,
    NO_GATEWAY,
    FAILURE,
    TIMEOUT, // Yukon specific timeout
}