package com.cannontech.amr.rfn.message.read;

public enum ChannelDataStatus {
    OK, //Used by partial and full reads
    PARTIAL_READ_TIMEOUT,
    PARTIAL_READ_FAILURE,
    PARTIAL_READ_LONG,
    FULL_READ_PASSWORD_ERROR,
    FULL_READ_BUSY_ERROR,
    FULL_READ_TIMEOUT_ERROR,
    FULL_READ_PROTOCOL_ERROR,
    FULL_READ_NO_SUCH_CHANNEL_ERROR,
    FULL_READ_READ_RESPONSE_ERROR_UNKNOWN,
    FULL_READ_UNKNOWN,
    FAILURE
    ;
    
    public boolean isOk() {
        return this == OK;
    }
}
