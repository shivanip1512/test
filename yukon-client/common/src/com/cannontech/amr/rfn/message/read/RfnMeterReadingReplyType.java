package com.cannontech.amr.rfn.message.read;

public enum RfnMeterReadingReplyType {
    OK,
    NO_NODE,
    NO_GATEWAY,
    FAILURE,
    TIMEOUT, // Yukon specific timeout
}