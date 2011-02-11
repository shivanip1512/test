package com.cannontech.stars.dr.optout.exception;

public class CancelOptOutException extends OptOutException {
    
    private String message;
    
    public CancelOptOutException(String message) {
        super("CancelFailed");
        this.message = message;
    }



    @Override
    public String getMessage() {
        return message;
    }
  
}
