package com.cannontech.stars.dr.optout.exception;

import com.cannontech.common.device.commands.impl.CommandCompletionException;

public abstract class OptOutException extends CommandCompletionException {

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
