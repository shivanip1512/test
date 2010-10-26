package com.cannontech.amr.rfn.message.disconnect;

public enum RfnMeterDisconnectConfirmationReplyType {
    SUCCESS,
    FAILURE,
    FAILED_UNEXPECTED_STATUS,
    NOT_SUPPORTED,
    NETWORK_TIMEOUT,
    TIMEOUT, // Yukon specific timeout
}