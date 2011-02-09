package com.cannontech.stars.dr.optout.exception;

/**
 * Exception thrown when there is no temporary override present
 */
public class NoTemporaryOverrideException extends OptOutException {

    public NoTemporaryOverrideException() {
        super("NoTemporaryOverride");
    }

    @Override
    public String getMessage() {
        return "No temporary override present";
    }

}
