package com.cannontech.capcontrol.exception;

/*
 * This exception is thrown in the case that the user enters an invalid operational state
 * on a cap bank hierarchy import or update.
 */
public class ImporterInvalidOpStateException extends CapControlImportException {

    public ImporterInvalidOpStateException(String message) {
        super(message);
    }
}
