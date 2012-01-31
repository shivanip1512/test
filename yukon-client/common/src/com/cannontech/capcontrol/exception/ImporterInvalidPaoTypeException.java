package com.cannontech.capcontrol.exception;

/*
 * This exception is thrown when the user attempts to import cap control data with 
 * and invalid PaoType.
 */
public class ImporterInvalidPaoTypeException extends CapControlImportException{
    public ImporterInvalidPaoTypeException(String message) {
        super(message);
    }
}
