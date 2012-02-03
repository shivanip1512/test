package com.cannontech.capcontrol.exception;

/**
 * This is the general catch-all exception that is used for the Cap
 * Control importer. It is used in cases when the data provided to 
 * the importer is invalid (i.e. invalid/unsupported PaoTypes.) or 
 * required data is missing entirely in the file provided (i.e. the
 * "name" column in a file is present, but the field was left blank
 * for a specific row.) This exception is the parent of the
 * {@link ImporterInvalidPaoTypeException}, {@link ImporterInvalidDisabledValueException},
 * {@link ImporterInvalidImportActionException}, and {@link ImporterInvalidOpStateException}.
 */
public class CapControlImportException extends RuntimeException {
	
	public CapControlImportException(String message) {
		super(message);
	}
}
