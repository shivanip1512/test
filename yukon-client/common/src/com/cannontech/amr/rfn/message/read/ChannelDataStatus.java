package com.cannontech.amr.rfn.message.read;

public enum ChannelDataStatus {
    OK,
    TIMEOUT,
    ;
    
    public boolean isOk() {
        return this == OK;
    }
}
