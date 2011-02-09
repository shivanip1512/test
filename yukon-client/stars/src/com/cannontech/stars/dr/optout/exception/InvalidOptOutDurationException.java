package com.cannontech.stars.dr.optout.exception;

public class InvalidOptOutDurationException extends OptOutException {

    public InvalidOptOutDurationException() {
        super("InvalidOptOutDuration");
    }

    @Override
    public String getMessage() {
        return "The provided duration is not valid.";
    }

}
