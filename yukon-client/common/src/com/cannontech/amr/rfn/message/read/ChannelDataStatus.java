package com.cannontech.amr.rfn.message.read;

public enum ChannelDataStatus {
    OK,
    TIMEOUT,
    FAILURE,
    LONG,
    ;
    
    public boolean isOk() {
        return this == OK;
    }
}
