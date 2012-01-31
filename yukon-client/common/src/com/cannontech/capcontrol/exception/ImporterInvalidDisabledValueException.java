package com.cannontech.capcontrol.exception;

/*
 * This exception is thrown when the user attempts to import a hierarchy object with 
 * a disabled value that isn't 'Y' or 'N'.
 */
public class ImporterInvalidDisabledValueException extends CapControlImportException {
    
    public ImporterInvalidDisabledValueException(String message) {
        super(message);
    }
}
