package com.cannontech.stars.dr.optout.exception;

public abstract class OptOutException extends RuntimeException {

    private String errorCode;

    public OptOutException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    @Override
    public abstract String getMessage();

    public String getErrorCode() {
        return errorCode;
    }

}
