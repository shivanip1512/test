package com.cannontech.amr.crf.message;

public enum ChannelDataStatus {
    OK,
    TIMEOUT,
    ;
    
    public boolean isOk() {
        return this == OK;
    }
}
