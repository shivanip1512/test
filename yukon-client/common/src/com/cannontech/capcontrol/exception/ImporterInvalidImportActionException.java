package com.cannontech.capcontrol.exception;

/*
 * This exception is used in the cap control importer when the user attempts to 
 * pass in an invalid import action.
 */
public class ImporterInvalidImportActionException extends CapControlImportException {
    public ImporterInvalidImportActionException(String message) {
        super(message);
    }
}
