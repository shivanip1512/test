package com.cannontech.thirdparty.exception;

public class ZigbeeClusterLibraryException extends Exception {
    
    private ZCLStatus zclStatus;
    
    public ZigbeeClusterLibraryException(ZCLStatus zclStatus) {
        super( zclStatus.getDescription() );
        this.zclStatus = zclStatus;
    }
    
    public ZCLStatus getZCLStatus() {
        return zclStatus;
    }
}
