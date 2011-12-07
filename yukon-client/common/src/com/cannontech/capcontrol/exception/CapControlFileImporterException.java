package com.cannontech.capcontrol.exception;

/**
 * This exception is the parent of the {@link CapControlCbcFileImportException} and
 * the {@link CapControlHierarchyFileImporterException}. Each of those exceptions
 * formats their list of columns into a string and calls the constructor of this
 * exception with that string.
 */
public class CapControlFileImporterException extends RuntimeException {
	
	protected CapControlFileImporterException(String message) {
		super(message);
	}
}