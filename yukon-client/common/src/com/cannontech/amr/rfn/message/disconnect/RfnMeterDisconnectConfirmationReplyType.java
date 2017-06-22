package com.cannontech.amr.rfn.message.disconnect;

public enum RfnMeterDisconnectConfirmationReplyType {
    SUCCESS,
    FAILURE,
    FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT,
    FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT,  
    FAILED_UNEXPECTED_STATUS,
    NOT_SUPPORTED,
    NETWORK_TIMEOUT,
    TIMEOUT, // Yukon specific timeout
}