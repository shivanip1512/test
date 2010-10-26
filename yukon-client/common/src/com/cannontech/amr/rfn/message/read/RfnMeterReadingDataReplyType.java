package com.cannontech.amr.rfn.message.read;

public enum RfnMeterReadingDataReplyType {
    OK,
    FAILURE,
    NETWORK_TIMEOUT,
    TIMEOUT, // Yukon specific timeout
}
