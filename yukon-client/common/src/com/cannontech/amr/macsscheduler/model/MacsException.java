package com.cannontech.amr.macsscheduler.model;

public class MacsException extends Exception{
    
    private MACSExceptionType type;
    public enum MACSExceptionType{
        //IOException, no connection to MACS Service
        ERROR,
        //Received no reply from the MACS Service
        NO_REPLY,
        //Received and error message from MACS Service
        PROCESSING_ERROR
    }
    
    public MacsException(MACSExceptionType type, Exception e) {
        super(e);
        this.type = type;
    }

    public MacsException(MACSExceptionType type, String description) {
        super(description);
        this.type = type;
    }
    
    public MACSExceptionType getType() {
        return type;
    }
}
