package com.cannontech.web.amr.util.cronExpressionTag;

public class CronException extends RuntimeException {

    private CronExceptionType type;
    
    public CronException(CronExceptionType type, Throwable cause) {
        super(cause);
        this.type = type;
    }
    
    public CronException(CronExceptionType type) {
        this.type = type;
    }
    
    public CronExceptionType getType() {
        return type;
    }
    
}