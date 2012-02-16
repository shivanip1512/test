package com.cannontech.capcontrol.exception;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.cannontech.capcontrol.creation.CapControlImporterCbcField;

/**
 * This exception is thrown when the user uses the Cap Control File Importer to
 * import a CBC file. If the CSV file that is submitted is missing required 
 * columns, this error is thrown with a description and a collection of the 
 * columns that are missing.
 */
public class CapControlCbcFileImportException extends CapControlFileImporterException {
	
	private Collection<CapControlImporterCbcField> columns;

	private CapControlCbcFileImportException(String message) {
		super(message);
	}
	
	public CapControlCbcFileImportException(String message, Collection<CapControlImporterCbcField> columns) {
		this(message + StringUtils.join(columns, ", "));
		this.columns = columns;
	}
	
	public Collection<CapControlImporterCbcField> getColumns() {
		return columns;
	}
}
