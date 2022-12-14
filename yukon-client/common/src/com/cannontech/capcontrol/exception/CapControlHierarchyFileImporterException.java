package com.cannontech.capcontrol.exception;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.capcontrol.creation.CapControlImporterHierarchyField;

/**
 * This exception is thrown when the user uses the Cap Control File Importer to
 * import a hierarchy file. If the CSV file that is submitted is missing required 
 * columns, this error is thrown with a description and a collection of the 
 * columns that are missing.
 */
public class CapControlHierarchyFileImporterException extends CapControlFileImporterException {

	private Collection<CapControlImporterHierarchyField> columns;
	
	private CapControlHierarchyFileImporterException(String message) {
		super(message);
	}
	
	public CapControlHierarchyFileImporterException(String message, Collection<CapControlImporterHierarchyField> columns) {
		this(message + StringUtils.join(columns, ", "));
		this.columns = columns;
	}
	
	public Collection<CapControlImporterHierarchyField> getColumns() {
		return columns;
	}
}
