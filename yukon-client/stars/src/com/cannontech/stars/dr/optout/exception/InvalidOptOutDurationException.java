package com.cannontech.stars.dr.optout.exception;

public class InvalidOptOutDurationException extends OptOutException {

    public InvalidOptOutDurationException() {
        super("InvalidOverrideDuration");
    }

    @Override
    public String getMessage() {
        return "The provided duration is not valid.";
    }

}
