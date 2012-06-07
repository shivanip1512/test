package com.cannontech.stars.dr.optout.exception;

public class InvalidOptOutStartDateException extends OptOutException {

    public InvalidOptOutStartDateException() {
        super("InvalidStartDate");
    }

    @Override
    public String getMessage() {
        return "The provided start date is not valid";
    }

}
